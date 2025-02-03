package subcommand;

import config.Constants;
import picocli.CommandLine;
import util.PrintUtils;

import java.io.*;
import java.util.concurrent.Callable;

import static util.ProcessUtils.*;


@CommandLine.Command(name = "start",
        description = "Start UTM daemon")
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
                processBuilder.command(
                        "nohup",
                        "sh", "-c", Constants.utmdPythonPath + " " + Constants.utmdBinPath + "/utmd.py > " + Constants.utmdBinPath + "/utmd.log"  + " 2>&1 & echo $!"
                );
                processBuilder.directory(new File(Constants.utmdBinPath));
                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String pidStr = reader.readLine();
                reader.close();

                if (pidStr != null && pidStr.matches("\\d+")) {
                    long returnedPid = Long.parseLong(pidStr);
                    System.out.println("UTMD started in background with PID: " + returnedPid);

                    File pidFile = new File(utmdPidFilePath);
                    File parentDir = pidFile.getParentFile();
                    if (!parentDir.exists()) {
                        boolean dirsCreated = parentDir.mkdirs();
                        if (!dirsCreated) {
                            System.err.println("Failed to create directories: " + parentDir.getAbsolutePath());
                            return 1;
                        }
                    }
                    try (FileWriter writer = new FileWriter(pidFile, false)) {
                        writer.write(String.valueOf(returnedPid));
                        writer.flush();
                        System.out.println("PID " + returnedPid + " written to " + utmdPidFilePath);
                    } catch (IOException e) {
                        System.err.println("Failed to write PID to file: " + e.getMessage());
                        return 1;
                    }
                } else {
                    System.out.println("Failed to retrieve PID.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
