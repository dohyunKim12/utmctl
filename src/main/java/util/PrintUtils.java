package util;

import com.google.gson.*;
import config.Global;
import dto.TaskDto;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class PrintUtils {
    public static String shortCmdRegex = "srun\\b(?:\\s+-\\w+\\s+\\S+)*\\s+(\\S+)";
    public  static Pattern shortCmdPattern = Pattern.compile(shortCmdRegex);

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_MAGENTA = "\u001B[1;95m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BOLD_RED = "\u001B[1;31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_BOLD_BLUE = "\u001B[1;34m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BOLD_GREEN = "\u001B[1;32m";
    public static final String ANSI_BOLD_WHITE = "\u001B[1;37m";
    public static final String ANSI_BOLD_YELLOW = "\033[1;33m";
    public static final String ANSI_BOLD_PURPLE = "\u001B[1;35m";

    public static void print(SimpleHttpResponse response) {
        int statusCode = response.getCode();
        String responseMessage = response.getBodyText();
        Gson gson = new Gson();
        if (statusCode >= 200 && statusCode < 300) {
            switch (Global.getInstance().getCaller()) {
                case ADD:
                    printlnGreen("Command registered. Returned Task ID: " + responseMessage);
                    break;
                case GET:
                    List<TaskDto> taskDtoList = new ArrayList<>();
                    JsonArray ja = JsonParser.parseString(responseMessage).getAsJsonArray();

                    for (JsonElement je : ja) {
                        TaskDto taskDto = gson.fromJson(je, TaskDto.class);
                        taskDtoList.add(taskDto);
                    }
                    TaskPrinter taskPrinter = new TaskPrinter(taskDtoList);
                    taskPrinter.print();
                    break;
                case DESCRIBE:
                    TaskDto taskDto = gson.fromJson(JsonParser.parseString(responseMessage), TaskDto.class);
                    TaskPrinter taskDescriber = new TaskPrinter();
                    taskDescriber.describe(taskDto);
                    break;
                case CANCEL:
                    printlnGreen("Task successfully canceled. " + responseMessage);
                    break;
            }
        } else {
            printError(responseMessage);
        }
    }

    public static String prettyJsonIndent(JsonObject jsonObject, int indent) {
        StringBuilder sb = new StringBuilder();
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        String json = gson.toJson(jsonObject);

        String[] lines = json.split("\n");
        for (String line : lines) {
            sb.append(repeatString(" ", indent)); // Java 1.8 호환 repeat 대체
            String[] parts = line.split(": ");
            if (parts.length == 2) {
                sb.append(ANSI_BOLD_PURPLE);
                sb.append(parts[0]).append(": ");
                sb.append(ANSI_RESET);
                sb.append(parts[1]);
            } else {
                sb.append(line);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    public static String padRightDynamic(String s, int n) {
        return String.format("%-" + (n + 3) + "s", s);
    }
    public static void printlnGreen(String msg) {
        System.out.print(PrintUtils.ANSI_BOLD_GREEN+"Success: "+ PrintUtils.ANSI_RESET);
        System.out.println(msg);
    }
    public static void printError(String msg) {
        System.out.print(PrintUtils.ANSI_BOLD_RED+"Failure: "+ PrintUtils.ANSI_RESET);
        System.out.println(msg);
    }
    public static void printWarn(String msg) {
        System.out.print(PrintUtils.ANSI_BOLD_YELLOW+"Warning: "+ PrintUtils.ANSI_RESET);
        System.out.println(msg);
    }
}
