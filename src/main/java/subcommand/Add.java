package subcommand;

import com.google.gson.JsonObject;
import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;
import util.PrintUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "add",
        description = "Add task to TM")
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
            names = { "-l", "--license-count"},
            required = true,
            paramLabel = "LICENSE_COUNT",
            description = "Number of license to be need"
    )
    Integer licenseCount = 0;

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
        System.out.println("Trying to put job in UTM ....");
        SimpleHttpRequest request = SimpleHttpRequest.create("POST", Global.getInstance().getServerUrl() + "/api/item/taskadd");

        if (commands == null) {
            PrintUtils.printError("No commands to add");
            return 1;
        }
        String command = "srun " + String.join(" ", commands + " -t" + timeout);
        String description = descriptions != null ? Arrays.stream(descriptions)
                .collect(Collectors.joining(" ")) : null;
        String username = System.getProperty("user.name");
        String workingDir = System.getProperty("user.dir");
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String uuid = timestamp + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        Pattern pattern = Pattern.compile("-c(\\d+)");
        Matcher matcher = pattern.matcher(command);
        int cpu = 0;
        if (matcher.find()) {
            cpu = Integer.parseInt(matcher.group(1));
        }

        JsonObject body = new JsonObject();
        body.addProperty("user", username);
        body.addProperty("license_type", Global.getInstance().getLicenseType());
        body.addProperty("license_count", licenseCount);
        body.addProperty("directory", workingDir);
        body.addProperty("command", command);
        body.addProperty("timeout", timeout);
        body.addProperty("description", description);
        body.addProperty("requested_cpu", cpu);
        body.addProperty("uuid", uuid);
        request.setBody(body.toString(), ContentType.APPLICATION_JSON);
        writeChannel(request);

        return 0;
    }
}
