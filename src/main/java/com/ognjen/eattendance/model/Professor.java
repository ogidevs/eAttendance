package com.ognjen.eattendance.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Professor extends User {
    private String title;
}