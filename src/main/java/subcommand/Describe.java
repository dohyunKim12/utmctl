package subcommand;

import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "describe",
        description = "Describe task details")
public class Describe implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "Task ID")
    String taskId;
    @Override
    public Integer call() throws Exception {
        System.out.println("Retrieving details for task ID " + taskId + " ....\n");
        SimpleHttpRequest request = SimpleHttpRequest.create("GET", Global.getInstance().getUtmServerUrl() + "/api/task/get/" + taskId);
        Global.getInstance().setCaller(Global.ActionType.DESCRIBE);
        writeChannel(request);

        return 0;
    }
}
