package org.cst8277.snookmichael.assigment4.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import org.cst8277.snookmichael.assigment4.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUmsRepository implements UmsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        byte[] userIdBytes = rs.getBytes("users.id");
        if (userIdBytes != null && userIdBytes.length == 16) { // UUIDs are 128 bits (16 bytes)
            user.setId(DaoHelper.bytesArrayToUuid(userIdBytes));
        } else {
            System.err.println("Invalid UUID byte array for users.id: " + Arrays.toString(userIdBytes));
        }
        // Map other fields similarly with checks if needed
        user.setName(rs.getString("users.name"));
        user.setEmail(rs.getString("users.email"));
        user.setCreated(rs.getInt("users.created"));
        user.setGithubId(rs.getString("users.github_id"));
        user.setLastSession(new LastSession(
                rs.getInt("last_visit.in"),
                rs.getInt("last_visit.out")
        ));
        user.setRoles(Collections.singletonList(new Roles(
                DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")),
                rs.getString("roles.name"),
                rs.getString("roles.description")
        )));
        return user;
    }

    @Override
    public User findUserByID(UUID userId) {
        try {
            List<User> users = jdbcTemplate.query(
                    Constants.GET_USER_BY_ID_FULL,
                    new UserRowMapper(),
                    DaoHelper.uuidToBytesArray(userId) // Convert UUID to byte array
            );
            if (!users.isEmpty()) {
                User user = users.get(0);
                for (User tempUser : users) {
                    if (tempUser.getRoles() != null && !tempUser.getRoles().isEmpty()) {
                        user.addRole(tempUser.getRoles().get(0));
                    }
                }
                return user;
            }
        } catch (EmptyResultDataAccessException e) {
            // Handle user not found
        }
        return null;
    }

    @Override
    public UUID createUser(User user) {
        long timestamp = Instant.now().getEpochSecond();
        Map<String, Roles> roles = this.findAllRoles();
        UUID userId = UUID.randomUUID();

        // Check if the email already exists
        if (findUserByEmail(user.getEmail()) != null) {
            System.err.println("Error creating user: Email already exists.");
            return null;
        }

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO users (id, name, email, created, last_visit_id, github_id) VALUES (?, ?, ?, ?, ?, ?)"
                );
                ps.setBytes(1, DaoHelper.uuidToBytesArray(userId));
                ps.setString(2, user.getName());
                ps.setString(3, user.getEmail());
                ps.setLong(4, timestamp);
                ps.setObject(5, null, Types.BINARY);  // Assuming this is nullable
                ps.setString(6, user.getGithubId());
                return ps;
            });

            for (Roles role : user.getRoles()) {
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)"
                    );
                    ps.setBytes(1, DaoHelper.uuidToBytesArray(userId));
                    ps.setBytes(2, DaoHelper.uuidToBytesArray(roles.get(role.getRole()).getRoleId()));
                    return ps;
                });
            }
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return null;
        }

        System.out.println("User created with ID: " + userId + ", Name: " + user.getName()
                + ", Email: " + user.getEmail());
        return userId;
    }

    @Override
    public int deleteUser(UUID userId) {
        return jdbcTemplate.update(Constants.DELETE_USER, userId.toString());
    }

    @Override
    public Map<UUID, User> findAllUsers() {
        return Map.of();
    }

    @Override
    public Map<String, Roles> findAllRoles() {
        Map<String, Roles> roles = new HashMap<>();
        jdbcTemplate.query(Constants.GET_ALL_ROLES, rs -> {
            Roles role = new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")), rs.getString("roles.name"),
                    rs.getString("roles.description"));
            roles.put(rs.getString("roles.name"), role);
        });
        return roles;
    }

    public void saveToken(String githubId, String token, LocalDateTime expirationTime) {
        String sql = "INSERT INTO tokens (github_id, token, expiration_time) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE token = VALUES(token), expiration_time = VALUES(expiration_time)";
        jdbcTemplate.update(sql, githubId, token, expirationTime);
    }

    public void deleteExpiredTokens(LocalDateTime now) {
        String sql = "DELETE FROM tokens WHERE expiration_time < ?";
        jdbcTemplate.update(sql, now);
    }

    public LocalDateTime getTokenExpirationTime(String token) {
        String sql = "SELECT expiration_time FROM tokens WHERE token = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{token}, LocalDateTime.class);
    }

    public String getRolesByToken(String token) {
        String sql = "SELECT GROUP_CONCAT(r.role_name) AS roles " +
                "FROM roles r " +
                "JOIN user_roles ur ON r.id = ur.role_id " +
                "JOIN tokens t ON ur.user_id = t.github_id " +
                "WHERE t.token = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{token}, String.class);
    }

    public User findUserByGitHubId(String githubId) {
        String sql = "SELECT * FROM users WHERE github_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{githubId}, new BeanPropertyRowMapper<>(User.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User findUserByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject(
                    Constants.GET_USER_BY_EMAIL,
                    new UserRowMapper(),
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean userExists(String email, String githubId) {
        return false;
    }
}