package nl.centric.innovation.local4localEU.unit;

import nl.centric.innovation.local4localEU.dto.GoogleResponseDto;
import nl.centric.innovation.local4localEU.service.impl.CaptchaServiceImpl;
import nl.centric.innovation.local4localEU.settings.CaptchaSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.web.client.RestOperations;

@ExtendWith(MockitoExtension.class)
public class CaptchaServiceImplTests {

    @InjectMocks
    private CaptchaServiceImpl captchaService;

    @Mock
    private CaptchaSettings captchaSettings;

    @Mock
    private RestOperations restTemplate;

    private static final String VALID_RESPONSE = "valid_response";
    private static final String INVALID_RESPONSE = "####";
    private static final String REMOTE_IP = "remote_ip";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(captchaService, "recaptchaUrlTemplate", "url");
        when(captchaSettings.getSecret()).thenReturn("secret_key");
    }

    @Test
    public void GivenValidResponse_WhenIsResponseValid_ThenExpectTrue() {

        GoogleResponseDto googleResponse = new GoogleResponseDto();
        googleResponse.setSuccess(true);

        when(restTemplate.getForObject(any(URI.class), eq(GoogleResponseDto.class)))
                .thenReturn(googleResponse);

        assertTrue(captchaService.isResponseValid(VALID_RESPONSE, REMOTE_IP));
    }

    @Test
    public void GivenValidResponse_WhenIsResponseValid_ThenExpectFalse() {

        GoogleResponseDto googleResponse = new GoogleResponseDto();
        googleResponse.setSuccess(false);

        when(restTemplate.getForObject(any(URI.class), eq(GoogleResponseDto.class)))
                .thenReturn(googleResponse);

        assertFalse(captchaService.isResponseValid(VALID_RESPONSE, REMOTE_IP));
    }

    @Test
    public void GivenError_WhenIsResponseValid_ThenExpectRestClientException() {

        when(restTemplate.getForObject(any(URI.class), eq(GoogleResponseDto.class)))
                .thenThrow(new RestClientException("RestClientException"));

        assertFalse(captchaService.isResponseValid(VALID_RESPONSE, REMOTE_IP));
    }

    @Test
    public void GivenInvalidResponse_WhenIsResponseValid_ThenExpectFalse() {
        Mockito.reset(captchaSettings);

        assertFalse(captchaService.isResponseValid(INVALID_RESPONSE, REMOTE_IP));
    }

    @Test
    public void GivenValidResponseAndNullObject_WhenIsResponseValid_ThenExpectFalse() {

        when(restTemplate.getForObject(any(URI.class), eq(GoogleResponseDto.class)))
                .thenReturn(null);

        assertFalse(captchaService.isResponseValid(VALID_RESPONSE, REMOTE_IP));

        verify(restTemplate).getForObject(any(URI.class), eq(GoogleResponseDto.class));
    }
}
