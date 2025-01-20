package subcommand;

import config.Global;
import picocli.CommandLine;

import java.io.*;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "end",
        description = "End UTM daemon")
public class End implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        // svc UTMd end
        System.out.println("Finishing utmd ....");
        // process kill (read utmd pid from utmd pid file)
        String utmdPidFile = "/tmp/utmd.pid";
        try {
            String pid = readPIDFromFile(utmdPidFile);

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
    private static String readPIDFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    private static boolean killProcessByPID(String pid) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("kill", "-9", pid).start();
        int exitCode = process.waitFor();
        return exitCode == 0;
    }
}
