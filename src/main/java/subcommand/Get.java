package subcommand;

import config.Constants;
import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import picocli.CommandLine;
import util.PrintUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "get",
        description = "Get list of user task from UTM",
        mixinStandardHelpOptions = true
)
public class Get implements Callable<Integer> {
    @CommandLine.Option(
            names = {"-u", "--username"},
            paramLabel = "USERNAME",
            description = "Get task list by username (default: current user)"
    )
    String user = null;

    @CommandLine.Option(
            names = { "-s", "--status"},
            paramLabel = "STATUS",
            description = "Status to filter task (pending | running | cancelled | completed)"
    )
    String status;

    @Override
    public Integer call() throws Exception {
        if(user == null)  {
            user = Constants.username;
        }
        System.out.println("Trying to find task list assigned by user " + user + " from UTM ....\n");

        String url = Constants.gtmServerUrl + "/api/task/list/" + user;
        if(status != null) {
            status = status.toLowerCase();
            List<String> statusList = new ArrayList<>();
            statusList.add("pending");
            statusList.add("running");
            statusList.add("cancelled");
            statusList.add("completed");
            if(!statusList.contains(status)) {
                PrintUtils.printError("Invalid status " + status + ". Status must be one of " + statusList);
                return 1;
            }
            url += "?status=" + status;
        }
        SimpleHttpRequest request = SimpleHttpRequest.create("GET", url);
        Global.getInstance().setCaller(Global.ActionType.GET);
        writeChannel(request);

        return 0;
    }
}
