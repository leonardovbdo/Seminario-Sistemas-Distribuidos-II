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

            Thread thread1 = new Thread(() -> calculateAndPrintPi(iterations1, "Thread 1"));
            Thread thread2 = new Thread(() -> calculateAndPrintPi(iterations2, "Thread 2"));

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();

            List<BigDecimal> results = new ArrayList<>();
            results.add(thread1Result);
            results.add(thread2Result);

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
        BigDecimal a = BigDecimal.ONE;
        // 1 dividido pela raíz quadrada de 2
        BigDecimal b = BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2)), MathContext.DECIMAL128); // Precisão de 34 dígitos decimais
        BigDecimal t = new BigDecimal("0.25");
        BigDecimal x = BigDecimal.ONE;
        BigDecimal y;
        BigDecimal result = BigDecimal.ZERO;

        for (int i = 0; i < iterations; i++) {
            // 'y' recebe valor de 'a'
            y = a;
            // 'a' é atualizado para a média e 'a' e 'b'
            a = a.add(b).divide(BigDecimal.valueOf(2), MathContext.DECIMAL128);
            // 'b' é atualizada para a raiz quadrada de 'b' multiplicado por 'y'
            b = BigDecimal.valueOf(Math.sqrt(b.multiply(y).doubleValue()));
            // 't' é atualizado por 'x' multiplicado por 'y' subrtraindo por 'a' multiplicando o valor de 'y' subtraindo por 'a'
            t = t.subtract(x.multiply(y.subtract(a).multiply(y.subtract(a))));
            // 'x' é multiplicado por 2
            x = x.multiply(BigDecimal.valueOf(2));

            // result é atualizado usando a fórmula para a aproximação de π
            result = a.add(b).multiply(a.add(b)).divide(t.multiply(BigDecimal.valueOf(4)), MathContext.DECIMAL128);
            // Impresso a respectiva interação e seu valor
            System.out.println(threadName + " - Iteração " + (i + 1) + ": " + result);
        }

        // Atribuição de valor a respectiva thread
        if (threadName.equals("Thread 1")) {
            thread1Result = result;
        } else {
            thread2Result = result;
        }

        // Resultado final
        System.out.println(threadName + " - Resultado Final: " + result);
    }

}
