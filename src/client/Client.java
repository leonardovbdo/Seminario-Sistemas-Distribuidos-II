package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Receber mensagem inicial do servidor
            String serverMessage = reader.readLine();
            System.out.println("Servidor diz: " + serverMessage);

            // Iniciar o algoritmo de quebra de senha no cliente
            new Thread(new PasswordBreaker(reader, writer)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
