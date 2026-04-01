package com.healthcare.auth.security;

import com.healthcare.auth.entity.User;
import com.healthcare.auth.exception.ResourceNotFoundException;
import com.healthcare.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with " + username + " not found"));

        return UserPrincipal.from(user);
    }
}
