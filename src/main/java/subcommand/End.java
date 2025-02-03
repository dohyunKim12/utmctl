package subcommand;

import config.Constants;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        String utmdPidFilePath = Constants.utmdUserPath + "/tmp/utmd.pid";
        try {
            String pid = readPIDFromFile(utmdPidFilePath);

            if (pid != null && !pid.isEmpty()) {
                boolean isKilled = killProcessByPID(pid);
                if (isKilled) {
                    System.out.println("Process with PID " + pid + " has been killed successfully.");
                } else {
                    System.err.println("Failed to kill process with PID " + pid + ".");
                }
            } else {
                System.err.println("No valid PID found in the file.");
            }
            // delete tmp file (pid)
            File pidFile = new File(utmdPidFilePath);
            Files.write(Paths.get(utmdPidFilePath), new byte[0]);
            boolean deleted = pidFile.delete();
            if (!deleted) {
                System.out.println("Direct deletion failed, trying alternative methods...");
                ProcessBuilder processBuilder = new ProcessBuilder("rm", "-f", utmdPidFilePath);
                processBuilder.start().waitFor();
            }
            System.out.println("PID file deleted successfully: " + utmdPidFilePath);
        } catch (IOException e) {
            System.err.println("Failed to delete PID file: " + e.getMessage());
            return 1;
        } catch (InterruptedException e) {
            System.err.println("Error while killing the process: " + e.getMessage());
            return 1;
        }
        return 0;
    }
}
