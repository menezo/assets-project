package com.menezo.assetsproject.model.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.menezo.assetsproject.model.dto.StockDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
public class StockApiClient {

    private static final String API_URL = "https://brapi.dev/api/quote";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${api.token}")
    private String apiToken;

    private static final Logger logger = LoggerFactory.getLogger(StockApiClient.class);


    @Autowired
    public StockApiClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public Optional<StockDTO> getStockData(String ticker) {
        try {
            URI uri = new URI(API_URL + "/" + ticker);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + apiToken)
                    .GET()
                    .build();

            HttpResponse<String>response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());
                JsonNode resultNode = jsonNode.get("results").get(0);
                StockDTO stockResponse = parseStockDTO(resultNode);
                return Optional.of(stockResponse);
            }
            else {
                throw new RuntimeException("Request error: " + response.statusCode() + ". Message: " + response.body());
            }
        }
        catch (Exception e) {
            logger.error("Failed to fetch stock data", e);
            return Optional.empty();
        }
    }

    private StockDTO parseStockDTO(JsonNode resultNode) {
        String longName = resultNode.get("longName").asText();
        double regularMarketPrice = resultNode.get("regularMarketPrice").asDouble();
        String symbol = resultNode.get("symbol").asText();
        return new StockDTO(longName, regularMarketPrice, symbol);
    }
}
