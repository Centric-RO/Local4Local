package nl.centric.innovation.local4localEU.service.impl;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.dto.CategoryDto;
import nl.centric.innovation.local4localEU.repository.CategoryRepository;
import nl.centric.innovation.local4localEU.service.interfaces.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}
