package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordBreaker implements Runnable {
    private static String TARGET_PASSWORD;
    private static final int MAX_PASSWORD_LENGTH = 5;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private static final Object lock = new Object(); // Objeto de sincronização
    private static volatile boolean senhaDescoberta = false; // Indicador se a senha foi descoberta
    private int tentativas = 0;

    public PasswordBreaker(BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {

        try {
            TARGET_PASSWORD = reader.readLine();
            System.out.println("Senha recebida do servidor: " + TARGET_PASSWORD);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        bruteForce("");
        long endTime = System.currentTimeMillis();

        String threadInfo = "Thread " + Thread.currentThread().getId();
        if (senhaDescoberta) {
            writer.println(threadInfo + " Senha encontrada: " + TARGET_PASSWORD);
            System.out.println(threadInfo + " Senha encontrada: " + TARGET_PASSWORD);
        } else {
            writer.println(threadInfo + " Senha não encontrada");
            System.out.println(threadInfo + " Senha não encontrada");
        }
        writer.println(threadInfo + " Tempo total: " + (endTime - startTime) + " ms");
        System.out.println(threadInfo + " Tempo total: " + (endTime - startTime) + " ms");
    }

    private void bruteForce(String prefix) {
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
            tentativas++;

            for (char c : characters) {
                System.out.println("Thread " + Thread.currentThread().getId() +
                        " tentando senha: " + prefix + c + " (Tentativas: " + tentativas + ")");
                bruteForce(prefix + c);
                if (senhaDescoberta) break;
            }
        }
    }
}
