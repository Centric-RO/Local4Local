package nl.centric.innovation.local4localEU.service.impl;

import static nl.centric.innovation.local4localEU.dto.MerchantDto.toEntity;
import static util.Validators.isKvkValid;
import static util.Validators.isValidUrl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.centric.innovation.local4localEU.entity.TalerInstance;
import nl.centric.innovation.local4localEU.enums.MerchantStatusEnum;
import nl.centric.innovation.local4localEU.exception.CustomException.TalerException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.service.interfaces.EmailService;
import nl.centric.innovation.local4localEU.service.interfaces.TalerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.dto.MerchantDto;
import nl.centric.innovation.local4localEU.dto.MerchantViewDto;
import nl.centric.innovation.local4localEU.entity.Merchant;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateAlreadyExistsException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;
import nl.centric.innovation.local4localEU.repository.MerchantRepository;
import nl.centric.innovation.local4localEU.service.interfaces.MerchantService;

@Service
@RequiredArgsConstructor
@PropertySource({"classpath:errorcodes.properties"})
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;

    private final EmailService emailService;

    private final TalerService talerService;

    @Value("${error.unique.violation}")
    private String errorUniqueViolation;

    @Value("${error.general.entityValidate}")
    private String errorEntityValidate;

    @Value("${error.entity.notfound}")
    private String errorEntityNotFound;

    @Value("${error.constraint.duplicate}")
    private String duplicateValue;

    @Value("${error.entity.redundantChange}")
    private String redundantChange;

    @Override
    public void approveMerchant(UUID merchantId, String language) throws DtoValidateException, URISyntaxException,
            IOException, InterruptedException, TalerException {
        Optional<Merchant> merchant = merchantRepository.findById(merchantId);

        if (merchant.isEmpty()) {
            throw new DtoValidateNotFoundException(errorEntityNotFound);
        }

        if (merchant.get().getStatus() == MerchantStatusEnum.APPROVED) {
            throw new DtoValidateAlreadyExistsException(redundantChange);
        }

        merchant.get().setStatus(MerchantStatusEnum.APPROVED);
        merchantRepository.save(merchant.get());

        talerService.createTallerInstance(merchant.get().getCompanyName());
        String[] email = new String[]{merchant.get().getContactEmail()};
        emailService.sendApproveMerchantEmail(email, language, merchant.get().getCompanyName());
    }

    @Override
    public MerchantDto saveMerchant(MerchantDto merchantDto) throws DtoValidateException {
        validateMerchantDto(merchantDto);
        merchantRepository.save(toEntity(merchantDto));

        return merchantDto;
    }

    @Override
    public List<MerchantViewDto> getAll() {
        return merchantRepository.findAll().stream()
                .map(MerchantViewDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchantViewDto> getPaginatedMerchants(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("status"),
                Sort.Order.asc("createdDate")
        ));

        Page<Merchant> merchants = merchantRepository.findAll(pageable);

        return merchants.stream()
                .map(MerchantViewDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<MerchantViewDto> getByCategory(Integer categoryId) {
        boolean isValidCategory = categoryId != null && categoryId >= 0 && categoryId <= 8;

        List<Merchant> merchants = isValidCategory
                ? merchantRepository.findByCategoryId(categoryId)
                : merchantRepository.findAll();

        return merchants.stream()
                .map(MerchantViewDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Long countAll() {
        return merchantRepository.count();
    }

    private void validateMerchantDto(MerchantDto merchantDto) throws DtoValidateException {
        Optional<Merchant> existingMerchant = merchantRepository.findByKvk(merchantDto.kvk());

        if (existingMerchant.isPresent()) {
            throw new DtoValidateAlreadyExistsException(errorUniqueViolation);
        }

        if (!isKvkValid(merchantDto.kvk())) {
            throw new DtoValidateException(errorEntityValidate);
        }

        if (merchantDto.category() < 0 || merchantDto.category() > 8) {
            throw new DtoValidateException(errorEntityValidate);
        }

        if (merchantDto.website() != null && !merchantDto.website().isEmpty() && !isValidUrl(merchantDto.website())) {
            throw new DtoValidateException(errorEntityValidate);
        }
    }

}
