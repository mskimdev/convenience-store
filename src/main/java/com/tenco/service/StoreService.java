package com.tenco.service;

import com.tenco.dao.AdminDAO;
import com.tenco.dao.ProductDAO;
import com.tenco.dao.SaleDAO;
import com.tenco.dto.Admin;
import com.tenco.dto.Product;
import com.tenco.dto.Sales;

import java.sql.SQLException;
import java.util.List;

public class StoreService {
    private final AdminDAO adminDAO = new AdminDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();

    private Admin currentAdmin = null;

    // 로그인
    public boolean login(String adminId, String password) throws SQLException{
        if(adminId.trim().isEmpty()){
            throw new SQLException("ID를 입력해주세요.");
        }

        if (password.trim().isEmpty()) {

            throw new SQLException("PASSWORD를 입력해주세요.");
        }

        Admin admin = adminDAO.login(adminId, password);
        if(admin == null){
            throw new SQLException("계정이 존재하지 않습니다.");
        }

        currentAdmin = admin;

        return true;
    }
    // 로그아웃
    public void logout() throws SQLException{
        if(currentAdmin == null){
            throw new SQLException("로그인되어있지 않습니다.");
        }

        currentAdmin = null;
    }

    // 로그인 상태 확인
    public boolean isLoggedIn(){
        return currentAdmin != null;
    }

    // 상품 목록
    public List<Product> getProductList() throws SQLException {
        List<Product> productList = productDAO.findAll();
        if(productList.isEmpty()){
            throw new SQLException("상품 목록이 존재하지 않습니다.");
        }

        return productList;
    }

    // 판매 처리
    public String processSale(String barcode, int quantity) throws SQLException{
        String result = "판매처리에 실패하였습니다.";
        if(barcode.trim().isEmpty()){
            throw new SQLException("바코드가 올바르지 않습니다.");
        }
        if(quantity < 0){
            throw new SQLException("수량이 올바르지 않습니다.");
        }

        Product product = productDAO.findByBarcode(barcode);
        if(product == null){
            throw new SQLException("바코드에 해당하는 상품이 존재하지 않습니다.");
        }
        if(saleDAO.processSale(product, quantity))
            result = "판매처리 성공!";

        return result;
    }

    // 재고 부족 판단
    public boolean isLowStock(Product product) throws SQLException{
        if(product == null){
            throw new SQLException("상품이 올바르지 않습니다.");
        }
        return !productDAO.findLowStock().isEmpty();
    }

    // 유통기한 임박 판단
    public boolean isNearExpiry(Product product) throws SQLException{
        if(product == null){
            throw new SQLException("상품이 올바르지 않습니다.");
        }

        return false;
        //return productDAO.findNearExpiry(product);
    }


    public Product findByBarcode(String barcode) throws SQLException{
        if(barcode.trim().isEmpty()){
            throw new SQLException("바코드가 올바르지 않습니다.");
        }
        return productDAO.findByBarcode(barcode);
    }

    public boolean insert(Product product) throws SQLException{
        if(product == null){
            throw new SQLException("상품이 올바르지 않습니다.");
        }

        return productDAO.insert(product);
    }

    public String update(Product product) throws SQLException{
        if(product == null){
            throw new SQLException("상품이 올바르지 않습니다.");
        }

        if(!productDAO.update(product)){
            return null;
        }

        return product.getName();
    }

    public boolean softDelete(int id) throws SQLException{
        if(id < 0 ){
            throw new SQLException("올바르지 않은 상품 ID");
        }

        return productDAO.softDelete(id);
    }

    public List<Product> findLowStock() throws SQLException{
        return productDAO.findLowStock();
    }

    public List<Product> findNearExpiry() throws SQLException{
        return productDAO.findNearExpiry();
    }

    public List<Sales> findTodaySales() throws SQLException{
        return saleDAO.findTodaySales();
    }


}
// 1,8801234560001,삼각김밥 참치,식품,1500.00,800.00,20,10,2026-04-12
