package com.tenco.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;

// DB 접근 해서 Connection 객체를 생성하는 역할의 클래스
// 싱글톤 패턴 : 프로그램 전체에서 단 하나의 인스턴스만을 존재하게 하는 기법
public class DBConnectionManager {

    // static : 클래스 레벨에서 하나만 유지하는 커넥션 풀
    private static final HikariDataSource dataSource;

    // static 초기화 블록 : 클래스가 메모리에 로딩될 때 단 1번만 실행
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/convenience_store");
        config.setUsername(System.getenv("DB_USER"));
        config.setPassword(System.getenv("DB_PASSWORD"));

        config.setMaximumPoolSize(10);       // 동시에 유지할 연결 최대 개수
        config.setMinimumIdle(3);            // 확보할 최소 유후 커넥션 수
        config.setConnectionTimeout(10000);  // 커넥션 할당 대기 시간 제한 (10초)
        config.setIdleTimeout(600000);       // 유후 커넥션 유지 시간(30초)

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // 애플리케이션 종료 시 커넥션 풀을 완전히 닫는다(메모리 누수 발생)
    public static void close() {
        if(dataSource != null && !dataSource.isClosed())
            dataSource.close();
    }

//    public static void main(String[] args) {
//        try {
//            DBConnectionManager.getConnection();
//            Thread.sleep(100000);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
