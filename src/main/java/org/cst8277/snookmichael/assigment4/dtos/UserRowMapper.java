package org.cst8277.snookmichael.assigment4.dtos;

import java.sql.ResultSet;
   import java.sql.SQLException;
   import java.util.UUID;

import org.cst8277.snookmichael.assigment4.dao.DaoHelper;
import org.cst8277.snookmichael.assigment4.dtos.User;
   import org.springframework.jdbc.core.RowMapper;

   public class UserRowMapper implements RowMapper<User> {
       @Override
       public User mapRow(ResultSet rs, int rowNum) throws SQLException {
           User user = new User();
           
           // Other fields mapping
           
           byte[] uuidBytes = rs.getBytes("id");
           if (uuidBytes != null && uuidBytes.length == 16) {
               user.setId(DaoHelper.bytesArrayToUuid(uuidBytes));
           }

           return user;
       }
   }