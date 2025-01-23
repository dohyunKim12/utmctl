package config;

public class Constants {
    public static String serverIp = System.getenv("SERVER_IP");
    public static String utmdBinPath = System.getenv("UTMD_BIN_PATH");
    public static String utmdUserPath = System.getenv("HOME") + "/utmd";
    public static String utmdCommandsPath = utmdUserPath + "/commands";
    public static String pythonPath = System.getenv("UTMD_PYTHON_PATH");
    public static String utmPort = System.getenv("UTM_PORT") == null ? "8023" : System.getenv("UTM_PORT");
    public static String kafkaPort = System.getenv("KAFKA_PORT") == null ? "9092" : System.getenv("KAFKA_PORT");
    public static String utmServerUrl = "http://" + serverIp + ":" + utmPort;
    public static String os = System.getProperty("os.name").toLowerCase();
    public static String username = System.getProperty("user.name").toLowerCase();
}
