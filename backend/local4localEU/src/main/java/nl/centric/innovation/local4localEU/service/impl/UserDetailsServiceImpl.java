package nl.centric.innovation.local4localEU.service.impl;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(()-> new UsernameNotFoundException("Username not found"));
    }
}
