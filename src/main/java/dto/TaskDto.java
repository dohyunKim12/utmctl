package dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.regex.Matcher;

import static util.PrintUtils.shortCmdPattern;

public class TaskDto {
    public TaskDto() {}
    @SerializedName("id")
    private int taskId;
    @SerializedName("user")
    private String username;
    private String description;
    @SerializedName("license_type")
    private String licenseType;
    @SerializedName("license_count")
    private int licenseCount;
    private String license;
    private String directory;
    private String uuid;
    private String command;
    @Expose(serialize = false, deserialize = false)
    private String shortCmd;
    private long timeout;
    @SerializedName("requested_cpu")
    private int cpu;
    private String submit;
    private String start;
    private String end;
    private String status;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public int getLicenseCount() {
        return licenseCount;
    }

    public void setLicenseCount(int licenseCount) {
        this.licenseCount = licenseCount;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLicense() {
        if (license == null && licenseType != null) {
            license = licenseType + ":" + licenseCount;
        }
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getShortCmd() {
        if (shortCmd == null && command != null) {
            Matcher matcher = shortCmdPattern.matcher(command);
            if (matcher.find()) {
                shortCmd = matcher.group(1);
            }
        }
        return shortCmd;
    }

    public void setShortCmd(String shortCmd) {
        this.shortCmd = shortCmd;
    }
}