package com.github.messenger.infrastructure.security.filters;

import com.github.messenger.infrastructure.helper.BearerTokenExtractorHelper;
import com.github.messenger.infrastructure.security.JwtCore;
import com.github.messenger.infrastructure.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    UserDetailsServiceImpl userDetailsService;
    JwtCore jwtCore;
    BearerTokenExtractorHelper jwtExtractorHelper;

    @Autowired
    public JwtFilter(UserDetailsServiceImpl userDetailsService, JwtCore jwtCore,  BearerTokenExtractorHelper jwtExtractorHelper) {
        this.userDetailsService = userDetailsService;
        this.jwtCore = jwtCore;
        this.jwtExtractorHelper = jwtExtractorHelper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        String token = jwtExtractorHelper.extract(authorizationHeader);

        if (token != null && jwtCore.verifyToken(token)) {
            Long userId = jwtCore.getUserIdFromToken(token);

            if (userId != null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserById(userId);
                    Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (UsernameNotFoundException ignored) {}
            }
        }

        filterChain.doFilter(request, response);
    }
}
