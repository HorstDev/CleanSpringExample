package com.example.batch;

import com.example.models.Order;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderFieldsMapper implements FieldSetMapper<Order> {

    @Override
    public Order mapFieldSet(FieldSet fieldSet) throws BindException {
        LocalDate formattedDate = LocalDate.parse(fieldSet.readString("orderDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return Order.builder()
                .id(fieldSet.readLong("id"))
                .customerName(fieldSet.readString("customerName"))
                .orderDate(formattedDate)
                .amount(fieldSet.readDouble("amount"))
                .build();
    }
}
