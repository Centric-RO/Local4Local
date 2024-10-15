package nl.centric.innovation.local4localEU.unit;

import lombok.SneakyThrows;
import nl.centric.innovation.local4localEU.dto.MerchantDto;
import nl.centric.innovation.local4localEU.dto.MerchantViewDto;
import nl.centric.innovation.local4localEU.entity.Category;
import nl.centric.innovation.local4localEU.entity.Merchant;
import nl.centric.innovation.local4localEU.enums.MerchantStatusEnum;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateAlreadyExistsException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.repository.MerchantRepository;
import nl.centric.innovation.local4localEU.service.impl.MerchantServiceImpl;
import nl.centric.innovation.local4localEU.service.interfaces.EmailService;
import nl.centric.innovation.local4localEU.service.interfaces.TalerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MerchantServiceImplTests {

    @InjectMocks
    private MerchantServiceImpl merchantService;

    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private EmailService emailService;

    @Mock
    private TalerService talerService;

    private static final String VALID_KVK = "12345678";
    private static final String INVALID_KVK = "1234";
    private static final String VALID_WEBSITE = "https://www.example.com";
    private static final String INVALID_WEBSITE = "invalid-url";
    private static final Integer VALID_CATEGORY = 0;
    private static final Integer INVALID_CATEGORY = 9;

    private static final UUID VALID_MERCHANT_ID = UUID.randomUUID();
    private static final String VALID_LANGUAGE = "en";
    private Merchant pendingMerchant;
    private Merchant approvedMerchant;

    private MerchantDto validMerchantDto;
    private MerchantDto invalidKvkMerchantDto;
    private MerchantDto invalidWebsiteMerchantDto;
    private MerchantDto invalidCategoryMerchantDto;

    @BeforeEach
    void setup() {
        validMerchantDto = MerchantDto.builder()
                .companyName("Company")
                .kvk(VALID_KVK)
                .website(VALID_WEBSITE)
                .category(VALID_CATEGORY)
                .longitude(51.926517)
                .latitude(4.462456)
                .address("Address 1")
                .contactEmail("domain@example.com")
                .build();

        invalidKvkMerchantDto = MerchantDto.builder()
                .companyName("Company")
                .kvk(INVALID_KVK)
                .website(VALID_WEBSITE)
                .category(VALID_CATEGORY)
                .longitude(51.926517)
                .latitude(4.462456)
                .address("Address 2")
                .contactEmail("domain@example.com")
                .build();

        invalidWebsiteMerchantDto = MerchantDto.builder()
                .companyName("Company")
                .kvk(VALID_KVK)
                .website(INVALID_WEBSITE)
                .category(VALID_CATEGORY)
                .longitude(51.926517)
                .latitude(4.462456)
                .address("Address 3")
                .contactEmail("domain@example.com")
                .build();

        invalidCategoryMerchantDto = MerchantDto.builder()
                .companyName("Company")
                .kvk(VALID_KVK)
                .website(VALID_WEBSITE)
                .category(INVALID_CATEGORY)
                .longitude(51.926517)
                .latitude(4.462456)
                .address("Address 1")
                .contactEmail("domain@example.com")
                .build();
    }

    @Test
    @SneakyThrows
    public void GivenValidMerchantDto_WhenSaveMerchant_ThenMerchantIsSaved() {
        // Given
        when(merchantRepository.findByKvk(VALID_KVK)).thenReturn(Optional.empty());

        // When
        merchantService.saveMerchant(validMerchantDto);

        // Then
        verify(merchantRepository, times(1)).save(any(Merchant.class));
    }

    @Test
    public void GivenExistingMerchant_WhenSaveMerchant_ThenExpectDtoValidateAlreadyExistsException() {
        // Given
        Merchant existingMerchant = new Merchant();
        when(merchantRepository.findByKvk(VALID_KVK)).thenReturn(Optional.of(existingMerchant));

        // When Then
        assertThrows(DtoValidateAlreadyExistsException.class, () -> merchantService.saveMerchant(validMerchantDto));

        verify(merchantRepository, never()).save(any(Merchant.class));
    }

    @Test
    public void GivenInvalidKvk_WhenSaveMerchant_ThenExpectDtoValidateException() {
        // Given
        when(merchantRepository.findByKvk(INVALID_KVK)).thenReturn(Optional.empty());

        // When Then
        assertThrows(DtoValidateException.class, () -> merchantService.saveMerchant(invalidKvkMerchantDto));

        verify(merchantRepository, never()).save(any(Merchant.class));
    }

    @Test
    public void GivenInvalidCategoryId_WhenSaveMerchant_ThenExpectDtoValidateException() {
        // Given
        when(merchantRepository.findByKvk(VALID_KVK)).thenReturn(Optional.empty());

        // When Then
        assertThrows(DtoValidateException.class, () -> merchantService.saveMerchant(invalidCategoryMerchantDto));

        verify(merchantRepository, never()).save(any(Merchant.class));
    }

    @Test
    public void GivenInvalidWebsite_WhenSaveMerchant_ThenExpectDtoValidateException() {
        // Given
        when(merchantRepository.findByKvk(VALID_KVK)).thenReturn(Optional.empty());

        // When Then
        assertThrows(DtoValidateException.class, () -> merchantService.saveMerchant(invalidWebsiteMerchantDto));

        verify(merchantRepository, never()).save(any(Merchant.class));
    }

    @Test
    public void GivenMerchantsInRepository_WhenGetAll_ThenReturnMerchantViewDtoList() {
        // Given
        Merchant merchant1 = merchantBuilder("Company 1", VALID_KVK);
        Merchant merchant2 = merchantBuilder("Company 2", "12345679");

        merchant1.setId(UUID.randomUUID());
        merchant2.setId(UUID.randomUUID());

        List<Merchant> merchantList = Arrays.asList(merchant1, merchant2);
        when(merchantRepository.findByStatus(MerchantStatusEnum.APPROVED)).thenReturn(merchantList);

        // When
        List<MerchantViewDto> result = merchantService.getAllApproved();

        // Then
        assertEquals(2, result.size());
        assertEquals("Company 1", result.get(0).companyName());
        assertEquals("Company 2", result.get(1).companyName());
        verify(merchantRepository, times(1)).findByStatus(MerchantStatusEnum.APPROVED);
    }

    @Test
    public void GivenEmptyRepository_WhenGetAll_ThenReturnEmptyList() {
        // Given
        when(merchantRepository.findByStatus(MerchantStatusEnum.APPROVED)).thenReturn(List.of());

        // When
        List<MerchantViewDto> result = merchantService.getAllApproved();

        // Then
        assertEquals(0, result.size());
        verify(merchantRepository, times(1)).findByStatus(MerchantStatusEnum.APPROVED);
    }

    @Test
    public void GivenMerchantsInCategory_WhenGetByCategory_ThenReturnMerchantViewDtoList() {
        // Given
        Merchant merchant1 = merchantBuilder("Company 1", VALID_KVK);
        Merchant merchant2 = merchantBuilder("Company 2", "12345679");
        merchant1.getCategory().setId(1);
        merchant2.getCategory().setId(1);

        merchant1.setId(UUID.randomUUID());
        merchant2.setId(UUID.randomUUID());

        List<Merchant> merchantList = Arrays.asList(merchant1, merchant2);
        when(merchantRepository.findByCategoryId(1)).thenReturn(merchantList);

        // When
        List<MerchantViewDto> result = merchantService.getByCategory(1);

        // Then
        assertEquals(2, result.size());
        assertEquals("Company 1", result.get(0).companyName());
        assertEquals("Company 2", result.get(1).companyName());
        verify(merchantRepository, times(1)).findByCategoryId(1);
    }

    @Test
    public void GivenNoMerchantsInCategory_WhenGetByCategory_ThenReturnEmptyList() {
        // Given
        when(merchantRepository.findByCategoryId(1)).thenReturn(List.of());

        // When
        List<MerchantViewDto> result = merchantService.getByCategory(1);

        // Then
        assertEquals(0, result.size());
        verify(merchantRepository, times(1)).findByCategoryId(1);
    }

    @Test
    public void GivenInvalidCategoryId_WhenGetByCategory_ThenReturnAllMerchants() {
        // Given
        Merchant merchant1 = merchantBuilder("Company 1", VALID_KVK);
        Merchant merchant2 = merchantBuilder("Company 2", "12345679");

        merchant1.setId(UUID.randomUUID());
        merchant2.setId(UUID.randomUUID());

        List<Merchant> merchantList = Arrays.asList(merchant1, merchant2);
        when(merchantRepository.findAll()).thenReturn(merchantList);

        Integer invalidCategoryId = 9;

        // When
        List<MerchantViewDto> result = merchantService.getByCategory(invalidCategoryId);

        // Then
        assertEquals(2, result.size());
        assertEquals("Company 1", result.get(0).companyName());
        assertEquals("Company 2", result.get(1).companyName());
        verify(merchantRepository, times(1)).findAll();
        verify(merchantRepository, never()).findByCategoryId(any(Integer.class));
    }

    @Test
    public void GivenMerchantsInRepository_WhenCountAllMerchants_ThenReturnMerchantCount() {
        // Given
        Long mockMerchantsCount = 12L;
        when(merchantRepository.count()).thenReturn(mockMerchantsCount);

        // When
        Long result = merchantService.countAll();

        // Then
        assertEquals(mockMerchantsCount, result);
        verify(merchantRepository, times(1)).count();
    }

    @Test
    public void GivenMerchantsInRepository_WhenGetPaginatedMerchants_ThenReturnMerchantViewDtoList() {
        // Given
        Merchant merchant1 = merchantBuilder("Company 1", VALID_KVK);
        Merchant merchant2 = merchantBuilder("Company 2", "12345679");

        merchant1.setId(UUID.randomUUID());
        merchant2.setId(UUID.randomUUID());

        List<Merchant> merchantList = Arrays.asList(merchant1, merchant2);
        Page<Merchant> merchantPage = new PageImpl<>(merchantList);

        when(merchantRepository.findAll(any(Pageable.class))).thenReturn(merchantPage);

        // When
        List<MerchantViewDto> result = merchantService.getPaginatedMerchants(0, 10);

        // Then
        assertEquals(2, result.size());
        assertEquals("Company 1", result.get(0).companyName());
        assertEquals("Company 2", result.get(1).companyName());
        verify(merchantRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void GivenEmptyRepository_WhenGetPaginatedMerchants_ThenReturnEmptyList() {
        // Given
        Page<Merchant> emptyMerchantPage = new PageImpl<>(List.of());

        when(merchantRepository.findAll(any(Pageable.class))).thenReturn(emptyMerchantPage);

        // When
        List<MerchantViewDto> result = merchantService.getPaginatedMerchants(0, 10);

        // Then
        assertEquals(0, result.size());
        verify(merchantRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void GivenInvalidPage_WhenGetPaginatedMerchants_ThenReturnEmptyList() {
        // Given
        Page<Merchant> emptyMerchantPage = new PageImpl<>(List.of());

        when(merchantRepository.findAll(any(Pageable.class))).thenReturn(emptyMerchantPage);

        // When
        List<MerchantViewDto> result = merchantService.getPaginatedMerchants(999, 10);

        // Then
        assertEquals(0, result.size());
        verify(merchantRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void GivenNonExistingMerchant_WhenApproveMerchant_ThenThrowDtoValidateNotFoundException() {
        // Given
        when(merchantRepository.findById(VALID_MERCHANT_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DtoValidateNotFoundException.class, () -> merchantService.approveMerchant(VALID_MERCHANT_ID, VALID_LANGUAGE));

        verify(merchantRepository, times(1)).findById(VALID_MERCHANT_ID);
        verify(merchantRepository, never()).save(any(Merchant.class));
        verify(emailService, never()).sendApproveMerchantEmail(any(), any(), any());
    }

    @Test
    public void GivenAlreadyApprovedMerchant_WhenApproveMerchant_ThenThrowDtoValidateAlreadyExistsException() {
        // Given
        Merchant approvedMerchant = merchantBuilder("Company 1", VALID_KVK);
        approvedMerchant.setStatus(MerchantStatusEnum.APPROVED);
        when(merchantRepository.findById(VALID_MERCHANT_ID)).thenReturn(Optional.of(approvedMerchant));

        // When & Then
        assertThrows(DtoValidateAlreadyExistsException.class, () -> merchantService.approveMerchant(VALID_MERCHANT_ID, VALID_LANGUAGE));

        verify(merchantRepository, times(1)).findById(VALID_MERCHANT_ID);
        verify(merchantRepository, never()).save(any(Merchant.class));
        verify(emailService, never()).sendApproveMerchantEmail(any(), any(), any());
    }

    @Test
    @SneakyThrows
    public void GivenPendingMerchant_WhenApproveMerchant_ThenMerchantIsApprovedAndEmailIsSent() {
        // Given
        Merchant pendingMerchant = merchantBuilder("Company 1", VALID_KVK);
        pendingMerchant.setStatus(MerchantStatusEnum.PENDING);
        when(merchantRepository.findById(VALID_MERCHANT_ID)).thenReturn(Optional.of(pendingMerchant));
        doNothing().when(talerService).createTallerInstance(pendingMerchant.getCompanyName());
        // When
        merchantService.approveMerchant(VALID_MERCHANT_ID, VALID_LANGUAGE);

        // Then
        assertEquals(MerchantStatusEnum.APPROVED, pendingMerchant.getStatus());
        verify(merchantRepository, times(1)).findById(VALID_MERCHANT_ID);
        verify(merchantRepository, times(1)).save(pendingMerchant);
        verify(emailService, times(1)).sendApproveMerchantEmail(new String[]{pendingMerchant.getContactEmail()}, VALID_LANGUAGE, pendingMerchant.getCompanyName());
    }

    private Merchant merchantBuilder(String companyName, String kvk) {
        return Merchant.builder()
                .companyName(companyName)
                .kvk(kvk)
                .website(VALID_WEBSITE)
                .category(Category.builder().id(2).label("category").build())
                .lat(51.926517)
                .lon(4.462456)
                .address("Address 1")
                .contactEmail("domain@example.com")
                .status(MerchantStatusEnum.APPROVED)
                .build();
    }
}
