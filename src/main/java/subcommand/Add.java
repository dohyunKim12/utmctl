package subcommand;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import config.Constants;
import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;
import util.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static cli.Admin.writeChannel;
import static config.Constants.*;
import static util.ProcessUtils.*;


@CommandLine.Command(name = "add",
        description = "Add srun task to GTM",
        mixinStandardHelpOptions = true
)
public class Add implements Callable<Integer> {
    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
            names = { "-c", "--cpu"},
            paramLabel = "CPU",
            description = "Requested CPU count (default: 1)"
    )
    Integer cpu = 1;

    @CommandLine.Option(
            names = { "-m", "--mem"},
            paramLabel = "MEMORY",
            description = "Requested MEMORY (default: 50MB)",
            converter = MemoryConverter.class
    )
    Integer mem = 50;

    @CommandLine.Option(
            names = {"-L", "--licenses"},
            paramLabel = "LICENSE_TYPE:COUNT",
            description = "LICENSE_TYPE:COUNT Number of license to be need"
    )
    String license;

    @CommandLine.Option(
            names = { "-p", "--partition"},
            paramLabel = "PARTITION",
            description = "SLURM Partition to execute task (default: shared)"
    )
    String partition = "shared";

    @CommandLine.Option(
            names = { "-t", "--timelimit"},
            paramLabel = "TIMELIMIT",
            description = "Timeout of task (MINUTES, default: 1440 min = 24 hours)"
    )
    Integer timelimit = 1440;

    @CommandLine.Option(
            names = { "-tu", "--timeunit"},
            paramLabel = "TIMEUNIT",
            description = "Time unit of time limit (default: MINUTE)",
            converter = TimeUnitConverter.class
    )
    TimeUnit timeUnit = TimeUnit.MINUTES;

    @CommandLine.Option(
            names = { "-d", "--description"},
            arity = "1..*",
            paramLabel = "DESCRIPTION",
            description = "Description of Task\n"
    )
    String[] descriptions = null;

    @CommandLine.Option(
            names = { "-fg", "--foreground"},
            paramLabel = "FOREGROUND",
            description = "Sync terminal for view interactive slurm process output"
    )
    boolean fg = false;

    @CommandLine.Option(
            names = { "-f", "--file"},
            paramLabel = "FILE_PATH",
            description = "File input for add batch task",
            converter = util.PathConverter.class
    )
    Path filePath = null;

    @CommandLine.Parameters(paramLabel = "COMMAND", description = "Command and args to execute")
    List<String> commands = new ArrayList<>();

    @Override
    public Integer call() throws Exception {
        validateOptions();
        // Check utmd running
        if (!test) {
            String utmdPidFilePath = Constants.utmdUserPath + "/tmp/utmd.pid";
            String pid = readPIDFromFile(utmdPidFilePath);
            if(!isUtmdRunning(pid)) return 1;
        }
        if (commands.size() == 0) {
            PrintUtils.printError("No commands to add");
            return 1;
        }

        if (timeUnit != TimeUnit.MINUTES) {
            timelimit = (int) timeUnit.toMinutes(timelimit);
        }

        System.out.println("Trying to put task in GTM ....\n");
        SimpleHttpRequest request = SimpleHttpRequest.create("POST",Constants.gtmServerUrl + "/api/task/add");

        if (filePath != null) {
            // Batch mode
            JsonArray ja = makePayloadFromFile(filePath);
            request.setBody(ja.toString(), ContentType.APPLICATION_JSON);
            Global.getInstance().setCaller(Global.ActionType.ADDBATCH);
            writeChannel(request);
            return 0;
        }

        if (commands == null) {
            PrintUtils.printError("No commands to add");
            return 1;
        }
        if(!license.contains(":")) {
            PrintUtils.printError("Invalid license format. ':' character is missing\n [LICENSE_TYPE:LICENSE_COUNT]");
            return 1;
        }

        String licenseType = license.split(":")[0];
        int licenseCount = Integer.parseInt(license.split(":")[1]);
        String description = descriptions != null ? Arrays.stream(descriptions)
                .collect(Collectors.joining(" ")) : null;
        String dateString = TimeUtils.generateCurrentDateString();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String uuid = timestamp + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        String command = "srun --comment='utm-" + uuid + "' -c " + cpu + " --mem " + mem + " -p " + partition + " -L " + license + " -t " + timelimit +
                " " + String.join(" ", commands);

        // Create env file
        FileUtils.createEnvFile(dateString, uuid);

        // Write Message
        JsonObject body = new JsonObject();
        body.addProperty("user", username);
        body.addProperty("license_type", licenseType);
        body.addProperty("license_count", licenseCount);
        body.addProperty("directory", workingDir);
        body.addProperty("command", command);
        body.addProperty("timelimit", timelimit);
        body.addProperty("description", description);
        body.addProperty("requested_cpu", cpu);
        body.addProperty("requested_mem", mem);
        body.addProperty("partition", partition);
        body.addProperty("uuid", uuid);
        request.setBody(body.toString(), ContentType.APPLICATION_JSON);
        Global.getInstance().setCaller(Global.ActionType.ADD);
        writeChannel(request);

        if (fg) {
            // sync until srun.log EOF
            String dirPath = Constants.utmdCommandsPath + File.separator + dateString + File.separator + uuid;
            String srunLogFilePath = dirPath + File.separator + "srun.log";
            tailFileUntilEOF(srunLogFilePath);
        }

        return 0;
    }

    private void validateOptions() {
        if(filePath == null && (license == null || license.isEmpty())) {
            throw new CommandLine.ParameterException(spec.commandLine(),
                    "Error: Either '--file' (-f) must be specified or '--license' (-L) must be provided.");
        }
    }

    private JsonArray makePayloadFromFile(Path filePath) throws IOException {
        File file = filePath.toFile();
        JsonArray ja = new JsonArray();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null) {
            if(line.trim().isEmpty()) continue;
            JsonObject jo = parseLineToJson(line);
            if (jo == null) {
                throw new RuntimeException("Failed to parse line: " + line);
            }
            ja.add(jo);
        }
        return ja;
    }

    private JsonObject parseLineToJson(String line) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String uuid = timestamp + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        line = line.trim();
        String command = null;
        if (line.startsWith("srun ")) {
            command =  line.substring(5).trim();
        }
        command.replaceAll("-t\\s*\\d+", "").trim();

        command = "srun --comment='utm-" + uuid + "' -t " + timelimit + " " + command;
        Matcher cpuMatcher = CPU_PATTERN.matcher(command);
        int cpu = 1;
        if (cpuMatcher.find()) {
            cpu = Integer.parseInt(cpuMatcher.group(1));
        }
        Matcher licenseMatcher = LICENSE_PATTERN.matcher(command);
        if (!licenseMatcher.find()) {
            PrintUtils.printError("License not found in command: " + command);
            return null;
        }
        String licenseType = licenseMatcher.group(1);
        int licenseCount = Integer.parseInt(licenseMatcher.group(2));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user", username);
        jsonObject.addProperty("license_type", licenseType);
        jsonObject.addProperty("license_count", licenseCount);
        jsonObject.addProperty("directory", workingDir);
        jsonObject.addProperty("command", command);
        jsonObject.addProperty("timelimit", timelimit);
        jsonObject.addProperty("requested_cpu", cpu);
        jsonObject.addProperty("uuid", uuid);
        return jsonObject;
    }

    private void tailFileUntilEOF(String filePath) throws IOException, InterruptedException {
        File file = new File(filePath);

        while (!file.exists()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println("Log file for srun detected: " + filePath);

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            String line;
            while ((line = raf.readLine()) != null) {
                System.out.println(line);
                if (line.trim().equals("===EOF===")) {
                    return;
                }
            }
            while (true) {
                while ((line = raf.readLine()) != null) {
                    System.out.println(line);
                    if (line.trim().equals("===EOF===")) {
                        return;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private boolean isUtmdRunning(String pid) throws IOException {
        if (pid == null) {
            System.out.println(PrintUtils.ANSI_BOLD_RED +
                    "UTMD is not running. Start it using the following command: utmctl start" +
                    PrintUtils.ANSI_RESET);
            return false;
        }
        if (pid.isEmpty()) {
            System.out.println(PrintUtils.ANSI_BOLD_RED +
                    "PID file exists, but no valid PID was found." +
                    PrintUtils.ANSI_RESET);
            return false;
        }
        if (!isProcessRunning(pid)) {
            System.out.println(PrintUtils.ANSI_BOLD_RED +
                    "UTMD not running, check PID: " + pid + " or restart utmd by command: utmctl end & start" +
                    PrintUtils.ANSI_RESET);
            return false;
        }
        return true;
    }
}
