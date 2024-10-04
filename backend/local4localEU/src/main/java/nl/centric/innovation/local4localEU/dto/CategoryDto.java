package nl.centric.innovation.local4localEU.dto;

import lombok.Builder;
import lombok.NonNull;
import nl.centric.innovation.local4localEU.entity.Category;

@Builder
public record CategoryDto(
        @NonNull Integer id,
        @NonNull String label
) {
    public static CategoryDto fromEntity(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .label(category.getLabel())
                .build();
    }
}
