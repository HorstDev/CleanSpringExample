package com.example.batch;

import com.example.models.Order;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OrderProcessor implements ItemProcessor<Order, Order> {

    // Обработка единицы заказа
    // При выбрасывании исключения передаем заказ в OrderSkipPolicy
    @Override
    @NonNull
    public Order process(Order order) {
        if (order.getAmount() < 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной у записи с id = " + order.getId());
        }
        if (order.getOrderDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("У записи с id = " + order.getId() + " дата в будущем");
        }
        if (order.getAmount() > 10000) {
            order.setVip(true);
        }

        return order;
    }
}
