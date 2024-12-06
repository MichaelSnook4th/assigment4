package org.cst8277.snookmichael.assigment4.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    UUID roleId;
    String role;
    String description;

    public Object getRoleName() {
        return null;
    }
}
