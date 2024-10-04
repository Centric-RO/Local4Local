package nl.centric.innovation.local4localEU.unit;

import nl.centric.innovation.local4localEU.entity.OtpCodes;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.repository.OtpCodesRepository;
import nl.centric.innovation.local4localEU.service.impl.OtpCodesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OtpCodesServiceImplTests {
    @Mock
    private OtpCodesRepository otpCodesRepository;

    @InjectMocks
    private OtpCodesServiceImpl otpCodesServiceImpl;

    @Test
    void GivenValidUser_WhenSaveOtpCode_ThenOtpCodeIsGeneratedAndSaved() {
        // Given
        User user = new User();
        OtpCodes otpCode = OtpCodes.builder()
                .otpCode(123456)
                .sessionId(UUID.randomUUID())
                .user(user)
                .build();

        when(otpCodesRepository.save(any(OtpCodes.class))).thenReturn(otpCode);

        // When
        OtpCodes savedOtpCode = otpCodesServiceImpl.saveOtpCode(user);

        // Then
        assertNotNull(savedOtpCode);
        assertEquals(user, savedOtpCode.getUser());
        assertEquals(6, String.valueOf(savedOtpCode.getOtpCode()).length());
        verify(otpCodesRepository, times(1)).save(any(OtpCodes.class));
    }

    @Test
    void givenOtpCodeIsGenerated_WhenSaveOtpCode_ThenOtpCodeHasSixDigits() {
        // Given
        User user = new User();

        // When
        OtpCodes savedOtpCode = otpCodesServiceImpl.saveOtpCode(user);

        // Then
        assertNotNull(savedOtpCode);
        assertTrue(savedOtpCode.getOtpCode() >= 100000 && savedOtpCode.getOtpCode() <= 999999);
    }
}
