package subcommand;

import config.Constants;
import picocli.CommandLine;
import util.PrintUtils;

import java.io.*;
import java.util.concurrent.Callable;

import static util.ProcessUtils.*;


@CommandLine.Command(name = "start",
        description = "Start UTM daemon\n" +
                "Need to start utmd for each user before running utmctl add command\n" +
                "Utmd watching kafka queue & will execute srun command in each thread simultaneously"
)
public class Start implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        // svc UTMd start
        if (Constants.utmdBinPath == null) {
            System.out.println(PrintUtils.ANSI_BOLD_RED+"Env value 'UTMD_PATH' unset "+ PrintUtils.ANSI_RESET);
        }
        System.out.println("Starting utmd ....\n");
        String utmdPidFilePath = Constants.utmdUserPath + "/tmp/utmd.pid";
        String pid = readPIDFromFile(utmdPidFilePath);
        if (pid != null) {
            if (isProcessRunning(pid)) {
                System.out.println(PrintUtils.ANSI_BOLD_RED+"UTMD Already running, PID: " + pid + PrintUtils.ANSI_RESET);
                return 1;
            } else if (!pid.isEmpty()) {
                boolean isKilled = killForcefullyByPID(pid);
                if (isKilled) {
                    System.out.println("Process with PID " + pid + " has been killed forcefully");
                } else {
                    System.out.println("Failed to kill process with PID " + pid + ".");
                }
            } else {
                System.out.println("No valid PID found in the file.");
            }
            File pidFile = new File(utmdPidFilePath);
            pidFile.delete();
        }

        // Start utmd.py
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.environment().put("GTM_SERVER_IP", Constants.gtmServerIp);
            processBuilder.environment().put("GTM_SERVER_PORT", Constants.gtmServerPort);
            processBuilder.environment().put("KAFKA_ADDRESS", Constants.kafkaAddress);
            processBuilder.environment().put("TOPIC_NAME", "utm-" + Constants.username);
            if (Constants.os.contains("win")) {
                // Windows
                PrintUtils.printError("Windows is not supported on this platform");
                return 1;
            } else {
                // Linux/Mac
                File pidFile = new File(utmdPidFilePath);
                File parentDir = pidFile.getParentFile();
                if (!parentDir.exists()) {
                    boolean dirsCreated = parentDir.mkdirs();
                    if (!dirsCreated) {
                        System.err.println("Failed to create directories: " + parentDir.getAbsolutePath());
                        return 1;
                    }
                }

                processBuilder.command(
                        "sh", "-c",
                        "nohup " + Constants.utmdPythonPath + " " + Constants.utmdBinPath + "/utmd.py > /dev/null 2>&1 & echo $! > " + utmdPidFilePath
                );
                processBuilder.directory(new File(Constants.utmdBinPath));
                processBuilder.start();
                System.out.println("UTMD started successfully. PID will be recorded in: " + utmdPidFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
