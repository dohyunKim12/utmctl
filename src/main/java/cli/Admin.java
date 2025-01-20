package cli;

import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import subcommand.*;
import util.PrintUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


@Command(name = "",
        subcommands = {Manual.class, Version.class, Add.class, Cancel.class, Start.class, End.class}
)
public class Admin {
    static String param = "";
    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Admin());

        CommandLine.Help.ColorScheme colorScheme = new CommandLine.Help.ColorScheme.Builder(CommandLine.Help.Ansi.ON)
                .errors(CommandLine.Help.Ansi.Style.fg_red, CommandLine.Help.Ansi.Style.bold).build();
        cmd.setColorScheme(colorScheme);

        if(args.length == 0) {
            System.out.println("Usage: utmctl command");
            System.exit(0);
        }

        if (Global.getInstance().getServerIp() == null || Global.getInstance().getServerPort() == null) {
            System.out.println("UTM_SERVER_IP or UTM_SERVER_PORT not set. (ENV)");
            System.exit(0);
        }
        if (Global.getInstance().getLicenseType() == null) {
            System.out.println("LICENSE_TYPE not set. (ENV)");
            System.exit(0);
        }

        List<String> helpCmd = new ArrayList<>();
        helpCmd.add("help");
        helpCmd.add("--help");
        helpCmd.add("-help");
        helpCmd.add("--h");
        helpCmd.add("-h");

        if(helpCmd.contains(args[0])) {
            cmd.execute("man");
            System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
            param += args[i] + " ";
        }

        int exitCode = cmd
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(param.split(" "));
        if(exitCode != 0) {
            System.exit(exitCode);
        }
    }

    public static void writeChannel(SimpleHttpRequest request) {
        try (CloseableHttpAsyncClient asyncClient = HttpAsyncClients.createDefault()) {
            asyncClient.start();

            Future<SimpleHttpResponse> future = asyncClient.execute(request, null);

            SimpleHttpResponse response = future.get();
            PrintUtils.print(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
