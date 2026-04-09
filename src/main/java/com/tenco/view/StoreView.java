package com.tenco.view;

import com.tenco.dto.Product;
import com.tenco.dto.Sales;
import com.tenco.service.StoreService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class StoreView {
    private static final Scanner sc = new Scanner(System.in);
    private static final StoreService service = new StoreService();

    public static void main(String[] args) {
        try {
            while (true) {
                printMenu();
                int input = Integer.parseInt(sc.nextLine());

                switch (input) {
                    case 1: // 로그인
                        login();
                        break;
                    case 2: //로그아웃
                        logout();
                        break;
                    case 3: // 상품 목록 조회
                        getProductList();
                        break;
                    case 4: // 바코드 상품 조회
                        getProductByBarcode();
                        break;
                    case 5: // 상품 등록
                        insert();
                        break;
                    case 6: // 상품 수정
                        updateProduct();
                        break;
                    case 7: // 상품 삭제
                        softDelete();
                        break;
                    case 8: // 재고 부족 알림
                        findLowStock();
                        break;
                    case 9: // 유통기한 임박 알림
                        getNearExpiry();
                        break;
                    case 10: // 판매 처리
                        processSale();
                        break;
                    case 11: // 오늘 매출 조회
                        findTodaySales();
                        break;
                    case 0: // 종료
                        System.out.println("프로그램을 종료합니다.");
                        sc.close();
                        System.exit(0);
                }

            }
        } catch (SQLException e) {
            System.out.println("오류발생 : " + e.getMessage());
        } finally{
            sc.close();
        }
    }

    // 1. 로그인
    private static void login() throws SQLException {
        System.out.print("아이디 입력 : ");
        String id = sc.nextLine();
        if (id.trim().isEmpty()) {
            System.out.println("id를 입력해주세요.");
            return;
        }
        System.out.print("패스워드 입력 : ");
        String pwd = sc.nextLine();
        if (pwd.trim().isEmpty()) {
            System.out.println("password를 입력해주세요.");
            return;
        }

        if (service.login(id, pwd)) {
            System.out.println("성공적으로 로그인 되셨습니다!");
        } else {
            System.out.println("로그인에 실패하셨습니다.");
        }
    }

    // 2. 로그아웃
    private static void logout() throws SQLException {
        if (service.isLoggedIn()) {
            service.logout();
            System.out.println("로그아웃 성공 !");
        } else {
            System.out.println("로그인되어 있지 않습니다.");
        }
    }

    // 3. 상품 목록 조회
    private static void getProductList() throws SQLException {
        if (!service.isLoggedIn()) {
            System.out.println("로그인 후 사용해주세요.");
            return;
        }
        List<Product> productList = service.getProductList();
        if (productList.isEmpty()) {
            System.out.println("상품목록이 존재하지 않습니다.");
        }

        System.out.println("-------------------------- 상품 목록 --------------------------------");
        productList.forEach(System.out::println);
        System.out.println("---------------------------------------------------------------------");
    }

    // 4. 바코드 상품 검색
    private static void getProductByBarcode() throws SQLException {
        if (!service.isLoggedIn()) {
            System.out.println("로그인 후 사용해주세요.");
            return;
        }
        System.out.print("바코드 입력 : ");
        String barcode = sc.nextLine();
        if (barcode.trim().isEmpty()) {
            System.out.println("바코드가 올바르게 인식되지 않았습니다.");
            return;
        }
        Product product = service.findByBarcode(barcode);
        System.out.println("---- 바코드 인식 결과 ----");
        System.out.println(product);
    }

    // 5. 상품 등록
    private static void insert() throws SQLException {
        if (!service.isLoggedIn()) {
            System.out.println("로그인 후 사용해주세요.");
            return;
        }
        System.out.println("---- 상품 등록 ----");
        System.out.print("바코드 인식 : ");
        String barcode = sc.nextLine();
        if (barcode.trim().isEmpty()) {
            System.out.println("바코드가 올바르게 인식되지 않았습니다.");
            return;
        }
        System.out.print("상품 이름 : ");
        String name = sc.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("상품 이름은 공란일수 없습니다.");
            return;
        }
        System.out.print("카테고리 : ");
        String category = sc.nextLine();
        if (category.trim().isEmpty()) {
            System.out.println("카테고리는 공란일 수 없습니다.");
            return;
        }
        System.out.print("가격 : ");
        BigDecimal price = sc.nextBigDecimal(); sc.nextLine();
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("가격은 0원 이하일 수 없습니다.");
            return;
        }
        System.out.print("원가 : ");
        BigDecimal cost = sc.nextBigDecimal(); sc.nextLine();
        if (cost.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("원가가 0원 이하일 수 없습니다.");
            return;
        }
        System.out.print("재고 : ");
        int stock = Integer.parseInt(sc.nextLine());
        if (stock < 0) {
            System.out.println("재고가 마이너스 일 수는 없습니다.");
            return;
        }
        System.out.print("최소 재고 : ");
        int minStock = Integer.parseInt(sc.nextLine());
        if (minStock <= 0) {
            System.out.println("최소 재고가 0 이하일 수 없습니다.");
            return;
        }
        System.out.print("유통기한 입력 (형식 : yyyy-mm-dd) : ");
        LocalDate expireDate = LocalDate.parse(sc.nextLine());
        // ??
        Product product = Product.builder()
                .barcode(barcode)
                .name(name)
                .category(category)
                .price(price)
                .cost(cost)
                .stock(stock)
                .minStock(minStock)
                .expireDate(expireDate)
                .build();

        if(service.insert(product)){
            System.out.println(name + " 이(가) 성공적으로 등록되었습니다.");
        } else{
            System.out.println("제품등록에 실패하였습니다.");
        }
    }

    // 6. 상품 수정
    private static void updateProduct() throws SQLException{
        if (!service.isLoggedIn()) {
            System.out.println("로그인 후 사용해주세요.");
            return;
        }

        System.out.println("---- 상품 수정 ----");
        System.out.print("수정할 상품의 바코드를 인식해주세요 : "); String barcode = sc.nextLine();
        if(barcode.trim().isEmpty()){
            System.out.println("바코드가 제대로 인식되지 않았습니다.");
            return;
        }

        Product product = service.findByBarcode(barcode);
        if(product == null){
            System.out.println("해당하는 바코드의 상품이 존재하지 않습니다.");
            return;
        }
        while(true){
            System.out.println("--- 바코드 인식 결과 ---");
            System.out.println(product);
            System.out.println("-----------------------");
            System.out.println("수정을 원하시는 항목을 선택해주세요.");
            System.out.println("1. 제품명");
            System.out.println("2. 카테고리");
            System.out.println("3. 가격");
            System.out.println("4. 원가");
            System.out.println("5. 재고");
            System.out.println("6. 최소 재고");
            System.out.println("7. 유통기한");
            System.out.println("0. 수정 종료");
            int choice = Integer.parseInt(sc.nextLine());
            switch(choice){
                case 1:
                    System.out.println("기존 제품명 : " + product.getName());
                    System.out.print("수정하실 제품명 : "); String name = sc.nextLine();
                    if(name.trim().isEmpty()){
                        System.out.println("제품명이 올바르지 않습니다.");
                        break;
                    }
                    product.setName(name);
                    break;
                case 2:
                    System.out.println("기존 카테고리 : " + product.getCategory());
                    System.out.print("수정하실 카테고리 : "); String category = sc.nextLine();
                    if(category.trim().isEmpty()){
                        System.out.println("카테고리가 올바르지 않습니다.");
                        break;
                    }
                    product.setCategory(category);
                    break;
                case 3:
                    System.out.println("기존 가격 : " + product.getPrice());
                    System.out.print("수정하실 가격 : "); BigDecimal price = sc.nextBigDecimal(); sc.nextLine();
                    if(price.compareTo(BigDecimal.ZERO) <= 0){
                        System.out.println("가격이 0원 이하일 수 없습니다.");
                        break;
                    }
                    product.setPrice(price);
                    break;
                case 4:
                    System.out.println("기존 원가 : " + product.getCost());
                    System.out.print("수정하실 원가 : "); BigDecimal cost = sc.nextBigDecimal(); sc.nextLine();
                    if(cost.compareTo(BigDecimal.ZERO) <= 0){
                        System.out.println("원가가 0원 이하일 수 없습니다.");
                        break;
                    }
                    product.setCost(cost);
                    break;
                case 5:
                    System.out.println("기존 재고 : " + product.getStock());
                    System.out.print("수정하실 재고 : "); int stock = Integer.parseInt(sc.nextLine());
                    if(stock < 0){
                        System.out.println("재고가 마이너스일 수 없습니다.");
                        break;
                    }
                    product.setStock(stock);
                    break;
                case 6:
                    System.out.println("기존 최소 재고 : " + product.getMinStock());
                    System.out.print("수정하실 최소 재고 : "); int minStock = Integer.parseInt(sc.nextLine());
                    if(minStock <= 0){
                        System.out.println("최소 재고가 0이하 일 수 없습니다.");
                        break;
                    }
                    product.setMinStock(minStock);
                    break;
                case 7:
                    System.out.println("기존 유통기한 : " + product.getExpireDate());
                    System.out.print("수정하실 유통기한 : "); LocalDate expireDate = LocalDate.parse(sc.nextLine());
                    product.setExpireDate(expireDate);
                    break;
                case 0:
                    return;
            }

            String updateName = service.update(product);
            if(updateName == null){
                System.out.println("상품 수정 실패");
                return;
            }
            System.out.println(updateName + "상품이 성공적으로 수정되었습니다.");
        }

    }

    // 7. 상품 삭제
    private static void softDelete() throws SQLException{
        if(!service.isLoggedIn()){
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        System.out.print("삭제할 상품의 바코드를 읽혀주세요 : ");
        String barcode = sc.nextLine();
        if(barcode.trim().isEmpty()){
            System.out.println("상품의 바코드가 올바르지 않습니다.");
            return;
        }

        Product product = service.findByBarcode(barcode);
        if(product == null){
            System.out.println("해당하는 상품이 존재하지 않습니다.");
            return;
        }

        if(!service.softDelete(product.getId())){
            System.out.println("상품 삭제에 실패했습니다.");
            return;
        }

        System.out.println("(" + product.getName() + ") 이 성공적으로 삭제되었습니다.");
    }

    // 8. 재고 부족 상품
    private static void findLowStock() throws SQLException{
        if(!service.isLoggedIn()){
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        List<Product> productList = service.findLowStock();

        System.out.println("------------- 재고 부족 상품 -----------------");
        productList.forEach(System.out::println);
        System.out.println("---------------------------------------------");
    }

    // 9. 유통기한 임박 상품
    private static void getNearExpiry() throws SQLException {
        if(!service.isLoggedIn()){
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        List<Product> productList = service.findNearExpiry();
        if(productList.isEmpty()){
            System.out.println("유통기한이 임박한 제품이 없습니다.");
            return;
        }

        System.out.println("-------------- 유통기한 임박 상품 ----------------");
        productList.forEach(System.out::println);
        System.out.println("---------------------------------------------------");
    }


    // 10. 판매 처리
    private static void processSale() throws SQLException{
        if(!service.isLoggedIn()){
            System.out.println("로그인 후 이용해주세요.");
            return;
        }

        System.out.print("판매처리할 상품의 바코드를 읽혀주세요 : ");
        String barcode = sc.nextLine();
        if(barcode.trim().isEmpty()){
            System.out.println("바코드가 잘못입력되었습니다.");
            return;
        }
        System.out.print("수량을 입력해주세요 : ");
        int quantity = Integer.parseInt(sc.nextLine());
        if(quantity <= 0){
            System.out.println("판매가 0개이하일수는 없습니다.");
            return;
        }

        Product product = service.findByBarcode(barcode);
        if(product == null){
            System.out.println("해당하는 상품이 존재하지 않습니다.");
            return;
        }

        String result = service.processSale(barcode, quantity);
        if(result == null){
            System.out.println("판매처리 실패");
            return;
        }

        System.out.println("("+product.getName()+") " + result);
    }

    // 11. 오늘 매출 조회
    private static void findTodaySales() throws SQLException{
        if(!service.isLoggedIn()){
            System.out.println("로그인 후 사용해주세요.");
        }

        List<Sales> salesList = service.findTodaySales();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for(Sales sales : salesList){
            System.out.print("상품명 : " + sales.getProductName());
            System.out.print("  ");
            System.out.print("전체 판매량 : "+ sales.getTotalQuantity());
            System.out.print("  ");
            System.out.print("마진 : " + sales.getTotalPrice());
            totalPrice = totalPrice.add(sales.getTotalPrice());
            System.out.println();
        }
        System.out.println("오늘 전체 마진 : " + totalPrice);
    }

    public static void printMenu() {
        System.out.println("\n------ 재고 관리 시스템 ------");
        if(!service.isLoggedIn())
            System.out.println("1. 로그인");
        if(service.isLoggedIn()){
            System.out.println("2. 로그아웃");
            System.out.println("3. 상품 목록 조회");
            System.out.println("4. 바코드 상품 검색");
            System.out.println("5. 상품 등록");
            System.out.println("6. 상품 수정");
            System.out.println("7. 상품 삭제");
            System.out.println("8. 재고 부족 알림");
            System.out.println("9. 유통기한 임박 알림");
            System.out.println("10. 판매 처리");
            System.out.println("11. 오늘 매출 조회");
        }
        System.out.println("0. 프로그램 종료");
        System.out.println("---------------------------------");
        System.out.print("입력 : ");
    }


}
