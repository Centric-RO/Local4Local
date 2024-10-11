package nl.centric.innovation.local4localEU.service.interfaces;

import java.util.List;
import java.util.UUID;

import nl.centric.innovation.local4localEU.dto.MerchantDto;
import nl.centric.innovation.local4localEU.dto.MerchantViewDto;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;

public interface MerchantService {
    void approveMerchant(UUID merchantId, String language) throws DtoValidateException;

    MerchantDto saveMerchant(MerchantDto merchantDto) throws DtoValidateException;

    List<MerchantViewDto> getAllApproved();

    List<MerchantViewDto> getPaginatedMerchants(Integer page, Integer size);

    List<MerchantViewDto> getByCategory(Integer categoryId);

    Long countAll();
}
