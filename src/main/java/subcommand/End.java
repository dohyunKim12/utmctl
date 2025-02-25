package subcommand;

import config.Constants;
import dto.UdsDto;
import picocli.CommandLine;
import util.SocketUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static util.ProcessUtils.*;


@CommandLine.Command(name = "end",
        description = "Terminate the UTM daemon\n" +
                "This command stops the 'utmd' process and deletes the 'utmd.pid' file"
)
public class End implements Callable<Integer> {
    @CommandLine.Option(
            names = {"-f", "--force"},
            paramLabel = "FORCE",
            description = "Forcefully terminate UTM daemon (default: current user)"
    )
    boolean force = false;

    @Override
    public Integer call() throws Exception {
        try {
            String utmdPidFilePath = Constants.utmdUserPath + "/tmp/utmd.pid";
            String pid = readPIDFromFile(utmdPidFilePath);
            if (pid == null || pid.isEmpty()) {
                System.out.println("No PID found in the file.");
                deleteFile(utmdPidFilePath);
                return 0;
            }
            if(!isProcessRunning(pid)) {
                System.out.println("UTMD is not running.");
                deleteFile(utmdPidFilePath);
                return 0;
            }

            if (!force) {
                // if utmd subprocesses running, return
                UdsDto udsDto = SocketUtils.sendRequest("count_running");
                if (udsDto.getStatusCode() != 200) {
                    System.out.println("Failed to get running process count from utmd.");
                    System.out.println("Status code: " + udsDto.getStatusCode());
                    System.out.println("Response msg: " + udsDto.getMessage());
                    System.out.println("Data: " + udsDto.getData().getAsJsonObject().getAsString());
                    return 0;
                }
                int runningCnt = udsDto.getData().getAsJsonObject().get("result").getAsInt();
                if (runningCnt > 0) {
                    System.out.println("There are " + runningCnt + " running tasks in utmd.");
                    System.out.println("Please wait or end with --force option.");
                    return 0;
                }
            }

            System.out.println("Terminating utmd process with PID " + pid + " ....");
            boolean isKilled = TerminateProcessByPID(pid, 10);
            if (isKilled) {
                System.out.println("Process with PID " + pid + " has been terminated successfully.");
            } else {
                System.err.println("Failed to terminate process with PID " + pid + ".");
                System.out.println("Kill process forcefully with PID " + pid + ".");
                killForcefullyByPID(pid);
            }
            deleteFile(utmdPidFilePath);
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
