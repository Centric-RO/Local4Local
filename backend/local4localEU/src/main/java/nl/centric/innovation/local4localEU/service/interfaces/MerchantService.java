package nl.centric.innovation.local4localEU.service.interfaces;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import nl.centric.innovation.local4localEU.dto.MerchantDto;
import nl.centric.innovation.local4localEU.dto.MerchantViewDto;
import nl.centric.innovation.local4localEU.exception.CustomException.TalerException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;

public interface MerchantService {
    void approveMerchant(UUID merchantId, String language) throws DtoValidateException, URISyntaxException, IOException,
            InterruptedException, TalerException;

    MerchantDto saveMerchant(MerchantDto merchantDto) throws DtoValidateException;

    List<MerchantViewDto> getAll();

    List<MerchantViewDto> getPaginatedMerchants(Integer page, Integer size);

    List<MerchantViewDto> getByCategory(Integer categoryId);

    Long countAll();
}
