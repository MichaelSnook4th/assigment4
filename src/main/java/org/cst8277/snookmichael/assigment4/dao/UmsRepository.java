package org.cst8277.snookmichael.assigment4.dao;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.cst8277.snookmichael.assigment4.dtos.Roles;
import org.cst8277.snookmichael.assigment4.dtos.User;

public interface UmsRepository {

    Map<UUID, User> findAllUsers();

    Map<String, Roles> findAllRoles();

    User findUserByID(UUID userId);

    UUID createUser(User user);

    int deleteUser(UUID userId);

    void saveToken(String userId, String sessionToken, LocalDateTime expirationTime);

    void deleteExpiredTokens(LocalDateTime now);

    LocalDateTime getTokenExpirationTime(String token);

    String getRolesByToken(String token);

    User findUserByGitHubId(String githubId);

    User findUserByEmail(String email);

    boolean userExists(String email, String githubId);
}
