package nl.centric.innovation.local4localEU.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CaptchaConfiguration {
    @Value("${captcha.api.client.connection-timeout}")
    private Integer connectionTimeout;

    @Value("${captcha.api.client.read-timeout}")
    private Integer readTimeout;

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    @Bean
    public RestOperations restTemplate() {
        return new RestTemplate(this.clientHttpRequestFactory());
    }
}
