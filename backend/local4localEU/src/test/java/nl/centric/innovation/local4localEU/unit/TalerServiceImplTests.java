package nl.centric.innovation.local4localEU.unit;

import lombok.SneakyThrows;
import nl.centric.innovation.local4localEU.exception.CustomException.TalerException;
import nl.centric.innovation.local4localEU.service.impl.TalerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TalerServiceImplTests {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private TalerServiceImpl talerService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(talerService, "talerUrl", "http://localhost/taler");
        ReflectionTestUtils.setField(talerService, "talerKey", "taler-test-key");
        ReflectionTestUtils.setField(talerService, "errorTalerCreateInstance", "40024");
    }

    @Test
    @SneakyThrows
    void givenValidRequest_whenCreateTallerInstance_thenSucceed() {
        // Given
        String merchantName = "TestMerchant";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(204);

        // When
        talerService.createTallerInstance(merchantName);

        // Then
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    @SneakyThrows
    void givenFailedResponse_whenCreateTallerInstance_thenThrowTalerException() {
        // Given
        String merchantName = "TestMerchant";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);

        // When & Then
        TalerException exception = assertThrows(TalerException.class, () -> {
            talerService.createTallerInstance(merchantName);
        });
        assertEquals("40024", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void givenIOException_whenCreateTallerInstance_thenThrowException() {
        // Given
        String merchantName = "IOExceptionMerchant";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException());

        // When & Then
        assertThrows(IOException.class, () -> {
            talerService.createTallerInstance(merchantName);
        });
    }
}