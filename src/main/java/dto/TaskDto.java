package dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {
    public TaskDto() {}
    @SerializedName("id")
    private Integer taskId;
    @SerializedName("job_id")
    private String jobId;
    @SerializedName("user")
    private String username;
    private String description;
    private String partition;
    @SerializedName("license_type")
    private String licenseType;
    @SerializedName("license_count")
    private Integer licenseCount;
    private String license;
    private String directory;
    private String uuid;
    private String command;
    @SerializedName("short_cmd")
    private String shortCmd;
    private Long timelimit;
    @SerializedName("requested_cpu")
    private Integer cpu;
    @SerializedName("requested_mem")
    private Integer mem;
    private String submit;
    private String start;
    private String end;
    private String status;

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
        if (shortCmd == null || shortCmd.equals("null") || shortCmd.isEmpty()) {
            return "-";
        }
        return shortCmd;
    }

    public void setShortCmd(String shortCmd) {
        this.shortCmd = shortCmd;
    }
}