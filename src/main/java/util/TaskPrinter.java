package util;


import config.Global;
import dto.TaskDto;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.PrintUtils.*;

public class TaskPrinter extends TablePrinter {
    private final List<TaskDto> taskList;
    private final Map<String, MethodHandle> cachedGetters = new HashMap<>();

    public TaskPrinter() {
        this.taskList = null;
        initializeGetterCache();
    }

    public TaskPrinter(List<TaskDto> taskList) {
        this.taskList = taskList;
        initializeGetterCache();
    }

    private void initializeGetterCache() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> clazz = TaskDto.class;
            for (String field : Global.getInstance().getFilter()) {
                String getterName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
                MethodHandle getterMethod = lookup.findVirtual(clazz, getterName, MethodType.methodType(clazz.getDeclaredField(field).getType()))
                        .asType(MethodType.methodType(Object.class, clazz));
                cachedGetters.put(field, getterMethod);
            }
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Cannot find Getter method", e);
        }
    }

    @Override
    protected void setMaxLengths() {
        for (TaskDto item : taskList) {
            maxLengths.put("username", Math.max(maxLengths.get("username"), item.getUsername().length()));
            maxLengths.put("shortCmd", Math.max(maxLengths.get("shortCmd"), item.getShortCmd().length()));
            maxLengths.put("license", Math.max(maxLengths.get("license"), item.getLicense().length()));
            maxLengths.put("status", Math.max(maxLengths.get("status"), item.getStatus().length()));
            maxLengths.put("directory", Math.max(maxLengths.get("directory"), item.getDirectory().length()));
        }
    }

    @Override
    protected void setFilteredMaxLengths() throws Throwable {
        for (TaskDto item : taskList) {
            for (String field : Global.getInstance().getFilter()) {
                MethodHandle getterMethod = cachedGetters.get(field);
                Object value = getterMethod.invoke(item);
                int length = String.valueOf(value).length();
                maxLengths.put(field, Math.max(maxLengths.get(field), length));
            }
        }
    }

    @Override
    protected void printHeader() {
        sb.append(ANSI_BOLD_GREEN)
                .append(padRightDynamic("TaskId", maxLengths.get("taskId")))
                .append(padRightDynamic("JobId", maxLengths.get("jobId")))
                .append(padRightDynamic("User", maxLengths.get("username")))
                .append(padRightDynamic("Command", maxLengths.get("shortCmd")))
                .append(padRightDynamic("License", maxLengths.get("license")))
                .append(padRightDynamic("CPU", maxLengths.get("cpu")))
                .append(padRightDynamic("Status", maxLengths.get("status")))
                .append(padRightDynamic("Directory", maxLengths.get("directory")))
                .append(padRightDynamic("SubmittedAt", maxLengths.get("submittedAt")))
                .append(ANSI_RESET).append("\n");
    }

    @Override
    protected void printFilteredHeader() {
        for (String field : Global.getInstance().getFilter()) {
            sb.append(ANSI_BOLD_GREEN)
                    .append(padRightDynamic(field, maxLengths.get(field)));
        }
        sb.append(ANSI_RESET).append("\n");
    }

    @Override
    protected void printData() {
        for (TaskDto item : taskList) {
            sb.append(padRightDynamic(String.valueOf(item.getTaskId()), maxLengths.get("taskId")))
                    .append(padRightDynamic(String.valueOf(item.getJobId()), maxLengths.get("jobId")))
                    .append(padRightDynamic(item.getUsername(), maxLengths.get("username")))
                    .append(padRightDynamic(item.getShortCmd(), maxLengths.get("shortCmd")))
                    .append(padRightDynamic(item.getLicense(), maxLengths.get("license")))
                    .append(padRightDynamic(String.valueOf(item.getCpu()), maxLengths.get("cpu")))
                    .append(padRightDynamic(item.getStatus(), maxLengths.get("status")))
                    .append(padRightDynamic(item.getDirectory(), maxLengths.get("directory")))
                    .append(padRightDynamic(item.getSubmit(), maxLengths.get("submittedAt")))
                    .append("\n");
        }
    }

    @Override
    protected void printFilteredData() throws Throwable {
        for (TaskDto item : taskList) {
            for (String field : Global.getInstance().getFilter()) {
                Object value = cachedGetters.get(field).invoke(item);
                sb.append(padRightDynamic(String.valueOf(value), maxLengths.get(field)));
            }
            sb.append("\n");
        }
    }

    public void describe(TaskDto response) {
        StringBuilder sb = new StringBuilder();
        sb.append(ANSI_BOLD_GREEN + "Task Id : " + ANSI_RESET).append(response.getTaskId()).append("\n");
        sb.append(ANSI_BOLD_GREEN + "Job Id : " + ANSI_RESET).append(response.getJobId()).append("\n");
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
