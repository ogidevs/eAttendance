package com.ognjen.eattendance.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends User {
    private String indexNumber;
}