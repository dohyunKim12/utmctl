package subcommand;

import com.google.gson.JsonObject;
import config.Constants;
import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;
import util.PrintUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cli.Admin.writeChannel;
import static util.ProcessUtils.*;


@CommandLine.Command(name = "add",
        description = "Add srun task to GTM")
public class Add implements Callable<Integer> {
    @CommandLine.Parameters(
            arity = "0",
            paramLabel = "COMMAND",
            description = "SLURM command with arguments (e.g., 'srun -c2 --mem=20G ...')"
    )
    private String commandsPlaceholder;
    @CommandLine.Unmatched
    private List<String> commands;

    @CommandLine.Option(
            names = { "-l", "--license"},
            required = true,
            paramLabel = "LICENSE_TYPE:COUNT",
            description = "LICENSE_TYPE:COUNT Number of license to be need"
    )
    String license;

    @CommandLine.Option(
            names = { "-t", "--timeout"},
            required = true,
            paramLabel = "TIMEOUT",
            description = "Timeout of task (MINUTES)"
    )
    Integer timeout = 0;

    @CommandLine.Option(
            names = { "-d", "--description"},
            arity = "1..*",
            paramLabel = "DESCRIPTION",
            description = "Description of Task\n"
    )
    String[] descriptions = null;
    @Override
    public Integer call() throws Exception {
        // Check UTMD running
        String utmdPidFilePath = Constants.utmdUserPath + "/tmp/utmd.pid";
        String pid = readPIDFromFile(utmdPidFilePath);
        if(!isUtmdRunning(pid)) return 1;


        System.out.println("Trying to put job in UTM ....\n");
        SimpleHttpRequest request = SimpleHttpRequest.create("POST",Constants.utmServerUrl + "/api/task/add");

        if (commands == null) {
            PrintUtils.printError("No commands to add");
            return 1;
        }
        String command = "srun -t" + timeout + " " + String.join(" ", commands);
        if(!license.contains(":")) {
            PrintUtils.printError("Invalid license format. ':' character is missing\n [LICENSE_TYPE:LICENSE_COUNT]");
            return 1;
        }
        String licenseType = license.split(":")[0];
        int licenseCount = Integer.parseInt(license.split(":")[1]);
        String description = descriptions != null ? Arrays.stream(descriptions)
                .collect(Collectors.joining(" ")) : null;
        String username = System.getProperty("user.name");
        String workingDir = System.getProperty("user.dir");

        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);
        TimeZone defaultTimeZone = TimeZone.getDefault();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(defaultTimeZone);
        String dateString = dateFormat.format(now);

        String timestamp = String.valueOf(currentTimeMillis / 1000);
        String uuid = timestamp + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        Pattern pattern = Pattern.compile("-c(\\d+)");
        Matcher matcher = pattern.matcher(command);
        int cpu = 0;
        if (matcher.find()) {
            cpu = Integer.parseInt(matcher.group(1));
        }

        // Create env file
        String filePath = Constants.utmdCommandsPath + File.separator + dateString + File.separator + uuid + File.separator + ".env";
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (created) {
                System.out.println("Parent directories created successfully.");
            } else {
                System.err.println("Failed to create parent directories.");
            }
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            Map<String, String> env = System.getenv();
            for (Map.Entry<String, String> entry : env.entrySet()) {
                writer.write(entry.getKey() + "='" + entry.getValue().replaceAll("([\\\\\\n\\'\\\"])", "\\\\$1") + "'\n");
            }
            System.out.println("Env values saved in " + filePath + " successfully");
        } catch (IOException e) {
            System.err.println("Error occurred while writing env file " + e.getMessage());
        }

        // Write Message
        JsonObject body = new JsonObject();
        body.addProperty("user", username);
        body.addProperty("license_type", licenseType);
        body.addProperty("license_count", licenseCount);
        body.addProperty("directory", workingDir);
        body.addProperty("command", command);
        body.addProperty("timeout", timeout);
        body.addProperty("description", description);
        body.addProperty("requested_cpu", cpu);
        body.addProperty("uuid", uuid);
        request.setBody(body.toString(), ContentType.APPLICATION_JSON);
        Global.getInstance().setCaller(Global.ActionType.ADD);
        writeChannel(request);

        return 0;
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
