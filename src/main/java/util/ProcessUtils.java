package util;

import java.io.*;

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
    public static boolean killProcessByPID(String pid) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("kill", pid).start();
        int exitCode = process.waitFor();
        return exitCode == 0;
    }
    public static boolean killForcefullyByPID(String pid) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("kill", "-9", pid).start();
        int exitCode = process.waitFor();
        return exitCode == 0;
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
