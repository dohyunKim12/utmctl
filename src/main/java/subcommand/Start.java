package subcommand;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import static cli.Admin.writeChannel;


@CommandLine.Command(name = "start",
        description = "Start UTM daemon")
public class Start implements Callable<Integer> {
    @CommandLine.Option(names = { "-f",
            "--file" }, paramLabel = "FILE", description = "Config env file path for SRUN\n")
    String file = null;
    @CommandLine.Option(names = { "-fg",
            "--foreground" }, paramLabel = "FOREGROUND", description = "Run UTMCTL foreground\n")
    Boolean foreground = false;
    @Override
    public Integer call() throws Exception {
        // svc UTMd start
        System.out.println("Starting utmd ....");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (Global.getInstance().getOs().contains("win")) {
                // Windows
                processBuilder.command("cmd.exe", "/c", Global.getInstance().getUtmdPath());
            } else {
                // Linux/Mac
                processBuilder.command("sh", Global.getInstance().getUtmdPath());
            }
            Process process = processBuilder.start();

            if(foreground) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line);
                }
                int exitCode = process.waitFor();
                System.out.println("Exit Code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
