package subcommand;

import config.Global;
import picocli.CommandLine;
import util.PrintUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "start",
        description = "Start UTM daemon")
public class Start implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        // svc UTMd start
        System.out.println("Starting utmd ....");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.environment().put("SERVER_IP", Global.getInstance().getServerIp());
            processBuilder.environment().put("UTM_PORT", Global.getInstance().getUtmPort());
            processBuilder.environment().put("KAFKA_PORT", Global.getInstance().getKafkaPort());
            processBuilder.environment().put("TOPIC_NAME", "utm-" + Global.getInstance().getUsername());
            if (Global.getInstance().getOs().contains("win")) {
                // Windows
                PrintUtils.printError("Windows is not supported on this platform");
                return 1;
            } else {
                // Linux/Mac
                processBuilder.command(
                        "nohup",
                        "sh", "-c", "python3 " + Global.getInstance().getUtmdPath() + " > " + Global.getInstance().getUtmdPath() + "/utmd.log"  + " 2>&1 &"
                );
                processBuilder.directory(new File(Global.getInstance().getUtmdPath()));
                processBuilder.start();
                System.out.println("UTMD started in background");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
