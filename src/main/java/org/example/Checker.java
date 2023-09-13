package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Checker {
    private final String url;
    private int failedCounter = 0;
    private int interval = 60000;

    public Checker(String url) {
        this.url = url;
        System.setProperty("com.sun.security.enableAIAcaIssuers", "true"); //отключение проверки сертификатов для https
    }

    public void check() throws IOException, URISyntaxException, InterruptedException {
        String[] split = url.split("/");
        String urlForNotify = split[2].trim();

        while (true) {
            Thread.sleep(interval);
            Map<String, Boolean> result = readData();
            if (result.get("app") && result.get("bd")) {
                if (failedCounter != 0)
                    Sender.sendMessage(urlForNotify + " All services are resumed " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
                failedCounter = 0;
                interval = 60000;
            }
            if (result.get("app") && !result.get("bd")) {
                failedCounter++;
                Sender.sendMessage(urlForNotify + " BD is fault " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
            }
            if (!result.get("app") && !result.get("bd")) {
                failedCounter++;
                Sender.sendMessage(urlForNotify + " APP is fault " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
            }
            if (failedCounter == 3) interval = 300000;
            if (failedCounter == 6) interval = 1800000;
        }
    }

    private Map<String, Boolean> readData() {
        Map<String, Boolean> resultMap = Map.of("app", false, "bd", false);
        ObjectMapper objectMapper = new ObjectMapper();

        HttpRequest httpRequest = null;
        try {
            httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();
        } catch (URISyntaxException e) {
            return resultMap;
        }
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            return resultMap;
        } catch (InterruptedException e) {
            return resultMap;
        }
        try {
            resultMap = objectMapper.readValue(httpResponse.body(), Map.class);
        } catch (JsonProcessingException e) {
            return resultMap;
        }
        return resultMap;
    }
}
