package nl.centric.innovation.local4localEU.dto;

import lombok.Builder;
import org.springframework.http.HttpHeaders;

@Builder
public record AuthResponseDto(LoginResponseDto loginResponseDto, HttpHeaders httpHeaders) { }
