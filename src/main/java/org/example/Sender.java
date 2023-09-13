package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Properties;

public class Sender {

    public static synchronized void sendMessage(String message) throws InterruptedException, IOException, URISyntaxException {
        Properties prop = PropertyLoader.loadProperties();
        String tgToken = prop.getProperty("tgToken");
        String chatId = prop.getProperty("chatId");
        String urlToken = "https://api.telegram.org/bot" + tgToken + "/sendMessage";

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> map = new HashMap<>();
        map.put("chat_id", chatId);
        map.put("text", message);
        String jsonString = objectMapper.writeValueAsString(map);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(urlToken))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        JsonNode jsonNode = objectMapper.readTree(httpResponse.body());
        if (jsonNode.get("ok").booleanValue()) System.out.println("Message: " + message + " send OK in "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
        else System.out.println(httpResponse.body());
    }
}