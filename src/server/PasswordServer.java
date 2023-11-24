package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

public class PasswordServer {
    private static final int PORT = 12345;
    private static int qtdConexoes = 0;
    private static String TARGET_PASSWORD;
    private static volatile boolean senhaEncontrada = false;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor aguardando conexões...");

            while (!senhaEncontrada) {

                Socket clientSocket1 = serverSocket.accept();
                System.out.println("Conexão estabelecida com cliente 1");
                qtdConexoes++;

                Socket clientSocket2 = serverSocket.accept();
                System.out.println("Conexão estabelecida com cliente 2");
                qtdConexoes++;

                if (qtdConexoes == 2) {

                    TARGET_PASSWORD = generateRandomPassword();
                    System.out.println("Senha gerada: " + TARGET_PASSWORD);

                    PrintWriter writer1 = new PrintWriter(clientSocket1.getOutputStream(), true);
                    writer1.println("Você pode iniciar o algoritmo de quebra de senha.");
                    writer1.println(TARGET_PASSWORD);

                    PrintWriter writer2 = new PrintWriter(clientSocket2.getOutputStream(), true);
                    writer2.println("Você pode iniciar o algoritmo de quebra de senha.");
                    writer2.println(TARGET_PASSWORD);

                    // Envia mensagem para os clientes indicando que podem iniciar o algoritmo
                    writer1.println("Você pode iniciar o algoritmo de quebra de senha.");

                    writer2.println("Você pode iniciar o algoritmo de quebra de senha.");

                    // Aguarda as senhas dos clientes
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
                    String senhaCliente1 = reader1.readLine();
                    System.out.println("Senha recebida do cliente 1: " + senhaCliente1);
                    System.out.println(reader1.readLine());

                    BufferedReader reader2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
                    String senhaCliente2 = reader2.readLine();
                    System.out.println("Senha recebida do cliente 2: " + senhaCliente2);
                    System.out.println(reader2.readLine());

                    // Verifica qual cliente encontrou a senha e responde
                    if (senhaCliente1.equals("Senha não encontrada") && senhaCliente2.equals("Senha não encontrada")) {
                        System.out.println("Nenhum cliente encontrou a senha.");
                    } else if (!senhaCliente1.equals("Senha não encontrada") && senhaCliente2.equals("Senha não encontrada")) {
                        System.out.println("Senha encontrada pelo cliente 1: " + senhaCliente1);
                        senhaEncontrada = true;
                    } else if (senhaCliente1.equals("Senha não encontrada") && !senhaCliente2.equals("Senha não encontrada")) {
                        System.out.println("Senha encontrada pelo cliente 2: " + senhaCliente2);
                        senhaEncontrada = true;
                    } else {
                        System.out.println("Ambos os clientes encontraram a senha. Isso não deveria acontecer.");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomPassword() {
        // Gera uma senha aleatória com 5 caracteres
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            char randomChar = (char) ('a' + random.nextInt(26));
            password.append(randomChar);
        }
        return password.toString();
    }
}
