package subcommand;

import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "get",
        description = "Get list of user task from UTM")
public class Get implements Callable<Integer> {
    @CommandLine.Parameters(index = "0", description = "user")
    String user;

    @CommandLine.Option(
            names = { "-s", "--status"},
            paramLabel = "STATUS",
            description = "Status to filter task"
    )
    String status;
    @Override
    public Integer call() throws Exception {
        System.out.println("Trying to find task list assigned by user " + user + " from UTM ....");

        String url = Global.getInstance().getUtmServerUrl() + "/api/task/list/" + user;
        if(status != null) {
            url += "?status=" + status;
        }
        SimpleHttpRequest request = SimpleHttpRequest.create("GET", url);
        Global.getInstance().setCaller(Global.ActionType.GET);
        writeChannel(request);

        return 0;
    }
}
