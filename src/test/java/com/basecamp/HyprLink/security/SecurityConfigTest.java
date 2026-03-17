package com.basecamp.HyprLink.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    // ==================== Bean Configuration Tests ====================

    @Test
    @DisplayName("Should expose BCrypt password encoder bean")
    void testPasswordEncoder_ReturnsBCryptEncoder() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("Should encode and verify password using configured encoder")
    void testPasswordEncoder_EncodesAndMatchesRawPassword() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "my-password";

        // Act
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Assert
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }

    // ==================== Filter Chain Configuration Tests ====================

    @Test
    @DisplayName("Should build and return security filter chain")
    void testSecurityFilterChain_BuildsAndReturnsFilterChain() throws Exception {
        // Arrange
        HttpSecurity httpSecurity = mock(HttpSecurity.class);
        DefaultSecurityFilterChain filterChain = mock(DefaultSecurityFilterChain.class);

        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.formLogin(any())).thenReturn(httpSecurity);
        when(httpSecurity.logout(any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(filterChain);

        // Act
        SecurityFilterChain result = securityConfig.securityFilterChain(httpSecurity);

        // Assert
        assertThat(result).isSameAs(filterChain);
    }

    @Test
    @DisplayName("Should apply authorize, form login, and logout configuration blocks")
    void testSecurityFilterChain_AppliesExpectedHttpSecurityBlocks() throws Exception {
        // Arrange
        HttpSecurity httpSecurity = mock(HttpSecurity.class);
        DefaultSecurityFilterChain filterChain = mock(DefaultSecurityFilterChain.class);

        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.formLogin(any())).thenReturn(httpSecurity);
        when(httpSecurity.logout(any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(filterChain);

        // Act
        securityConfig.securityFilterChain(httpSecurity);

        // Assert
        verify(httpSecurity, times(1)).authorizeHttpRequests(any());
        verify(httpSecurity, times(1)).formLogin(any());
        verify(httpSecurity, times(1)).logout(any());
        verify(httpSecurity, times(1)).build();
    }
}



