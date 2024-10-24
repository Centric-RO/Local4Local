package nl.centric.innovation.local4localEU.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.dto.InviteMerchantDto;
import nl.centric.innovation.local4localEU.entity.MerchantInvitation;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;
import nl.centric.innovation.local4localEU.repository.MerchantInvitationRepository;
import nl.centric.innovation.local4localEU.service.interfaces.EmailService;
import nl.centric.innovation.local4localEU.service.interfaces.MerchantInvitationService;

@Service
@RequiredArgsConstructor
@PropertySource({"classpath:errorcodes.properties"})
public class MerchantInvitationServiceImpl implements MerchantInvitationService {

    private final EmailService emailService;

    private final MerchantInvitationRepository merchantInvitationRepository;

    @Value("${error.constraint.duplicate}")
    private String duplicateValue;

    @Value("${error.TooManyEmails}")
    private String errorTooManyEmails;

    @Value("${local4localEU.server.name}")
    private String baseURL;

    @Value("${error.general.entityValidate}")
    private String errorEntityValidate;

    @Override
    public void save(InviteMerchantDto inviteMerchantDto, String language) throws DtoValidateException {

        if (inviteMerchantDto.emails().size() > 50) {
            throw new DtoValidateException(errorTooManyEmails);
        }

        if (inviteMerchantDto.message() == null || inviteMerchantDto.message().isEmpty() || inviteMerchantDto.message().length() > 1024) {
            throw new DtoValidateException(errorEntityValidate);
        }

        Set<String> processedEmails = new HashSet<>();

        for (String email : inviteMerchantDto.emails()) {

            if (processedEmails.contains(email)) {
                throw new DtoValidateException(duplicateValue);
            }

            MerchantInvitation InviteMerchant = MerchantInvitation.builder().email(email)
                    .message(inviteMerchantDto.message()).build();
            merchantInvitationRepository.save(InviteMerchant);

            processedEmails.add(email);
        }
        String url = baseURL;
        String[] emailsArray = processedEmails.toArray(new String[0]);
        emailService.sendInviteMerchantEmail(url, language, emailsArray, inviteMerchantDto.message());

    }

    @Override
    public Integer countInvitations() {
        return merchantInvitationRepository.countByIsActiveTrue();
    }
}
