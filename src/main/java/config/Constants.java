package config;

public class Constants {
    public static String gtmServerIp = System.getenv("GTM_SERVER_IP");
    public static String gtmServerPort = System.getenv("GTM_SERVER_PORT") == null ? "8023" : System.getenv("GTM_SERVER_PORT");
    public static String utmdBinPath = System.getenv("UTMD_BIN_PATH");
    public static String utmdPythonPath = System.getenv("UTMD_PYTHON_PATH");
    public static String kafkaAddress = System.getenv("KAFKA_ADDRESS") == null ? gtmServerIp + ":9092" : System.getenv("KAFKA_ADDRESS");
    public static String utmdUserPath = System.getenv("HOME") + "/utmd";
    public static String utmdCommandsPath = utmdUserPath + "/commands";
    public static String gtmServerUrl = "http://" + gtmServerIp + ":" + gtmServerPort;
    public static String os = System.getProperty("os.name").toLowerCase();
    public static String username = System.getProperty("user.name").toLowerCase();
}
