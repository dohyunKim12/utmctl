package subcommand;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;

import java.util.concurrent.Callable;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "cancel",
        description = "Cancel task from TM or Local")
public class Cancel implements Callable<Integer> {
    @CommandLine.Parameters(arity = "1..*", paramLabel = "JOB_ID_LIST", description = "Job id from UTM add response")
    String[] jobId;
    @Override
    public Integer call() throws Exception {
        System.out.println("Trying to cancel job from UTM ....");
        SimpleHttpRequest request = SimpleHttpRequest.create("POST", Global.getInstance().getServerUrl() + "/api/item/cancel");

        JsonObject body = new JsonObject();
        JsonArray jobIdList = new JsonArray();
        for (String jobId : jobId) {
            jobIdList.add(jobId);
        }
        body.add("jobIdList", jobIdList);
        body.addProperty("user", Global.getInstance().getUsername());
        body.addProperty("license_type", Global.getInstance().getLicenseType());
        request.setBody(body.toString(), ContentType.APPLICATION_JSON);
        writeChannel(request);

        return 0;
    }
}
