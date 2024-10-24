package nl.centric.innovation.local4localEU.service.interfaces;

import nl.centric.innovation.local4localEU.dto.InviteMerchantDto;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;

public interface MerchantInvitationService {
	void save(InviteMerchantDto inviteSupplierDto, String language) throws DtoValidateException;

    Integer countInvitations();
}
