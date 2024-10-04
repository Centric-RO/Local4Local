package nl.centric.innovation.local4localEU.unit;

import nl.centric.innovation.local4localEU.entity.LoginAttempt;
import nl.centric.innovation.local4localEU.repository.LoginAttemptRepository;
import nl.centric.innovation.local4localEU.service.impl.LoginAttemptServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginAttemptServiceImplTests {

    @InjectMocks
    private LoginAttemptServiceImpl loginService;
    @Mock
    private LoginAttemptRepository attemptRepository;

    private static final String REMOTE_ADDRESS = "127.0.0.1";

    private static Stream<Arguments> customData() {
        return Stream.of(
                Arguments.of(true, 0, 0),
                Arguments.of(false, 0, 1)
        );
    }

    @Test
    public void GivenValid_WhenLogin_ThenExpectSuccess() {
        // Given

        boolean isSuccessful = true;
        LoginAttempt loginAttempt = loginAttemptBuilder(0);

        when(attemptRepository.save(any(LoginAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        LoginAttempt result = loginService.countLoginAttempts(Optional.of(loginAttempt), REMOTE_ADDRESS, isSuccessful);

        // Then
        assertEquals(0, result.getCount());
    }

    @Test
    public void GivenIsSuccessfulFalseCount2_WhenLogin_ThenExpectCount3() {
        // Given
        boolean isSuccessful = false;
        LoginAttempt existingLoginAttempt = loginAttemptBuilder(2);

        when(attemptRepository.save(any(LoginAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        LoginAttempt result = loginService.countLoginAttempts(Optional.of(existingLoginAttempt), REMOTE_ADDRESS, isSuccessful);

        // Then
        assertEquals(3, result.getCount());
    }

    @ParameterizedTest
    @MethodSource("customData")
    public void GivenFirstLogin_WhenLogin_ThenExpect0(Boolean isSuccessful, Integer actualCount, Integer expectedCount) {
        // Given and When
        when(attemptRepository.save(any(LoginAttempt.class))).thenAnswer(invocation -> invocation.getArgument(actualCount));
        LoginAttempt result = loginService.countLoginAttempts(Optional.empty(), REMOTE_ADDRESS, isSuccessful);

        // Then
        assertEquals(expectedCount, result.getCount());
    }

    @Test
    public void GivenCount5_WhenLoginAttempt_ThenExpectCount0() {
        // Given
        LoginAttempt loginAttempt = loginAttemptBuilder(5);

        // When
        when(attemptRepository.save(any(LoginAttempt.class))).thenReturn(loginAttempt);

        LoginAttempt result = loginService.countLoginAttempts(Optional.of(loginAttempt), REMOTE_ADDRESS, true);

        // Then
        assertEquals(0, result.getCount());
        verify(attemptRepository, times(1)).save(loginAttempt);
    }

    @Test
    public void GivenCount5_WHenIsBlocked_ThenExpectTrue() {

        LoginAttempt loginAttempt = loginAttemptBuilder(5);

        assertTrue(loginService.isBlocked(loginAttempt));
    }

    @Test
    public void GivenCount3_WHenIsBlocked_ThenExpectFalse() {

        LoginAttempt loginAttempt = loginAttemptBuilder(3);

        assertFalse(loginService.isBlocked(loginAttempt));
    }

    @Test
    void GivenAlreadyExistingRemoteAddress_WhenFindByRemoteAddress_ThenExpectResultIsPresent() {
        String remoteAddress = "192.168.1.1";
        LoginAttempt expectedLoginAttempt = new LoginAttempt();
        when(attemptRepository.findByRemoteAddress(remoteAddress)).thenReturn(Optional.of(expectedLoginAttempt));

        Optional<LoginAttempt> result = loginService.findByRemoteAddress(remoteAddress);

        assertTrue(result.isPresent());
        assertEquals(expectedLoginAttempt, result.get());
        verify(attemptRepository).findByRemoteAddress(remoteAddress);
    }

    private LoginAttempt loginAttemptBuilder(Integer count) {
        return LoginAttempt.builder()
                .remoteAddress(REMOTE_ADDRESS)
                .count(count)
                .build();
    }

}

