package subcommand;

import config.Global;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static cli.Admin.writeChannel;

@CommandLine.Command(name = "version",
        description = "Display the UTM-client and UTM-server version information")
public class Version implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        SimpleHttpRequest request = SimpleHttpRequest.create("GET", Global.getInstance().getServerUrl() + "/api/version");
        StringBuilder stb = new StringBuilder();
        try {
            Manifest manifest = this.getManifest();
            if (manifest != null) {
                stb.append("Client Version: (");
                Attributes attribute = manifest.getMainAttributes();
                if (attribute != null) {
                    String version = attribute.getValue("Specification-Version");
                    String javaVersion = System.getProperty("java.version");
                    String osName = System.getProperty("os.name");
                    String osArch = System.getProperty("os.arch");
                    String osVersion = System.getProperty("os.version");

                    if (version != null) {
                        stb.append("Version : ").append(version).append(", ");
                        stb.append("Java : ").append(javaVersion).append(", ");
                        stb.append("Platform : ").append(osName + "/" + osArch + "/" + osVersion);
                    }
                }
                stb.append(")");
            } else {
                stb.append("Client is running on dev mode");
            }
        } catch (Exception e){
            stb = new StringBuilder();
            stb.append("Client exception occurred");
        }
        System.out.println(stb);
        writeChannel(request);
        return 0;
    }

    public static Manifest getManifest() throws Exception {
        String jarFilePath = System.getProperties().getProperty("java.class.path");
        JarFile jarFile = null;
        Manifest manifest = null;
        try {
            jarFile = new JarFile(jarFilePath);
            manifest = jarFile.getManifest();
        } catch (Throwable var12) {
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException var11) {
                    throw var11;
                }
            }
        }
        return manifest;
    }
}
