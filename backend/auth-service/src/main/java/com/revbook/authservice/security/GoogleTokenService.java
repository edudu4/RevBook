package com.revbook.authservice.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Valida o ID token emitido pelo Google Identity Services: assinatura, emissor, audience
 * (nosso Client ID) e expiração. Sem isso, qualquer cliente poderia forjar um login mandando
 * um email arbitrário direto pro endpoint.
 */
@Service
public class GoogleTokenService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenService(@Value("${revbook.google.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public DadosUsuarioGoogle verificar(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new TokenGoogleInvalidoException("Token do Google inválido ou expirado");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            return new DadosUsuarioGoogle(
                    payload.getSubject(),
                    payload.getEmail(),
                    (String) payload.get("name"),
                    (String) payload.get("picture"));
        } catch (GeneralSecurityException | IOException | IllegalArgumentException ex) {
            throw new TokenGoogleInvalidoException("Não foi possível validar o token do Google");
        }
    }

    public record DadosUsuarioGoogle(String googleId, String email, String nome, String avatar) {
    }
}
