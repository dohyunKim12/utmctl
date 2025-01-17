package subcommand;

import com.google.gson.JsonObject;
import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "add",
        description = "Add task to TM")
public class Add implements Callable<Integer> {
    @CommandLine.Parameters(arity = "1..*", paramLabel = "COMMAND", description = "SLURM command")
    String[] commands;
    @CommandLine.Option(names = { "-l", "--license-count"}, required = true, paramLabel = "LICENSE_COUNT", description = "Number of license to be need")
    Integer licenseCount = 0;
    @CommandLine.Option(names = { "-t", "--timeout"}, required = true, paramLabel = "TIMEOUT", description = "Timeout of task")
    Integer timeout = 0;
    @CommandLine.Option(names = { "-d",
            "--description"}, arity = "1..*", paramLabel = "DESCRIPTION", description = "Description of Task\n" +
            "(delimiter '-')")
    String[] descriptions;
    @Override
    public Integer call() throws Exception {
        System.out.println("Trying to put job in UTM ....");
        SimpleHttpRequest request = SimpleHttpRequest.create("POST", Global.getInstance().getServerUrl() + "/api/item/taskadd");

        String command = Arrays.stream(commands)
                .collect(Collectors.joining(" "));
        String description = Arrays.stream(descriptions)
                .collect(Collectors.joining(" "));
        String username = System.getProperty("user.name");
        String workingDir = System.getProperty("user.dir");

//        srun -c2 --mem=20G xa -c xa_cmd_file -i PVT_sfg_sigrcmin_0p8250v_0p6750v_m40c_NM-C1_D1_L1_MCS00_MCS+
        int cpu = 0;

        JsonObject body = new JsonObject();
        body.addProperty("user", username);
        body.addProperty("license_type", Global.getInstance().getLicenseType());
        body.addProperty("license_count", licenseCount);
        body.addProperty("directory", workingDir);
        body.addProperty("command", command);
        body.addProperty("timeout", timeout);
        body.addProperty("description", description);
        body.addProperty("requested_cpu", cpu);
        request.setBody(body.toString(), ContentType.APPLICATION_JSON);
        writeChannel(request);

        return 0;
    }
}
