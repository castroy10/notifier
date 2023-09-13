package org.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    public static void main(String[] args) {
        Checker checker1 = new Checker("https://addr1.example.ru/ping");
        Checker checker2 = new Checker("https://addr2.example.ru/ping");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(() -> {
            try {
                checker1.check();
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        executor.execute(() -> {
            try {
                checker2.check();
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        executor.shutdown();
    }
}
