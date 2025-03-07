package subcommand;

import config.Constants;
import config.Global;
import config.Global.TaskStatus;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import picocli.CommandLine;
import util.TaskField;
import util.TaskFieldConverter;
import util.TaskStatusConverter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "get",
        description = "Get list of user task from UTM",
        mixinStandardHelpOptions = true
)
public class Get implements Callable<Integer> {
    @CommandLine.Option(
            names = {"-u", "--username"},
            paramLabel = "USERNAME",
            description = "Get task list by username (default: ALL user)"
    )
    String user;

    @CommandLine.Option(
            names = { "-s", "--status"},
            arity = "0..*",
            paramLabel = "STATUS",
            description = "Status to filter task (all | pending | running | cancelled | completed | failed | preempted) \n" +
                    "Multiple values can be separated by comma (,)",
            converter = TaskStatusConverter.class
    )
    List<TaskStatus> statusList = new ArrayList<>(Arrays.asList(TaskStatus.PENDING, TaskStatus.RUNNING));

    @CommandLine.Option(
            names = { "-f", "--format"},
            arity = "0..*",
            paramLabel = "FORMAT",
            description = "Specify field to print out (all | taskId | jobId | username | licenseType | directory | uuid ...) \n" +
                    "Multiple values can be separated by comma (,)",
            converter = TaskFieldConverter.class
    )
    List<String> taskFields = new ArrayList<>(Arrays.asList("taskId", "jobId", "username", "shortCmd", "licenseType",
            "cpu", "status", "directory", "submit"));

    @Override
    public Integer call() throws Exception {
        String url;
        if(user == null) {
            url = Constants.gtmServerUrl + "/api/task/list";
        } else {
            System.out.println("Trying to find task list assigned by user " + user + " from UTM ....\n");
            url = Constants.gtmServerUrl + "/api/task/list/" + user;
        }

        if (statusList.contains(TaskStatus.ALL)) {
            url += "?status=all";
        } else {
            String queryString = statusList.stream()
                    .map(status -> {
                        try {
                            return "status=" + URLEncoder.encode(status.name().toLowerCase(), String.valueOf(StandardCharsets.UTF_8));
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.joining("&"));
            url += "?" + queryString;
        }
        SimpleHttpRequest request = SimpleHttpRequest.create("GET", url);
        Global.getInstance().setCaller(Global.ActionType.GET);
        if (taskFields.contains("all")) {
            Global.getInstance().setFilter(TaskField.getAllFields());
        } else {
            Global.getInstance().setFilter(taskFields);
        }
        writeChannel(request);

        return 0;
    }
}
