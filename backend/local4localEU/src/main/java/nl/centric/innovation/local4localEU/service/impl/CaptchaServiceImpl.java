package nl.centric.innovation.local4localEU.service.impl;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.dto.GoogleResponseDto;
import nl.centric.innovation.local4localEU.service.interfaces.CaptchaService;
import nl.centric.innovation.local4localEU.settings.CaptchaSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@PropertySource({"classpath:application.properties"})
public class CaptchaServiceImpl implements CaptchaService {
    private final CaptchaSettings captchaSettings;

    private final RestOperations restTemplate;

    @Value("${google.recaptcha.verify.url-template}")
    private String recaptchaUrlTemplate;

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z\\d_-]+");

    @Override
    public boolean isResponseValid(final String response, final String remoteIp) {
        if (!responseSanityCheck(response)) {
            return false;
        }

        URI verifyUri = URI.create(String.format(
                recaptchaUrlTemplate,
                captchaSettings.getSecret(), response, remoteIp));
        try {
            final GoogleResponseDto googleResponse = restTemplate.getForObject(verifyUri, GoogleResponseDto.class);

            if (googleResponse == null) {
                return false;
            }

            return googleResponse.isSuccess();
        } catch (RestClientException rce) {
            return false;
        }
    }

    private boolean responseSanityCheck(final String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}
