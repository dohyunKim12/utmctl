package config;

import java.util.regex.Pattern;

public class Constants {
    public static String gtmServerIp = System.getenv("GTM_SERVER_IP") == null ? null : System.getenv("GTM_SERVER_IP");
    public static String gtmServerPort = System.getenv("GTM_SERVER_PORT") == null ? "8023" : System.getenv("GTM_SERVER_PORT");
    public static String utmdBinPath = System.getenv("UTMD_BIN_PATH");
    public static String utmdPythonPath = System.getenv("UTMD_PYTHON_PATH");
    public static String kafkaAddress = System.getenv("KAFKA_ADDRESS") == null ? gtmServerIp + ":9092" : System.getenv("KAFKA_ADDRESS");
    public static String utmdUserPath = System.getenv("HOME") + "/utmd";
    public static String utmdCommandsPath = utmdUserPath + "/commands";
    public static String gtmServerUrl = "http://" + gtmServerIp + ":" + gtmServerPort;
    public static String os = System.getProperty("os.name").toLowerCase();
    public static String username = System.getProperty("user.name") == null ? System.getenv("USER") : System.getProperty("user.name");
    public static final String SOCKET_PATH = "/tmp/utm_uds_" + username;
    public static final String workingDir = System.getProperty("user.dir");
    public static final boolean test = System.getenv("UTM_TEST") != null && !System.getenv("UTM_TEST").isEmpty();

    public static final Pattern LICENSE_PATTERN = Pattern.compile("-l\\s+(\\S+):(\\d+)");
    public static final Pattern CPU_PATTERN = Pattern.compile("-c\\s*(\\d+)");
}
