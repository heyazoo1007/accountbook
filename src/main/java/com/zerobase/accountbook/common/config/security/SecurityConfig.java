package com.zerobase.accountbook.common.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpSession;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // authenticationManager 를 Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(HttpBasicConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(configurer ->
                configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize-> authorize
                                        .requestMatchers(new AntPathRequestMatcher("/index")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/sign-up")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/sign-in")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/v1/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/v2/api-docs")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/configuration/ui")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/configuration/security")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/ws-stomp/**")).permitAll()
                                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/index")
                        // 로그아웃 핸들러 추가 (세션 무효화 처리)
                        .addLogoutHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession();
                            session.invalidate();
                        })
                        // 로그아웃 성공 핸들러 추가 (리다이렉션 처리)
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.sendRedirect("/index"))
                        .deleteCookies("JSESSIONID", "access_token")
                );

        return http.build();
    }
}
