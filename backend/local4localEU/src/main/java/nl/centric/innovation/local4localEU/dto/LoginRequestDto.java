package nl.centric.innovation.local4localEU.dto;

import lombok.Builder;

@Builder
public record LoginRequestDto(
        String email,
        String password,
        String role,
        String reCaptchaResponse,
        Boolean rememberMe) {
}
