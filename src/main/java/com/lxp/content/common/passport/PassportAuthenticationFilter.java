package com.lxp.content.common.passport;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PassportAuthenticationFilter extends OncePerRequestFilter {

    private final PassportExtractor extractor;
    private final PassportVerifier verifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String encodedPassport = extractor.extract(request);

        if (encodedPassport != null) {
            try {
                PassportClaims claims = verifier.verify(encodedPassport);

                log.info("=== Passport Debug ===");
                log.info("UserId: {}", claims.userId());
                log.info("Raw roles: {}", claims.roles());

                // UsernamePasswordAuthenticationToken 생성
                // principal: userId, credentials: Passport JWT, authorities: 권한 목록
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                claims.userId(),
                                encodedPassport,  // Credentials에 JWT 저장
                                claims.roles().stream()
                                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                        .toList()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // SecurityContext에 인증 정보 설정
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                // 분산 추적을 위해 MDC에 traceId 저장
                MDC.put("traceId", claims.traceId());

            } catch (Exception e) {
                throw new BadCredentialsException("Invalid passport", e);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}