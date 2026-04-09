package com.tenco.dao;


import com.tenco.dto.Admin;
import com.tenco.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {
    public Admin login(String adminId, String password) throws SQLException {

        String sql = """
                    SELECT * FROM admins WHERE admin_id = ? AND password = ?;
                    """;

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, adminId);
            stmt.setString(2, password);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    return Admin.builder()
                            .adminId(rs.getString("admin_id"))
                            .name(rs.getString("name"))
                            .build();
                }
            }

            return null;
        }
    }
}
