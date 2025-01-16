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


@CommandLine.Command(name = "add-test",
        description = "Add task to TM")
public class AddTest implements Callable<Integer> {
//    @CommandLine.Parameters(arity = "1..*", paramLabel = "COMMAND", description = "SLURM command")
//    String[] commands;
//    @CommandLine.Option(names = { "-l", "--license-count"}, required = true, paramLabel = "LICENSE_COUNT", description = "Number of license to be need")
//    Integer licenseCount = 0;
//    @CommandLine.Option(names = { "-t", "--timeout"}, required = true, paramLabel = "TIMEOUT", description = "Timeout of task")
//    Integer timeout = 0;
//    @CommandLine.Option(names = { "-d",
//            "--description"}, paramLabel = "DESCRIPTION", description = "Description of Task\n" +
//            "(delimiter '-')")
//    String description = null;
    @Override
    public Integer call() throws Exception {
//        description = description == null ? null
//                : String.join(" ", description.split("-"));

        System.out.println("Trying to add Task to TaskManager ....");
        SimpleHttpRequest request = SimpleHttpRequest.create("POST", Global.getInstance().getServerUrl() + "/api/item/add");

//        String command = Arrays.stream(commands)
//                .collect(Collectors.joining(" "));
        String username = System.getProperty("user.name");
        String workingDir = System.getProperty("user.dir");

        JsonObject body = new JsonObject();
        body.addProperty("owner", "asdf");
        body.addProperty("pk", 888);
        body.addProperty("estimation", 888);
        body.addProperty("project", 888);
        body.addProperty("command", "qwer");
        body.addProperty("build", 3);
        body.addProperty("channel", "q2124");
        body.addProperty("channel_address", "q2124qwer");

        request.setBody(body.toString(), ContentType.APPLICATION_JSON);
        writeChannel(request);

        return 0;
    }
}
