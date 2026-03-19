package me.gabrielporto.comandapro.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import me.gabrielporto.comandapro.infrastructure.persistence.repository.UserJpaRepository;

@Service
public class AuthConfig implements UserDetailsService {

    private final UserJpaRepository userRepository;

    public AuthConfig(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
