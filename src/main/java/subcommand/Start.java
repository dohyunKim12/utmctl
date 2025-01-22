package subcommand;

import config.Global;
import picocli.CommandLine;
import util.PrintUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static util.ProcessUtils.*;


@CommandLine.Command(name = "start",
        description = "Start UTM daemon")
public class Start implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        // svc UTMd start
        if (Global.getInstance().getUtmdPath() == null) {
            System.out.println(PrintUtils.ANSI_BOLD_RED+"Env value 'UTMD_PATH' unset "+ PrintUtils.ANSI_RESET);
        }
        System.out.println("Starting utmd ....\n");
        String utmdPidFilePath = Global.getInstance().getUtmdPath() + "/tmp/utmd.pid";
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
            processBuilder.environment().put("SERVER_IP", Global.getInstance().getServerIp());
            processBuilder.environment().put("UTM_PORT", Global.getInstance().getUtmPort());
            processBuilder.environment().put("KAFKA_PORT", Global.getInstance().getKafkaPort());
            processBuilder.environment().put("TOPIC_NAME", "utm-" + Global.getInstance().getUsername());
            if (Global.getInstance().getOs().contains("win")) {
                // Windows
                PrintUtils.printError("Windows is not supported on this platform");
                return 1;
            } else {
                // Linux/Mac
                processBuilder.command(
                        "nohup",
                        "sh", "-c", Global.getInstance().getPythonPath() + " " + Global.getInstance().getUtmdPath() + "/utmd.py > " + Global.getInstance().getUtmdPath() + "/utmd.log"  + " 2>&1 &"
                );
                processBuilder.directory(new File(Global.getInstance().getUtmdPath()));
                processBuilder.start();
                System.out.println("UTMD started in background");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
