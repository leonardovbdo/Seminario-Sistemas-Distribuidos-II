import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraPiServer {
    private static final int PORT = 9876;

    private static BigDecimal thread1Result;
    private static BigDecimal thread2Result;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor aguardando conexões...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexão recebida: " + clientSocket);

                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            int iterations1 = (int) in.readObject();
            int iterations2 = (int) in.readObject();

            // Use Thread.join() to wait for both threads to finish before sending results
            Thread thread1 = new Thread(() -> calculateAndPrintPi(iterations1, "Thread 1"));
            Thread thread2 = new Thread(() -> calculateAndPrintPi(iterations2, "Thread 2"));

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();

            // Agora, você pode criar a lista final e enviá-la para o cliente
            List<BigDecimal> results = new ArrayList<>();
            results.add(thread1Result);
            results.add(thread2Result);

            out.writeObject(results);

            System.out.println("Resultados enviados para " + clientSocket);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void calculateAndPrintPi(int iterations, String threadName) {
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2)), MathContext.DECIMAL128);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal x = BigDecimal.ONE;
        BigDecimal y;
        BigDecimal result = BigDecimal.ZERO;  // Adicione essa variável para armazenar temporariamente o resultado da thread

        for (int i = 0; i < iterations; i++) {
            y = a;
            a = a.add(b).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
            b = BigDecimal.valueOf(Math.sqrt(b.multiply(y).doubleValue()));
            t = t.subtract(x.multiply(y.subtract(a).multiply(y.subtract(a))));
            x = x.multiply(BigDecimal.valueOf(2));

            // Resultado parcial com identificação da thread
            result = a.add(b).multiply(a.add(b)).divide(t.multiply(BigDecimal.valueOf(4)), MathContext.DECIMAL128);
            System.out.println(threadName + " - Iteração " + (i + 1) + ": " + result);
        }

        // Atualize a variável de resultado da thread correspondente
        if (threadName.equals("Thread 1")) {
            thread1Result = result;
        } else {
            thread2Result = result;
        }

        System.out.println(threadName + " - Resultado Final: " + result);
    }
}
