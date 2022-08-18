package com.featureflag.config;

import com.featureflag.authentication.Http401UnauthorizedEntryPoint;
import com.featureflag.authentication.jwt.JwtAuthenticationProvider;
import com.featureflag.authentication.jwt.JwtProperties;
import com.featureflag.authentication.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {

    private final TokenProvider tokenProvider;

    private final JwtProperties securityJwtProperties;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder) {
        String admin = securityJwtProperties.getAdmin();
        String password = securityJwtProperties.getPassword();
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            builder
                    .authenticationProvider(new JwtAuthenticationProvider(securityJwtProperties))
                    .inMemoryAuthentication()
                    .withUser(admin)
                    .password(passwordEncoder.encode(password))
                    .roles("ADMIN");
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint() {
        return new Http401UnauthorizedEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**").antMatchers("/swagger-ui/index.html")
//                .antMatchers("/test/**").antMatchers("/h2-console/**");
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .addFilterBefore(new BearerTokenAuthenticationFilter(authenticationManager),UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class).exceptionHandling()
                .authenticationEntryPoint(http401UnauthorizedEntryPoint()).and().csrf().disable().headers()
                .frameOptions().disable().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers("/feature-flags").authenticated()
                .antMatchers("/feature-flags/**").authenticated()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/swagger-resources/configuration/ui").permitAll().antMatchers("/swagger-ui/index.html").permitAll();
        return http.build();
    }

    private CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return new CorsFilter(source);
    }
}
