package com.javatechie.filter;

import com.javatechie.entity.RefreshToken;
import com.javatechie.repository.RefreshTokenRepository;
import com.javatechie.service.Impl.UserInfoUserDetailsService;
import com.javatechie.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserInfoUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String accessToken = null;
        String refreshToken = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
            username = jwtService.extractUsername(accessToken);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isAccessTokenValid(accessToken)) {

                Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByAccessToken(accessToken);
                if (refreshTokenOptional.isPresent()) {
                    RefreshToken rt = refreshTokenOptional.get();
                    if (!rt.isRevoked()) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        revokeTokens(rt);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token has been revoked");
                        return;
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
                    return;
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
                return;
            }
        }

        filterChain.doFilter(request, response);


        if (isLogoutRequest(request)) {
            Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByAccessToken(accessToken);
            if (refreshTokenOptional.isPresent()) {
                RefreshToken rt = refreshTokenOptional.get();
                revokeTokens(rt);
            }
        }
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return request.getParameter("logout") != null;
    }

    private void revokeTokens(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}