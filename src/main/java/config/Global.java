package config;


import java.util.concurrent.ThreadLocalRandom;

public class Global {
    private String serverIp = System.getenv("SERVER_IP");
    private String utmdPath = System.getenv("UTMD_PATH");
    private String utmPort = System.getenv("UTM_PORT") == null ? "8023" : System.getenv("UTM_PORT");
    private String kafkaPort = System.getenv("KAFKA_PORT") == null ? "9092" : System.getenv("KAFKA_PORT");
    private String utmServerUrl = "http://" + serverIp + ":" + utmPort;
    private ClassLoader cl = Thread.currentThread().getContextClassLoader();
    private String os = System.getProperty("os.name").toLowerCase();
    private String username = System.getProperty("user.name").toLowerCase();
    private boolean errorFlag = false;
    private ActionType caller;

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getUtmPort() {
        return utmPort;
    }

    public void setUtmPort(String utmPort) {
        this.utmPort = utmPort;
    }

    public String getUtmServerUrl() {
        return utmServerUrl;
    }

    public void setUtmServerUrl(String utmServerUrl) {
        this.utmServerUrl = utmServerUrl;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKafkaPort() {
        return kafkaPort;
    }

    public void setKafkaPort(String kafkaPort) {
        this.kafkaPort = kafkaPort;
    }

    public ActionType getCaller() {
        return caller;
    }

    public void setCaller(ActionType caller) {
        this.caller = caller;
    }

    private static class Holder {
        public static final Global instance = new Global();
    }

    public static Global getInstance() {
        return Holder.instance;
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

    public enum ActionType {
        GET(1),
        ADD(2),
        CANCEL(3),
        START(4),
        END(5),
        DESCRIBE(6);

        private final int value;
        ActionType(int value) { this.value = value; }
        public int getValue() { return value; }

        public static ActionType fromValue(int value) {
            for (ActionType action : ActionType.values()) {
                if (action.value == value) {
                    return action;
                }
            }
            throw new IllegalArgumentException("No enum constant with value: " + value);
        }
    }
}
