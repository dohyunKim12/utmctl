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
                case "start":
                    System.out.println("Need to start utmd before run utmctl add command\n");
                    System.out.println("Utmd watching kafka queue & will execute srun command in each thread simultaneously\n");
                    break;
                case "add":
                    System.out.println("Examples");
                    System.out.println("  # Add all worker status");
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
                case "get":
                    System.out.println("Examples");
                    System.out.println("  # Upload application and not deploy");
                    System.out.println("  upload application | app [file]\n");
                    System.out.println("  # Upload application with specified app name");
                    System.out.println("  upload app -n [app_name] [file]\n");
                    break;
                case "describe":
                    System.out.println("Examples");
                    System.out.println("  # Upload application and not deploy");
                    System.out.println("  upload application | app [file]\n");
                    System.out.println("  # Upload application with specified app name");
                    System.out.println("  upload app -n [app_name] [file]\n");
                    break;
                case "end":
                    System.out.println("Examples");
                    System.out.println("  # Undeploy controller");
                    System.out.println("  undeploy controller | cont [cont_name]\n");
                    break;
                case "version":
                    System.out.println("Display the UTM-client and GTM-server version information\n");
                    break;
            }
        }
        return 0;
    }
}
