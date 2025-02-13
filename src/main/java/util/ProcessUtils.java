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
    public static boolean isUtmdRunning(String pid) throws IOException {
        if (pid == null) {
            System.out.println(PrintUtils.ANSI_BOLD_RED +
                    "UTMD is not running. Start it using the following command: utmctl start" +
                    PrintUtils.ANSI_RESET);
            return false;
        }
        if (pid.isEmpty()) {
            System.out.println(PrintUtils.ANSI_BOLD_RED +
                    "PID file exists, but no valid PID was found." +
                    PrintUtils.ANSI_RESET);
            return false;
        }
        if (!isProcessRunning(pid)) {
            System.out.println(PrintUtils.ANSI_BOLD_RED +
                    "UTMD not running, check PID: " + pid + " or restart utmd by command: utmctl end & start" +
                    PrintUtils.ANSI_RESET);
            return false;
        }
        return true;
    }
    public static void tailFileUntilEOF(String filePath) throws IOException {
        File file = new File(filePath);

        while (!file.exists()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println("Log file for srun detected: " + filePath);

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            String line;
            while ((line = raf.readLine()) != null) {
                System.out.println(line);
                if (line.trim().equals("===EOF===")) {
                    return;
                }
            }
            while (true) {
                while ((line = raf.readLine()) != null) {
                    System.out.println(line);
                    if (line.trim().equals("===EOF===")) {
                        return;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
