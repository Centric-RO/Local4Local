package nl.centric.innovation.local4localEU.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import nl.centric.innovation.local4localEU.dto.InviteMerchantDto;
import nl.centric.innovation.local4localEU.entity.MerchantInvitation;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;
import nl.centric.innovation.local4localEU.repository.MerchantInvitationRepository;
import nl.centric.innovation.local4localEU.service.impl.MerchantInvitationServiceImpl;
import nl.centric.innovation.local4localEU.service.interfaces.EmailService;

@ExtendWith(MockitoExtension.class)
public class MerchantInvitationServiceImplTest {

	@InjectMocks
    private MerchantInvitationServiceImpl merchantInvitationService;

	@Mock
    private EmailService emailService;

    @Mock
    private MerchantInvitationRepository merchantInvitationRepository;
    @Value("${error.constraint.duplicate}")
    private String duplicateValue;

    @Value("${error.TooManyEmails}")
    private String errorTooManyEmails;


    @Test
    void GivenTooManyEmails_WhenSave_ThenExpectDtoValidateException() {
        InviteMerchantDto dto = InviteMerchantDto.builder()
                .emails(Arrays.asList(new String[51])) // More than 50 emails
                .build();
        assertThrows(DtoValidateException.class, () -> {
            merchantInvitationService.save(dto, "en");
        });
    }

    @Test
    void GivenDuplicatesEmails_WhenSave_ThenExpectDtoValidateException() {
        InviteMerchantDto dto = InviteMerchantDto.builder()
                .emails(Arrays.asList("test@example.com", "test@example.com"))
                .build();

        assertThrows(DtoValidateException.class, () -> {
            merchantInvitationService.save(dto, "en");
        });
    }

    @Test
    void GivenEmptyMessage_WhenSave_ThenExpectDtoValidateException() {
        InviteMerchantDto dto = InviteMerchantDto.builder()
                .emails(Arrays.asList("test@example.com"))
                .build();

        assertThrows(DtoValidateException.class, () -> {
            merchantInvitationService.save(dto, "en");
        });
    }
    
    @Test
    void GivenValidInviteMerchantDto_WhenSave_ThenMerchantInvitationIsSavedAndEmailsAreSent() throws DtoValidateException {
        InviteMerchantDto dto = InviteMerchantDto.builder()
                .emails(Arrays.asList("test1@example.com", "test2@example.com"))
                .message("Join us!")
                .build();

        merchantInvitationService.save(dto, "en");

        verify(merchantInvitationRepository, times(2)).save(any(MerchantInvitation.class));
        verify(emailService, times(1)).sendInviteMerchantEmail(any(), anyString(), any(), anyString());
    }
}
