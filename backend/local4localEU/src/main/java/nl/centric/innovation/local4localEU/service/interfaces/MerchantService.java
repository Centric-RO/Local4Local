package nl.centric.innovation.local4localEU.service.interfaces;

import java.util.List;

import nl.centric.innovation.local4localEU.dto.MerchantDto;
import nl.centric.innovation.local4localEU.dto.MerchantViewDto;
import nl.centric.innovation.local4localEU.dto.RecoverPasswordDto;
import nl.centric.innovation.local4localEU.entity.RecoverPassword;
import nl.centric.innovation.local4localEU.exception.CustomException.CaptchaException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;
import nl.centric.innovation.local4localEU.exception.CustomException.RecoverException;

public interface MerchantService {
    MerchantDto saveMerchant(MerchantDto merchantDto) throws DtoValidateException;

    List<MerchantViewDto> getAll();

    List<MerchantViewDto> getPaginatedMerchants(Integer page, Integer size);

    List<MerchantViewDto> getByCategory(Integer categoryId);

    Long countAll();
}
