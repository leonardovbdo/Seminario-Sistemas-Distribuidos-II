import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordBreaker {

    private static final String TARGET_PASSWORD = "fsvsa"; // Senha alvo
    private static final int MAX_PASSWORD_LENGTH = 5; // Comprimento máximo da senha
    private static volatile boolean senhaDescoberta = false; // Indicador se a senha foi descoberta
    private static final Object lock = new Object(); // Objeto de sincronização

    public static void main(String[] args) {
        Thread thread = new Thread(new PasswordBreakerThread());
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt(); // Restaurar a flag de interrupção
        }
    }

    static class PasswordBreakerThread implements Runnable {
        private int tentativas = 0;

        @Override
        public void run() {
            bruteForce("");
        }

        private void bruteForce(String prefix) {
            // Embaralhar a ordem dos caracteres
            List<Character> characters = new ArrayList<>();
            for (char c = 'a'; c <= 'z'; c++) {
                characters.add(c);
            }
            Collections.shuffle(characters);

            synchronized (lock) {
                if (prefix.equals(TARGET_PASSWORD)) {
                    senhaDescoberta = true;
                    System.out.println("Senha quebrada pela thread " + Thread.currentThread().getId() +
                            ": " + prefix + " (Tentativas: " + tentativas + ")");
                    return;
                }
            }

            if (!senhaDescoberta && prefix.length() < MAX_PASSWORD_LENGTH) {
                System.out.println("Thread " + Thread.currentThread().getId() +
                        " tentando senha: " + prefix + " (Tentativas: " + tentativas + ")");
                tentativas++;

                for (char c : characters) {
                    bruteForce(prefix + c);
                }
            }
        }
    }
}
