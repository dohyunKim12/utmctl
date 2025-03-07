package util;

import config.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FileUtils {
    public static String createEnvFile(String dateString, String uuid) {
        String dirPath = Constants.utmdCommandsPath + File.separator + dateString + File.separator + uuid;
        String envFilePath = dirPath + File.separator + ".env";
        File file = new File(envFilePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
            } else {
                System.err.println("Failed to create parent directories.");
                return null;
            }
        }

        try (FileWriter writer = new FileWriter(envFilePath)) {
            Map<String, String> env = System.getenv();
            for (Map.Entry<String, String> entry : env.entrySet()) {
                String escapedValue = entry.getValue().replaceAll("([\\\\\\n\\'\\\"])", "\\\\$1");
                writer.write(entry.getKey() + "='" + escapedValue + "'\n");
            }
            System.out.println("Env values saved in " + envFilePath + " successfully");
            return file.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("Error occurred while writing env file: " + e.getMessage());
            return null;
        }
    }
}
