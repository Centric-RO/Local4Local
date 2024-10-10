package nl.centric.innovation.local4localEU.unit;

import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.repository.UserRepository;
import nl.centric.innovation.local4localEU.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void GivenUsername_WhenLoadUserByUsername_ThenExpectUserDetails() {
        String username = "testUser";
        User user = new User();
        user.setEmail(username);
        when(userRepository.findByEmailIgnoreCase(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(username, userDetails.getUsername(), "The usernames should match");
    }
}

