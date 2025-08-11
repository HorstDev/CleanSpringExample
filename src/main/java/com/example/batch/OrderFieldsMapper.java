package com.example.batch;

import com.example.models.Order;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderFieldsMapper implements FieldSetMapper<Order> {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String ID = "id";
    public static final String CUSTOMER_NAME = "customerName";
    public static final String AMOUNT = "amount";

    @Override
    public Order mapFieldSet(FieldSet fieldSet) {
        LocalDate formattedDate = LocalDate.parse(fieldSet.readString("orderDate"), DateTimeFormatter.ofPattern(YYYY_MM_DD));

        return Order.builder()
                .id(fieldSet.readLong(ID))
                .customerName(fieldSet.readString(CUSTOMER_NAME))
                .orderDate(formattedDate)
                .amount(fieldSet.readDouble(AMOUNT))
                .build();
    }
}
