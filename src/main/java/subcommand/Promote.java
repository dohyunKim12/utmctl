package subcommand;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import config.Constants;
import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "promote",
        description = "Adjust task priority. Move task to the front of the queue",
        mixinStandardHelpOptions = true
)
public class Promote implements Callable<Integer> {
    @CommandLine.Parameters(arity = "1..*", split = ",", paramLabel = "TASK_ID_LIST", description = "Task id to promote")
    String[] taskId;
    @Override
    public Integer call() throws Exception {
        System.out.println("Trying to adjust task priority higher ....\n");
        SimpleHttpRequest request = SimpleHttpRequest.create("POST", Constants.gtmServerUrl + "/api/task/promote");

        JsonObject body = new JsonObject();
        JsonArray taskIdList = new JsonArray();
        for (String tid : taskId) {
            taskIdList.add(Integer.parseInt(tid));
        }
        body.add("task_id_list", taskIdList);
        body.addProperty("user", Constants.username);
        request.setBody(body.toString(), ContentType.APPLICATION_JSON);
        Global.getInstance().setCaller(Global.ActionType.PROMOTE);
        writeChannel(request);

        return 0;
    }
}
