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
                    break;
                case "add":
                    System.out.println("Examples");
                    System.out.println("  # Add srun command to the TaskManager");
                    System.out.println("  add srun commands ... \n");
                    System.out.println("  # Add srun command with sleep 10 sec, using 2 license primesim, and timeout in 1 min");
                    System.out.println("  add srun -t1 -l primesim:2 sleep 10\n");
                    System.out.println("  # Add srun command with 2 requested cpu, timeout in 30 min, and other commands");
                    System.out.println("  add srun -c2 -t30 -l primesim:2 sleep 1\n");
                    break;
                case "cancel":
                    System.out.println("Examples");
                    System.out.println("  # Cancel 1 task by task_id, you can retrieve task_id by get command");
                    System.out.println("  cancel 13\n");
                    System.out.println("  # Cancel many task by task_id_list");
                    System.out.println("  cancel 13, 14, 15\n");
                    break;
                case "get":
                    System.out.println("Examples");
                    System.out.println("  # Get task by username");
                    System.out.println("  get username\n");
                    break;
                case "describe":
                    System.out.println("Examples");
                    System.out.println("  # Describe task by task_id");
                    System.out.println("  describe 15\n");
                    break;
                case "end":
                    break;
                case "version":
                    break;
            }
        }
        return 0;
    }
}
