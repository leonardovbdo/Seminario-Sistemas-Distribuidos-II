import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PiCalculationServer {
    private static final int numDigits = 1000;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Aguardando conexões das máquinas do laboratório...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new PiCalculationWorker(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class PiCalculationWorker implements Runnable {
        private Socket clientSocket;

        PiCalculationWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                int start = 0;
                int end = numDigits / 2;

                if (Thread.currentThread().getId() % 2 == 0) {
                    start = numDigits / 2;
                    end = numDigits;
                }

                double piPartial = calculatePiDigits(start, end);

                out.println(piPartial);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private double calculatePiDigits(int start, int end) {
            double pi = 0.0;
            for (int k = start; k < end; k++) {
                pi += 1.0 / (16.0 * k + 1) - 4.0 / (16.0 * k + 5) + 4.0 / (16.0 * k + 3) - 1.0 / (16.0 * k + 7);
            }
            return pi;
        }
    }
}
