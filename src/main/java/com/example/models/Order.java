package com.example.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Builder
public class Order {
    private Long id;
    private String customerName;
    private LocalDate orderDate;
    private double amount;
    private boolean vip;
}
