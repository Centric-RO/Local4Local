package nl.centric.innovation.local4localEU.dto;

import lombok.Builder;
import nl.centric.innovation.local4localEU.entity.Role;

import java.util.Date;

@Builder
public record LoginResponseDto(String role, Date expirationDate, boolean rememberMe) { }
