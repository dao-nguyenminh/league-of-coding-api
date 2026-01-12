package com.leagueofcoding.api.security;

import com.leagueofcoding.api.entity.User;
import com.leagueofcoding.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CustomUserDetailsService - Load user từ database cho Spring Security.
 *
 * @author dao-nguyenminh
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user theo username (được gọi bởi AuthenticationManager khi login).
     *
     * @param username username để tìm kiếm
     * @return UserDetails object
     * @throws UsernameNotFoundException nếu user không tồn tại
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));

        return UserPrincipal.create(user);
    }

    /**
     * Load user theo ID (custom method, dùng khi có userId từ JWT).
     *
     * @param id user ID
     * @return UserDetails object
     * @throws UsernameNotFoundException nếu user không tồn tại
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with id: " + id
                ));

        return UserPrincipal.create(user);
    }
}