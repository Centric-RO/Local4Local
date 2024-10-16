package nl.centric.innovation.local4localEU.dto;

import lombok.Builder;
import lombok.NonNull;
import nl.centric.innovation.local4localEU.entity.Merchant;
import nl.centric.innovation.local4localEU.entity.RejectMerchant;

import java.util.UUID;

@Builder
public record RejectMerchantDto(
        @NonNull String reason,
        @NonNull UUID merchantId) {
    public static RejectMerchant toEntity(RejectMerchantDto rejectMerchantDto, Merchant merchant) {
        return RejectMerchant.builder()
                .reason(rejectMerchantDto.reason())
                .merchant(merchant)
                .build();
    }
}
