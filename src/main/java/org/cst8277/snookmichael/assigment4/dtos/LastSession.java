package org.cst8277.snookmichael.assigment4.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastSession {
    int lastLoginTimeStamp;
    int lastLogoutTimeStamp;
}
