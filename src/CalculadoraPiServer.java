import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraPiServer {
    private static final int PORT = 9876;

    // Variáveis para armazenar os resultados das Threads 1 e 2.
    private static BigDecimal thread1Result;
    private static BigDecimal thread2Result;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor aguardando conexões...");

            // Aguarda conexões continuamente.
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexão recebida: " + clientSocket);

                // Lida com a conexão do cliente em uma nova Thread.
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para lidar com a comunicação com o cliente.
    private static void handleClient(Socket clientSocket) {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
                /*
                OBS: O uso de 'ObjectOutputStream' e 'ObjectInputStream' é para realizar a serialização
                de objetos, transformando as informações em bytes para o envio e o processo reverso para
                o receber, reconstruindo as informações a partir da sequência de bytes. Dessa forma, é uma
                maneira ideal para a comunicação entre o cliente e o servidor e sua troca de informações.
                 */
        ) {
            // Lê o número de iterações para as Threads 1 e 2.
            int iterations1 = (int) in.readObject();
            int iterations2 = (int) in.readObject();

            // Cria Threads para calcular π em paralelo.
            Thread thread1 = new Thread(() -> calculateAndPrintPi(iterations1, "Thread 1"));
            Thread thread2 = new Thread(() -> calculateAndPrintPi(iterations2, "Thread 2"));

            // Inicia as Threads.
            thread1.start();
            thread2.start();

            // Aguarda a conclusão das Threads.
            thread1.join();
            thread2.join();

            // Cria uma lista com os resultados das Threads.
            List<BigDecimal> results = new ArrayList<>();
            results.add(thread1Result);
            results.add(thread2Result);

            // Envia os resultados de volta para o cliente.
            out.writeObject(results);

            System.out.println("Resultados enviados para " + clientSocket);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
    Código que realiza uma série de iterações usando cálculos específicos para aproximar os
    dígitos de π. Inspirado no algoritmo de Gauss-Legendre.
     */
    private static void calculateAndPrintPi(int iterations, String threadName) {
        // Variáveis para os cálculos.
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2)), MathContext.DECIMAL128);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal x = BigDecimal.ONE;
        BigDecimal y;
        BigDecimal result = BigDecimal.ZERO;

        // Loop para realizar as iterações.
        for (int i = 0; i < iterations; i++) {
            y = a;
            a = a.add(b).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
            b = BigDecimal.valueOf(Math.sqrt(b.multiply(y).doubleValue()));
            t = t.subtract(x.multiply(y.subtract(a).multiply(y.subtract(a))));
            x = x.multiply(BigDecimal.valueOf(2));

            result = a.add(b).multiply(a.add(b)).divide(t.multiply(BigDecimal.valueOf(4)), MathContext.DECIMAL128);

            // Imprime a iteração atual e seu valor.
            System.out.println(threadName + " - Iteração " + (i + 1) + ": " + result);
        }

        // Atribui o resultado à variável correspondente à Thread.
        if (threadName.equals("Thread 1")) {
            thread1Result = result;
        } else {
            thread2Result = result;
        }

        // Imprime o resultado final da Thread.
        System.out.println(threadName + " - Resultado Final: " + result);
    }
}