package hexlet.code.service;

import hexlet.code.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;

    @Value("${jwt.exp-minutes:60}")
    private int expirationMinutes;

    /**
     * Generates a JWT token for the authenticated user.
     * The token includes the user's role in the 'scope' claim and has a configurable expiration time.
     * @param authentication the authentication object containing user details
     * @return a signed JWT token string
     */
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        var user = (User) authentication.getPrincipal();
        String scope = user.getRole().name();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .claim("scope", scope)
                .subject(authentication.getName())
                .build();

        log.info("JWT token generated successfully");
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
