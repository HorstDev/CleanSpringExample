package com.example.models;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Order {
    private Long id;
    private String customerName;
    private LocalDate orderDate;
    private double amount;
    private boolean vip;
}
