package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.user.User;
import de.franconia.tuebingen.adh.user.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(username.toLowerCase())
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "Benutzer nicht gefunden"
                        )
                );

        return new AppUserPrincipal(user);
    }
}