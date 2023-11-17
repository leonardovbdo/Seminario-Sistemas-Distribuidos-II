import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class CalculadoraPiClient {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 9876;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            int iterations1 = getNumberOfIterationsFromUser("Digite o número de iterações para a Thread 1: ");
            int iterations2 = getNumberOfIterationsFromUser("Digite o número de iterações para a Thread 2: ");

            out.writeObject(iterations1);
            out.writeObject(iterations2);

            List<BigDecimal> results = (List<BigDecimal>) in.readObject();

            for (int i = 0; i < results.size(); i++) {
                System.out.println("Resultado recebido da Thread " + (i + 1) + ": " + results.get(i));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Loop que continuar até que o usuário insira um número inteiro positivo;
    /*
    Dentro do loop a mensagem do prompt será exibida e o método irá converter a entrada
    do usuário para um número inteiro. Se a entrada não for válida, um erro será exibido com
    uma mensagem. Por fim retornará o número de iterações inserido pelo usuário.
    */
    private static int getNumberOfIterationsFromUser(String prompt) {
        Scanner scanner = new Scanner(System.in);
        int iterations = 0;

        while (iterations <= 0) {
            try {
                System.out.print(prompt);
                iterations = Integer.parseInt(scanner.nextLine());

                if (iterations <= 0) {
                    System.out.println("Por favor, insira um número inteiro positivo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, insira um número inteiro válido.");
            }
        }

        return iterations;
    }
}
