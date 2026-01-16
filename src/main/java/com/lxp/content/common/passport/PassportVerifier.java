package com.lxp.content.common.passport;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;

@Component
public class PassportVerifier {

    private final SecretKey key;

    public PassportVerifier(KeyProperties keyProperties) {
        this.key = keyProperties. passportSecretKey();
    }


    public PassportClaims verify(String encodedPassport) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(encodedPassport)
                    .getPayload();

            return new PassportClaims(
                    claims.get(PassportConstants.PASSPORT_USER_ID).toString(),
                    Arrays.asList(
                            claims.get(PassportConstants.PASSPORT_ROLE).toString().split(",")
                    ),
                    claims.get(PassportConstants.PASSPORT_TRACE_ID).toString()
            );

        } catch (ExpiredJwtException e) {
            throw new InvalidPassportException("Expired passport");

        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidPassportException("Invalid passport");
        }
    }
}
