package util;


import dto.TaskDto;

import java.util.List;

import static util.PrintUtils.*;

public class TaskPrinter extends TablePrinter {
    private final List<TaskDto> taskList;

    public TaskPrinter() {
        this.taskList = null;
    }

    public TaskPrinter(List<TaskDto> taskList) {
        this.taskList = taskList;
    }

    @Override
    protected void setMaxLengths() {
        for (TaskDto item : taskList) {
            maxLengths.put("shortCmd", Math.max(maxLengths.get("shortCmd"), item.getShortCmd().length()));
            maxLengths.put("license", Math.max(maxLengths.get("license"), item.getLicense().length()));
            maxLengths.put("status", Math.max(maxLengths.get("status"), item.getStatus().length()));
            maxLengths.put("directory", Math.max(maxLengths.get("directory"), item.getDirectory().length()));
        }
    }

    @Override
    protected void printHeader() {
        sb.append(ANSI_BOLD_GREEN)
                .append(padRightDynamic("TaskId", maxLengths.get("taskId")))
                .append(padRightDynamic("Command", maxLengths.get("shortCmd")))
                .append(padRightDynamic("License", maxLengths.get("license")))
                .append(padRightDynamic("CPU", maxLengths.get("cpu")))
                .append(padRightDynamic("Status", maxLengths.get("status")))
                .append(padRightDynamic("Directory", maxLengths.get("directory")))
                .append(ANSI_RESET).append("\n");
    }

    @Override
    protected void printData() {
        for (TaskDto item : taskList) {
            sb.append(padRightDynamic(String.valueOf(item.getTaskId()), maxLengths.get("taskId")))
                    .append(padRightDynamic(item.getShortCmd(), maxLengths.get("shortCmd")))
                    .append(padRightDynamic(item.getLicense(), maxLengths.get("license")))
                    .append(padRightDynamic(String.valueOf(item.getCpu()), maxLengths.get("cpu")))
                    .append(padRightDynamic(item.getStatus(), maxLengths.get("status")))
                    .append(padRightDynamic(item.getDirectory(), maxLengths.get("directory")))
                    .append("\n");
        }
    }

    public void describe(TaskDto response) {
        StringBuilder sb = new StringBuilder();
        sb.append(ANSI_BOLD_GREEN + "Task Id : " + ANSI_RESET).append(response.getTaskId()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Username : " + ANSI_RESET).append(response.getUsername()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "License Type : " + ANSI_RESET).append(response.getLicenseType()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "License Count : " + ANSI_RESET).append(response.getLicenseCount()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Directory : " + ANSI_RESET).append(response.getDirectory()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "UUID : " + ANSI_RESET).append(response.getUuid()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Command : " + ANSI_RESET).append(response.getCommand()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Timelimit(min) : " + ANSI_RESET).append(response.getTimelimit()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Requested CPU : " + ANSI_RESET).append(response.getCpu()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Submitted At : " + ANSI_RESET).append(response.getSubmit()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Started At : " + ANSI_RESET).append(response.getStart()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Completed At : " + ANSI_RESET).append(response.getEnd()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Status : " + ANSI_RESET).append(response.getStatus()).append("\n");
        System.out.println(sb.toString().replace("null","-"));
    }
}
