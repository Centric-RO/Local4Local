package nl.centric.innovation.local4localEU.unit;

import nl.centric.innovation.local4localEU.dto.CategoryDto;
import nl.centric.innovation.local4localEU.entity.Category;
import nl.centric.innovation.local4localEU.repository.CategoryRepository;
import nl.centric.innovation.local4localEU.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTests {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    public void GivenCategoriesInRepository_WhenGetAllCategories_ThenReturnCategoryDtoList() {
        // Given
        Category category1 = Category.builder().id(1).label("category1").build();
        Category category2 = Category.builder().id(2).label("category2").build();

        List<Category> categoryList = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // When
        List<CategoryDto> result = categoryService.getAllCategories();

        // Then
        assertEquals(2, result.size());
        assertEquals("category1", result.get(0).label());
        assertEquals("category2", result.get(1).label());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void GivenEmptyRepository_WhenGetAllCategories_ThenReturnEmptyList() {
        // Given
        when(categoryRepository.findAll()).thenReturn(List.of());

        // When
        List<CategoryDto> result = categoryService.getAllCategories();

        // Then
        assertEquals(0, result.size());
        verify(categoryRepository, times(1)).findAll();
    }
}
