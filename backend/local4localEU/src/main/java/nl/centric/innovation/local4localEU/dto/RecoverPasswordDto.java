package nl.centric.innovation.local4localEU.dto;

import lombok.Builder;
import lombok.NonNull;
@Builder
public record RecoverPasswordDto(@NonNull String email, @NonNull String reCaptchaResponse) {
}
