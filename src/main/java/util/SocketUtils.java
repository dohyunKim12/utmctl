package util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.UdsDto;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import static config.Constants.SOCKET_PATH;

public class SocketUtils {
  public static UdsDto sendRequest(String requestMsg) throws IOException {
        File socketFile = new File(SOCKET_PATH);
        try (AFUNIXSocket socket = AFUNIXSocket.newInstance()) {
            socket.connect(new AFUNIXSocketAddress(socketFile));
            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                byte[] messageBytes = requestMsg.getBytes("UTF-8");
                out.writeInt(messageBytes.length);
                out.write(messageBytes);
                out.flush();

                int responseLength = in.readInt(); // blocking
                byte[] responseBytes = new byte[responseLength];
                in.readFully(responseBytes);
                String responseJson = new String(responseBytes, "UTF-8");

                JsonObject jsonResponse = JsonParser.parseString(responseJson).getAsJsonObject();
                int statusCode = jsonResponse.get("status_code").getAsInt();
                String message = jsonResponse.get("message").getAsString();
                JsonObject data = jsonResponse.get("data").getAsJsonObject();

                return new UdsDto(statusCode, message, data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
