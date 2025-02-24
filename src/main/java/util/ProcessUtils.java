package util;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ProcessUtils {
    public static String readPIDFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
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



    public static boolean TerminateProcessByPID(String pid, int timeoutSeconds) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("kill", pid).start();
        return process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
    }
    public static boolean killForcefullyByPID(String pid) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("kill", "-9", pid).start();
        int result = process.waitFor();
        switch (result) {
            case 0:
                System.out.println("Process with PID " + pid + " has been killed successfully.");
                break;
            case 1:
                System.err.println("Authorization error. Failed to kill process with PID " + pid + ".");
                break;
            case 64:
                System.err.println("Failed to kill process with PID " + pid + ". No such process.");
                break;
            default:
                System.err.println("Unknown error occurred while killing the process with PID " + pid + ".");
                break;
        }
        return result == 0;
    }
    public static boolean isProcessRunning(String pid) throws IOException {
        Process process = new ProcessBuilder("ps", "-p", pid).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(pid)) {
                    return true;
                }
            }
        }
        return false;
    }

}
