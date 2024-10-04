package nl.centric.innovation.local4localEU.unit;

import nl.centric.innovation.local4localEU.entity.RecoverPassword;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.exception.CustomException.RecoverException;
import nl.centric.innovation.local4localEU.repository.RecoverPasswordRepository;
import nl.centric.innovation.local4localEU.service.impl.RecoverPasswordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecoverPasswordServiceImplTests {
    @InjectMocks
    private RecoverPasswordServiceImpl recoverPasswordService;

    @Mock
    private RecoverPasswordRepository recoverPasswordRepository;

    private static final String SAMPLE_TOKEN = "sampleToken";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(recoverPasswordService, "errorRecoverExpired", "Recovery expired");
        ReflectionTestUtils.setField(recoverPasswordService, "errorEntityNotFound", "Entity not found");
    }

    @Test
    public void GivenValidToken_WhenFindByToken_ThenResultShouldMatch() {
        RecoverPassword recoverPassword = RecoverPassword.builder()
                .token(SAMPLE_TOKEN)
                .build();

        when(recoverPasswordRepository.findByTokenAndIsActiveTrue(SAMPLE_TOKEN)).thenReturn(Optional.of(recoverPassword));

        Optional<RecoverPassword> result = recoverPasswordRepository.findByTokenAndIsActiveTrue(SAMPLE_TOKEN);
        assertEquals(recoverPassword.getToken(), result.get().getToken());
    }

    @Test
    public void GivenNonExistingToken_WhenFindByToken_ThenReturnError() {
        when(recoverPasswordRepository.findByTokenAndIsActiveTrue(SAMPLE_TOKEN)).thenReturn(Optional.empty());

        assertThrows(DtoValidateNotFoundException.class, () -> recoverPasswordService.findRecoverPasswordByToken(SAMPLE_TOKEN));
    }

    @Test
    public void GivenExpiredToken_WhenFindByToken_ThenReturnException() {
        RecoverPassword recoverPassword = RecoverPassword.builder()
                .token(SAMPLE_TOKEN)
                .tokenExpirationDate(new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3)))
                .build();

        when(recoverPasswordRepository.findByTokenAndIsActiveTrue(SAMPLE_TOKEN)).thenReturn(Optional.of(recoverPassword));

        assertThrows(RecoverException.class, () -> recoverPasswordService.findRecoverPasswordByToken(SAMPLE_TOKEN));
    }

    @Test
    public void GivenNumberOfRecovery_WhenCountRequests_ThenShouldMatchTheValid() {
        UUID userId = UUID.randomUUID();

        int countValue = 5;
        when(recoverPasswordRepository.countRecoveryRequestsInLastDayByUser(userId)).thenReturn(countValue);

        Integer result = recoverPasswordService.countAllByUserInLastDay(userId);
        assertEquals(countValue, result);
    }

    @Test
    public void GivenValidRecoverPassword_WhenSave_ThenShouldReturnTheSame() {
        RecoverPassword recoverPassword = RecoverPassword.builder()
                .token(SAMPLE_TOKEN)
                .build();

        when(recoverPasswordRepository.save(recoverPassword)).thenReturn(recoverPassword);

        RecoverPassword result = recoverPasswordService.save(recoverPassword);
        assertEquals(recoverPassword, result);
    }

}
