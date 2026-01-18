package com.github.messenger.infrastructure.security.filters;

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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    UserDetailsServiceImpl userDetailsService;
    JwtCore jwtCore;

    @Autowired
    public JwtFilter(UserDetailsServiceImpl userDetailsService, JwtCore jwtCore) {
        this.userDetailsService = userDetailsService;
        this.jwtCore = jwtCore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            if (jwtCore.verifyToken(token)) {
                String login = jwtCore.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(login);
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
