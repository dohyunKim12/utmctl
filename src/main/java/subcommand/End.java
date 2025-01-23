package subcommand;

import config.Global;
import picocli.CommandLine;

import java.io.*;
import java.util.concurrent.Callable;

import static util.ProcessUtils.killProcessByPID;
import static util.ProcessUtils.readPIDFromFile;


@CommandLine.Command(name = "end",
        description = "End UTM daemon")
public class End implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("Finishing utmd ....");
        // Process terminate (read utmd pid from utmd pid file)
        String utmdPidFilePath = Global.getInstance().getUtmdUserPath() + "/tmp/utmd.pid";
        try {
            String pid = readPIDFromFile(utmdPidFilePath);

            if (pid != null && !pid.isEmpty()) {
                boolean isKilled = killProcessByPID(pid);
                if (isKilled) {
                    System.out.println("Process with PID " + pid + " has been killed successfully.");
                } else {
                    System.out.println("Failed to kill process with PID " + pid + ".");
                }
            } else {
                System.out.println("No valid PID found in the file.");
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Error while killing the process: " + e.getMessage());
        }

        return 0;
    }


}
