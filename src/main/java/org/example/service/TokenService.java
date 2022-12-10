package org.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    public String generateToken(Authentication authentication, int expireMinute) {
        Instant now = Instant.now();
        String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuer("Self").issuedAt(now).expiresAt(now.plus(expireMinute, ChronoUnit.MINUTES)).subject(authentication.getName()).claim("roles", roles).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public Jwt verifyToken(String token) {
        return jwtDecoder.decode(token);

    }


}
