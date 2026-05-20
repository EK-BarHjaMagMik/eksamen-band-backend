package org.example.eksamenbandbackend.security;

import org.example.eksamenbandbackend.entity.User;
import org.example.eksamenbandbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.jspecify.annotations.NonNull;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        // Resolve the stored user record that Spring Security will authenticate
        // against.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert the entity to UserDetails so Spring Security can work with it.
        return new SecurityUser(user); // wraps entity into UserDetails
    }
}
