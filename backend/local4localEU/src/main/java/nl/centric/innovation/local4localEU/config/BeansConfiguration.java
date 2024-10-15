package nl.centric.innovation.local4localEU.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class BeansConfiguration {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
