package nl.centric.innovation.local4localEU.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record InviteMerchantDto(List<String> emails, String message) {
}