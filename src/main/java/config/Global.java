package config;


import java.util.concurrent.ThreadLocalRandom;

public class Global {
    private String serverIp = System.getenv("UTM_SERVER_IP");
    private String serverPort = System.getenv("UTM_SERVER_PORT");
    private String serverUrl = "http://" + serverIp + ":" + serverPort;
    private ClassLoader cl = Thread.currentThread().getContextClassLoader();
    private String filePath;
    private String caller = "undefined";
    private String licenseType = System.getenv("LICENSE_TYPE");
    private String utmdPath = System.getenv("UTMD_PATH");
    private String os = System.getProperty("os.name").toLowerCase();
    private boolean errorFlag = false;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getUtmdPath() {
        return utmdPath;
    }

    public void setUtmdPath(String utmdPath) {
        this.utmdPath = utmdPath;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    private static class Holder {
        public static final Global instance = new Global();
    }

    public static Global getInstance() {
        return Holder.instance;
    }

    public ClassLoader getCl() {
        return cl;
    }
    public void setCl(ClassLoader cl) {
        this.cl = cl;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getCaller() {
        return caller;
    }
    public void setCaller(String caller) {
        this.caller = caller;
    }
    public boolean isErrorFlag() {
        return errorFlag;
    }
    public void setErrorFlag(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }
    public int randInt() {
        return(ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
    }
}
