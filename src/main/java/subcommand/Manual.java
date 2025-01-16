package subcommand;

import cli.Admin;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.Callable;

import static util.PrintUtils.*;

@CommandLine.Command(name = "man",
        description = "Display manual for use utmctl")
public class Manual implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("utmctl Manual page\n");
        CommandLine self = new CommandLine(new Admin());
        self.usage(new PrintWriter(System.out));

        Map<String, CommandLine> subcommands = self.getCommandSpec().subcommands();

        for(Map.Entry<String, CommandLine> entry : subcommands.entrySet()){
            String subcommand = entry.getKey();
            if (subcommand.equals("man")) continue;
            if (subcommand.equals("help")) break;

            System.out.println("\n" + ANSI_BOLD_WHITE + entry.getKey() + ANSI_RESET );
            entry.getValue().usage(new PrintWriter(System.out));

            switch(subcommand) {
                case "add":
                    System.out.println("Examples");
                    System.out.println("  # List all worker status");
                    System.out.println("  get worker\n");
                    System.out.println("  # List all application status");
                    System.out.println("  get application | app\n");
                    System.out.println("  # List all controller status");
                    System.out.println("  get controller | cont\n");
                    System.out.println("  # List all service status");
                    System.out.println("  get service | svc\n");
                    break;
                case "cancel":
                    System.out.println("Examples");
                    System.out.println("  # Upload application and not deploy");
                    System.out.println("  upload application | app [file]\n");
                    System.out.println("  # Upload application with specified app name");
                    System.out.println("  upload app -n [app_name] [file]\n");
                    break;
                case "start":
                    System.out.println("Examples");
                    System.out.println("  # Deploy application to specified worker-pool");
                    System.out.println("  Deploy application | app [app_name] -p [worker-pool]\n");
                    System.out.println("  # Deploy controller to specified worker-pool");
                    System.out.println("  start controller | cont [cont_name] -p [worker-pool]\n");
                    break;
                case "end":
                    System.out.println("Examples");
                    System.out.println("  # Undeploy controller");
                    System.out.println("  undeploy controller | cont [cont_name]\n");
                    break;
            }
        }
        return 0;
    }
}
