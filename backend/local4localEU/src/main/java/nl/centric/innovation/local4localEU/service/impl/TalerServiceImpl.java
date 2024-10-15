package nl.centric.innovation.local4localEU.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.entity.TalerInstance;
import nl.centric.innovation.local4localEU.exception.CustomException.TalerException;
import nl.centric.innovation.local4localEU.service.interfaces.TalerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TalerServiceImpl implements TalerService {

    private final HttpClient client;

    @Value("${taler.request.url}")
    private String talerUrl;

    @Value("${tk}")
    private String talerKey;

    @Value("${error.taler.createInstance}")
    private String errorTalerCreateInstance;

    public void createTallerInstance(String merchantName) throws URISyntaxException, IOException,
            InterruptedException, TalerException {

        String token = "secret-token:" + UUID.randomUUID();

        TalerInstance talerInstance = new TalerInstance(merchantName.replace(" ", "-"), token);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestBody = objectMapper.writeValueAsString(talerInstance);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(talerUrl))
                .header("Authorization", "Bearer secret-token:" + talerKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new TalerException(errorTalerCreateInstance);
        }
    }
}
