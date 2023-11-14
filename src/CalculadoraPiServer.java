import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CalculadoraPiServer {
    private static final int PORT = 9876;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor aguardando conexões...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexão recebida: " + clientSocket);

                new Thread(() -> handleClient(clientSocket)).start();
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

            List<BigDecimal> results = new ArrayList<>();
            results.add(calculatePi(iterations1, "Thread 1"));
            results.add(calculatePi(iterations2, "Thread 2"));

            out.writeObject(results);

            System.out.println("Resultados enviados para " + clientSocket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static BigDecimal calculatePi(int iterations, String threadName) {
        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2)), MathContext.DECIMAL128);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal x = BigDecimal.ONE;
        BigDecimal y;

        for (int i = 0; i < iterations; i++) {
            y = a;
            a = a.add(b).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
            b = BigDecimal.valueOf(Math.sqrt(b.multiply(y).doubleValue()));
            t = t.subtract(x.multiply(y.subtract(a).multiply(y.subtract(a))));
            x = x.multiply(BigDecimal.valueOf(2));

            // Resultado parcial com identificação da thread
            BigDecimal piApproximation = a.add(b).multiply(a.add(b)).divide(t.multiply(BigDecimal.valueOf(4)), MathContext.DECIMAL128);
            System.out.println(threadName + " - Iteração " + (i + 1) + ": " + piApproximation);
        }

        return a.add(b).multiply(a.add(b)).divide(t.multiply(BigDecimal.valueOf(4)), MathContext.DECIMAL128);
    }
}