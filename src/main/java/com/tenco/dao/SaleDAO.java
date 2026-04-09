package com.tenco.dao;

import com.tenco.dto.Product;
import com.tenco.dto.Sales;
import com.tenco.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    // 판매 처리(트랜잭션)
    public boolean processSale(Product product, int quantity) throws SQLException {
        Connection conn = null;
        boolean flag = false;
        try {
            // 판매 가능한지 확인 (재고확인)
            conn = DBConnectionManager.getConnection();
            String selectSql = """
                    SELECT stock, is_active FROM product WHERE id = ? AND stock >= ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                conn.setAutoCommit(false);

                stmt.setInt(1, product.getId());
                stmt.setInt(2, quantity);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.getBoolean("is_active")) {
                        throw new SQLException("판매 불가(이미 삭제된 상품)");
                    }

                    if (rs.getInt("stock") < quantity) {
                        throw new SQLException("판매 불가(재고 부족)");
                    }
                }
            }

            // sales 테이블 insert
            String insertSql = """
                    INSERT INTO sales (product_id, quantity, unit_price) VALUES (?, ?, ?)
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, product.getId());
                stmt.setInt(2, quantity);
                stmt.setBigDecimal(3, product.getPrice());
                stmt.executeUpdate();
            }

            // product 테이블 update
            String updateSql = """
                    UPDATE product SET stock = stock - ? WHERE id = ?
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, product.getStock() - quantity);
                stmt.setInt(2, product.getId());

                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(false);
                conn.close();
            }
        }
        return true;
    }

    // 오늘 매출 집계
    public List<Sales> findTodaySales() throws SQLException {
        String sql = """
                SELECT
                    p.name as product_name,
                    sum(quantity) as total_quantity,
                    sum((p.price - p.cost) * quantity) as total_sales
                FROM sales s
                JOIN product p on p.id = s.product_id
                WHERE date(sold_at) = CURRENT_DATE
                GROUP BY p.id, date(sold_at);
                """;
        List<Sales> salesList = new ArrayList<>();

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next()){
                salesList.add(
                        Sales.builder()
                                .productName(rs.getString("product_name"))
                                .totalQuantity(rs.getInt("total_quantity"))
                                .totalPrice(rs.getBigDecimal("total_sales"))
                                .build());
            }
        }

        return salesList;
    }
}
