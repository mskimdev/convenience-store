package com.tenco.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
@Builder
public class Admin {
    private int id;
    private String admin_id;
    private String password;
    private String name;
}
