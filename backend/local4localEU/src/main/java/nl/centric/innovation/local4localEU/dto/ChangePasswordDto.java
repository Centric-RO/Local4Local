package nl.centric.innovation.local4localEU.dto;

import lombok.NonNull;

public record ChangePasswordDto(@NonNull String token, @NonNull String password) {
}
