package org.cst8277.snookmichael.assigment4.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String email;
    private int created;
    private String githubId; // Added field for GitHub ID
    private List<Roles> roles = new ArrayList<>();
    private LastSession lastSession;

    public void addRole(Roles role) {
        this.roles.add(role);
    }

    public void setLastVisitId(UUID uuid) {
    }
}
