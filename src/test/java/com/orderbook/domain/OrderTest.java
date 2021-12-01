package com.orderbook.domain;

import com.orderbook.constant.OrderSide;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTest {

    private static Order order;

    @BeforeAll
    static void setUpAll() {
        order = new Order("1", new BigDecimal("100"), new BigInteger("10"), OrderSide.ASK);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    static void testToString() {
        String orderString = "30";
        assertEquals(orderString, order.toString());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    public void testCreateOrderShouldThrowIllegalArgumentExceptionWhenOrderIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order(null, new BigDecimal("100"), new BigInteger("10"), OrderSide.ASK);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenPriceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", null, new BigInteger("10"), OrderSide.ASK);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenQuantityIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", new BigDecimal("100"), null, OrderSide.ASK);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenOrderSideIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", new BigDecimal("100"), new BigInteger("10"), null);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenPriceIsLessThanOrEqualToZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", new BigDecimal(-1), new BigInteger("10"), OrderSide.ASK);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", BigDecimal.ZERO, new BigInteger("10"), OrderSide.ASK);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenQuantityIsLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", new BigDecimal("100"), new BigInteger("-1"), OrderSide.ASK);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testGetOrderId() {
        assertEquals("1", order.getOrderId());
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testGetQuantity() {
        BigInteger quantity = new BigInteger("10");
        order = new Order("1", new BigDecimal("100"), new BigInteger("10"), OrderSide.ASK);
        assertEquals(quantity, order.getQuantity());
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void testGetPrice() {
        BigDecimal price = new BigDecimal("100");
        assertEquals(price, order.getPrice());
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void testGetSide() {
        assertEquals(OrderSide.ASK, order.getSide());
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    void testSetQuantity() {
        BigInteger quantity = new BigInteger("30");
        order.setQuantity(quantity);
        assertEquals(quantity, order.getQuantity());
    }

    @Test
    @org.junit.jupiter.api.Order(13)
    void testSetQuantityShouldThrowIllegalArgumentExceptionWhenQuantityIsLessThanZero() {
        BigInteger quantity = new BigInteger("-1");
        assertThrows(IllegalArgumentException.class, () -> {
            order.setQuantity(quantity);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(14)
    void testSetPrice() {
        BigDecimal price = new BigDecimal("10.45");
        order.setPrice(price);
        assertEquals(0, order.getPrice().compareTo(price));
    }

    @Test
    @org.junit.jupiter.api.Order(15)
    void testSetSide() {
        OrderSide side = OrderSide.ASK;
        order.setSide(side);
        assertEquals(side, order.getSide());
    }

    @Test
    @org.junit.jupiter.api.Order(16)
    void testSetOrderId() {
        String orderId = "1111";
        order.setOrderId(orderId);
        assertEquals(orderId, order.getOrderId());
    }

    @Test
    @org.junit.jupiter.api.Order(17)
    void testIsExecutableOrderShouldNotBeExecutableWhenOrderQuantityIsZero() {
        order.setQuantity(BigInteger.ZERO);
        assertFalse(order.isExecutable());
    }

    @Test
    @org.junit.jupiter.api.Order(18)
    void testIsExecutableOrderShouldBeExecutableWhenOrderQuantityIsGreaterThanZero() {
        order.setQuantity(new BigInteger("10"));
        assertTrue(order.isExecutable());
    }

    @Test
    @org.junit.jupiter.api.Order(19)
    void testIsExecutableBuyOrderShouldBeExecutableWhenQuantityIsGreaterThanZeroAndComparingPriceIsLessThanOrderPrice() {
        BigDecimal comparingPrice = new BigDecimal("80");
        order.setQuantity(new BigInteger("10"));
        order.setPrice(new BigDecimal("90"));
        order.setSide(OrderSide.BID);

        assertTrue(order.isExecutable(comparingPrice));
    }

    @Test
    @org.junit.jupiter.api.Order(20)
    void testIsExecutableBuyOrderShouldNotBeExecutableWhenQuantityIsGreaterThanZeroAndComparingPriceIsGreaterOrderPrice() {
        BigDecimal comparingPrice = new BigDecimal("80");
        order.setQuantity(new BigInteger("10"));
        order.setPrice(new BigDecimal("70"));
        order.setSide(OrderSide.BID);

        assertFalse(order.isExecutable(comparingPrice));
    }

    @Test
    @org.junit.jupiter.api.Order(21)
    void testIsExecutableSellOrderShouldNotBeExecutableWhenQuantityIsGreaterThanZeroAndComparingPriceIsLessThanOrderPrice() {
        BigDecimal comparingPrice = new BigDecimal("80");
        order.setQuantity(new BigInteger("10"));
        order.setPrice(new BigDecimal("90"));
        order.setSide(OrderSide.ASK);

        assertFalse(order.isExecutable(comparingPrice));
    }

    @Test
    @org.junit.jupiter.api.Order(22)
    void testIsExecutableSellOrderShouldBeExecutableWhenQuantityIsGreaterThanZeroAndComparingPriceIsGreaterOrderPrice() {
        BigDecimal comparingPrice = new BigDecimal("80");
        order.setQuantity(new BigInteger("10"));
        order.setPrice(new BigDecimal("70"));
        order.setSide(OrderSide.ASK);

        assertTrue(order.isExecutable(comparingPrice));
    }

}