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

            // Calcula os resultados sequencialmente sem criar novas threads
            BigDecimal result1 = calculatePi(iterations1, "Thread 1");
            BigDecimal result2 = calculatePi(iterations2, "Thread 2");

            // Cria a lista final e envia para o cliente
            List<BigDecimal> results = new ArrayList<>();
            results.add(result1);
            results.add(result2);

            out.writeObject(results);

            System.out.println("Resultados enviados para " + clientSocket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static BigDecimal calculatePi(int iterations, String threadName) {
        // Seu código de cálculo Pi permanece inalterado

        BigDecimal a = BigDecimal.ONE;
        BigDecimal b = BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2)), MathContext.DECIMAL128);
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal x = BigDecimal.ONE;
        BigDecimal y;
        BigDecimal result = BigDecimal.ZERO;

        for (int i = 0; i < iterations; i++) {
            y = a;
            a = a.add(b).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
            b = BigDecimal.valueOf(Math.sqrt(b.multiply(y).doubleValue()));
            t = t.subtract(x.multiply(y.subtract(a).multiply(y.subtract(a))));
            x = x.multiply(BigDecimal.valueOf(2));

            result = a.add(b).multiply(a.add(b)).divide(t.multiply(BigDecimal.valueOf(4)), MathContext.DECIMAL128);
            System.out.println(threadName + " - Iteração " + (i + 1) + ": " + result);
        }

        return result;
    }
}
