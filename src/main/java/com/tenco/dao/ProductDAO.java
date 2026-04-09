package com.tenco.dao;

import com.tenco.dto.Product;
import com.tenco.util.DBConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // 전체 목록
    public List<Product> findAll() throws SQLException {
        Connection conn = DBConnectionManager.getConnection();

        List<Product> productList = new ArrayList<>();
        String sql = """
                SELECT * FROM product;
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                productList.add(
                        Product.builder()
                                .id(rs.getInt("id"))
                                .barcode(rs.getString("barcode"))
                                .name(rs.getString("name"))
                                .category(rs.getString("category"))
                                .price(rs.getBigDecimal("price"))
                                .cost(rs.getBigDecimal("cost"))
                                .stock(rs.getInt("stock"))
                                .expireDate(rs.getDate("expire_date").toLocalDate())
                                .isActive(rs.getBoolean("is_active"))
                                .build()
                );
            }
        }

        return productList;
    }

    // 바코드로 1건 조회
    public Product findByBarcode(String barcode) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        String sql = """
                SELECT * FROM product WHERE barcode = ? AND is_active = true
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, barcode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Product.builder()
                            .id(rs.getInt("id"))
                            .barcode(rs.getString("barcode"))
                            .name(rs.getString("name"))
                            .category(rs.getString("category"))
                            .price(rs.getBigDecimal("price"))
                            .cost(rs.getBigDecimal("cost"))
                            .stock(rs.getInt("stock"))
                            .expireDate(rs.getDate("expire_date").toLocalDate())
                            .isActive(rs.getBoolean("is_active"))
                            .build();
                }
            }
        }

        return null;
    }

    // 상픔 등록
    public boolean insert(Product product) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        String sql = """
                INSERT INTO product (barcode, name, category, price, cost, stock, min_stock, expire_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getBarcode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getCategory());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setBigDecimal(5, product.getCost());
            stmt.setInt(6, product.getStock());
            stmt.setInt(7, product.getMinStock());
            stmt.setDate(8, product.getExpireDate() == null ?
                    null : Date.valueOf(product.getExpireDate()));

            return stmt.executeUpdate() > 0;
        }
    }

    // 상품 수정
    public boolean update(Product product) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        String sql = """
                UPDATE product
                SET barcode = ?, name = ?,
                category = ?, price = ?, cost = ?, stock = ?,
                min_stock = ?, expire_date = ?, is_active = ?
                WHERE barcode = ?;
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getBarcode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getCategory());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setBigDecimal(5, product.getCost());
            stmt.setInt(6, product.getStock());
            stmt.setInt(7, product.getMinStock());
            stmt.setDate(8, product.getExpireDate() == null ?
                    null : Date.valueOf(product.getExpireDate()));
            stmt.setBoolean(9, product.isActive());
            stmt.setString(10, product.getBarcode());

            return stmt.executeUpdate() > 0;
        }
    }

    // 소프트 삭제
    public boolean softDelete(int id) throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        String sql = """
                UPDATE product SET is_active = false WHERE id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // 재고 부족 상품
    public List<Product> findLowStock() throws SQLException {
        Connection conn = DBConnectionManager.getConnection();
        String sql = """
                SELECT * FROM product WHERE stock < min_stock AND is_active = true;
                """;

        List<Product> productList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                productList.add(
                        Product.builder()
                                .id(rs.getInt("id"))
                                .barcode(rs.getString("barcode"))
                                .name(rs.getString("name"))
                                .category(rs.getString("category"))
                                .price(rs.getBigDecimal("price"))
                                .cost(rs.getBigDecimal("cost"))
                                .stock(rs.getInt("stock"))
                                .minStock(rs.getInt("min_stock"))
                                .expireDate(rs.getDate("expire_date").toLocalDate())
                                .isActive(rs.getBoolean("is_active"))
                                .build()
                );
            }
        }
        return productList;
    }

    // 유통기한 임박 상품
    public List<Product> findNearExpiry() throws SQLException {
        String sql = """
                SELECT * from product WHERE expire_date - CURRENT_DATE <= 3 AND is_active = true
                """;

        List<Product> productList = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                productList.add(
                        Product.builder()
                                .id(rs.getInt("id"))
                                .barcode(rs.getString("barcode"))
                                .name(rs.getString("name"))
                                .category(rs.getString("category"))
                                .price(rs.getBigDecimal("price"))
                                .cost(rs.getBigDecimal("cost"))
                                .stock(rs.getInt("stock"))
                                .minStock(rs.getInt("min_stock"))
                                .expireDate(rs.getDate("expire_date").toLocalDate())
                                .isActive(rs.getBoolean("is_active"))
                                .build()
                );
            }
        }
        return productList;
    }
}
