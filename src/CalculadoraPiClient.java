import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class CalculadoraPiClient {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 9876;

    public static void main(String[] args) {
        // Bloco try-with-resources para garantir que os recursos (socket, streams) sejam fechados corretamente.
        try (Socket socket = new Socket(SERVER_IP, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Solicita ao usuário o número de iterações para a Thread 1 e 2.
            int iterations1 = getNumberOfIterationsFromUser("Digite o número de iterações para a Thread 1: ");
            int iterations2 = getNumberOfIterationsFromUser("Digite o número de iterações para a Thread 2: ");

            // Envia os números de iterações para o servidor.
            out.writeObject(iterations1);
            out.writeObject(iterations2);

            // Recebe os resultados do servidor.
            List<BigDecimal> results = (List<BigDecimal>) in.readObject();

            // Exibe os resultados recebidos.
            for (int i = 0; i < results.size(); i++) {
                System.out.println("Resultado recebido da Thread " + (i + 1) + ": " + results.get(i));
            }

        } catch (IOException | ClassNotFoundException e) {
            // Trata exceções de IO e ClassNotFoundException (para readObject).
            e.printStackTrace();
        }
    }

    /*
    Loop que continua até que o usuário insira um número inteiro positivo.
    Dentro do loop, a mensagem do prompt será exibida e o método irá converter a entrada
    do usuário para um número inteiro. Se a entrada não for válida, um erro será exibido com
    uma mensagem. Por fim, retornará o número de iterações inserido pelo usuário.
    */
    private static int getNumberOfIterationsFromUser(String prompt) {
        Scanner scanner = new Scanner(System.in);
        int iterations = 0;

        while (iterations <= 0) {
            try {
                // Exibe o prompt e lê a entrada do usuário.
                System.out.print(prompt);
                iterations = Integer.parseInt(scanner.nextLine());

                // Se a entrada não for válida, exibe uma mensagem de erro.
                if (iterations <= 0) {
                    System.out.println("Por favor, insira um número inteiro positivo.");
                }
            } catch (NumberFormatException e) {
                // Se a entrada não for um número inteiro válido, exibe uma mensagem de erro.
                System.out.println("Por favor, insira um número inteiro válido.");
            }
        }

        return iterations;
    }
}
