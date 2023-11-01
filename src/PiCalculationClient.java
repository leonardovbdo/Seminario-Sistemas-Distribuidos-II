import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PiCalculationClient {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1"; // Endereço IP do servidor
        int serverPort = 8888; // Porta em que o servidor está ouvindo

        try {
            Socket clientSocket = new Socket(serverAddress, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Recebe a resposta do servidor
            String response = in.readLine();
            double piPartial = Double.parseDouble(response);

            System.out.println("Resultado parcial de Pi: " + piPartial);

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
