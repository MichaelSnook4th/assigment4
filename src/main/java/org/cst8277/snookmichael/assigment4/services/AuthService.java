package org.cst8277.snookmichael.assigment4.services;

import org.cst8277.snookmichael.assigment4.dao.UmsRepository;
import org.cst8277.snookmichael.assigment4.dtos.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final String DEFAULT_USER_NAME = "GitHub User";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UmsRepository umsRepository;

    public AuthService(UmsRepository umsRepository) {
        this.umsRepository = umsRepository;
    }

    public void processOAuth2Token(OAuth2AuthenticationToken authenticationToken, String jwt) {
        Map<String, Object> attributes = extractGitHubUserAttributes(authenticationToken);

        String githubId = attributes.get("id").toString();
        String email = attributes.get("email").toString();

        saveGitHubUser(githubId, email, DEFAULT_USER_NAME);
    }

    private Map<String, Object> extractGitHubUserAttributes(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        if (attributes.get("id") == null || attributes.get("email") == null) {
            String errorMessage = "Required attributes 'id' and/or 'email' are missing from OAuth2 token."
                    + (attributes.get("id") == null ? " Missing 'id'." : "")
                    + (attributes.get("email") == null ? " Missing 'email'." : "");
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        return attributes;
    }

    private void saveGitHubUser(String githubId, String email, String name) {
        if (!umsRepository.userExists(email, githubId)) {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setName(name);
            user.setEmail(email);
            user.setGithubId(githubId);
            user.setCreated((int)(System.currentTimeMillis() / 1000));
            user.setLastVisitId(UUID.randomUUID());
            umsRepository.createUser(user);
            LOGGER.info("New GitHub user created: {}", email);
        } else {
            LOGGER.info("GitHub user already exists: {}", email);
        }
    }

    public String generateAndStoreToken(OAuth2AuthenticationToken token) {
        String sessionToken = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);
        umsRepository.saveToken(token.getPrincipal().getAttributes().get("id").toString(),
                sessionToken, expirationTime);
        LOGGER.info("Session token saved for GitHub user: {}",
                token.getPrincipal().getAttributes().get("email").toString());
        return sessionToken;
    }

    public void invalidateExpiredTokens() {
        umsRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    public boolean isTokenValid(String token) {
        LocalDateTime expirationTime = umsRepository.getTokenExpirationTime(token);
        return expirationTime != null && expirationTime.isAfter(LocalDateTime.now());
    }

    public String getRolesByToken(String token) {
        return umsRepository.getRolesByToken(token);
    }
}