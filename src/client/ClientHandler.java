package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            // Envie uma mensagem inicial ao cliente
            writer.println("Conexão estabelecida com o servidor. Iniciando quebra de senha...");

            // Inicie o algoritmo de quebra de senha
            PasswordBreaker passwordBreaker = new PasswordBreaker(reader, writer);
            new Thread(passwordBreaker).start();
            passwordBreaker.run(); // Você também pode optar por iniciar o algoritmo em uma thread separada.

            // A seguir, você pode adicionar lógica para receber e processar mensagens do cliente, se necessário.
            // Exemplo:
            // String clientMessage = reader.readLine();
            // System.out.println("Mensagem do Cliente: " + clientMessage);

        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
