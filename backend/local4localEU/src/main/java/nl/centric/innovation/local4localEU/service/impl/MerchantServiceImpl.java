package nl.centric.innovation.local4localEU.service.impl;

import static nl.centric.innovation.local4localEU.dto.MerchantDto.toEntity;
import static util.Validators.isKvkValid;
import static util.Validators.isValidUrl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Value("${error.unique.violation}")
    private String errorUniqueViolation;

    @Value("${error.general.entityValidate}")
    private String errorEntityValidate;

    @Value("${error.entity.notfound}")
    private String errorEntityNotFound;

    @Value("${error.constraint.duplicate}")
    private String duplicateValue;

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
