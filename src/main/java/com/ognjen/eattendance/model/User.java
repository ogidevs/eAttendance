package com.ognjen.eattendance.model;

import lombok.Data;

@Data
public abstract class User {
    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}