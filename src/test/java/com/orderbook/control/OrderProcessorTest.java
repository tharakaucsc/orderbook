package com.orderbook.control;

import com.orderbook.exception.OrderException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {

    private static OrderProcessor orderProcessor;
    private static final String symbol = "AAA";

    @BeforeAll
    static void setUpAll() {
        orderProcessor = new OrderProcessor();
    }

    @Test
    void testIsValidOrderSideText() {
        assertTrue(orderProcessor.isValidOrderSideText("B"));
        assertTrue(orderProcessor.isValidOrderSideText("S"));
        assertFalse(orderProcessor.isValidOrderSideText("X"));
    }

    @Test
    void testIsValidActionTypeText() {
        assertTrue(orderProcessor.isValidOrderActionTypeText("A"));
        assertTrue(orderProcessor.isValidOrderActionTypeText("X"));
        assertFalse(orderProcessor.isValidOrderActionTypeText("R"));
    }

    @Test
    void testIsValidQuantityText() {
        assertTrue(orderProcessor.isValidQuantityText("100"));
        assertFalse(orderProcessor.isValidQuantityText("-100"));
        assertFalse(orderProcessor.isValidQuantityText("1.5"));
    }

    @Test
    void testIsValidPriceText() {
        assertTrue(orderProcessor.isValidPriceText("100"));
        assertTrue(orderProcessor.isValidPriceText("1.5"));
        assertFalse(orderProcessor.isValidPriceText("-1.5"));
        assertFalse(orderProcessor.isValidPriceText("1.5.8"));
        assertFalse(orderProcessor.isValidPriceText("-1.5.8"));
        assertFalse(orderProcessor.isValidPriceText("-10"));
    }

    @Test
    void testIsValidOrderRecord() {
        String[] orderRecord = {"A", "Order-1", "B", "10", "102"};
        assertTrue(orderProcessor.isValidOrderRecord(orderRecord));
    }

    @Test
    void testProcessOrderShouldThrowIllegalArgumentExceptionWhenOrderRecordArrayLengthIsLessThanFive() {
        String[] orderRecord = {"A", "Order-1", "B", "10"};
        assertThrows(IllegalArgumentException.class, () -> {
            orderProcessor.processOrder(symbol, orderRecord);
        });
    }

    @Test
    void testProcessOrderShouldThrowIllegalArgumentExceptionWhenOrderRecordActionTextIsIncorrect() {
        String[] orderRecord = {"Z", "Order-1", "B", "10", "100"};
        assertThrows(IllegalArgumentException.class, () -> {
            orderProcessor.processOrder(symbol, orderRecord);
        });
    }

    @Test
    void testProcessOrderShouldThrowIllegalArgumentExceptionWhenOrderRecordSideTextIsIncorrect() {
        String[] orderRecord = {"X", "Order-1", "V", "10", "100"};
        assertThrows(IllegalArgumentException.class, () -> {
            orderProcessor.processOrder(symbol, orderRecord);
        });
    }

    @Test
    void testProcessOrderShouldThrowIllegalArgumentExceptionWhenOrderRecordQuantityTextIsIncorrect() {
        String[] orderRecord = {"X", "Order-1", "S", "-10", "100"};
        assertThrows(IllegalArgumentException.class, () -> {
            orderProcessor.processOrder(symbol, orderRecord);
        });
    }

    @Test
    void testProcessOrderShouldThrowIllegalArgumentExceptionWhenOrderRecordPriceTextIsIncorrect() {
        String[] orderRecord = {"A", "Order-1", "S", "10", "100.4.4"};
        assertThrows(IllegalArgumentException.class, () -> {
            orderProcessor.processOrder(symbol, orderRecord);
        });
    }

    @Test
    void testProcessOrderBuySide() throws OrderException {
        // test order add
        String type = "A";
        String orderId = "1234";
        String side = "B";
        String quantity = "10";
        String price = "85";

        BigDecimal priceKey = new BigDecimal(price);

        orderProcessor.processOrder(symbol, type, orderId, side, quantity, price);
        assertTrue(orderProcessor.getOrderBook(symbol).getBuyOrders().containsKey(priceKey));
        assertFalse(orderProcessor.getOrderBook(symbol).getSellOrders().containsKey(priceKey));

        // remove order
        type = "X";
        orderProcessor.processOrder(symbol, type, orderId, side, quantity, price);
        assertFalse(orderProcessor.getOrderBook(symbol).getBuyOrders().containsKey(priceKey));
        assertFalse(orderProcessor.getOrderBook(symbol).getSellOrders().containsKey(priceKey));
    }

    @Test
    void testProcessOrderSellSide() throws OrderException {
        String type = "A";
        String orderId = "1234";
        String side = "S";
        String quantity = "10";
        String price = "100";

        BigDecimal priceKey = new BigDecimal(price);

        orderProcessor.processOrder(symbol, type, orderId, side, quantity, price);
        assertTrue(orderProcessor.getOrderBook(symbol).getSellOrders().containsKey(priceKey));
        assertFalse(orderProcessor.getOrderBook(symbol).getBuyOrders().containsKey(priceKey));

        // remove order
        type = "X";
        orderProcessor.processOrder(symbol, type, orderId, side, quantity, price);
        assertFalse(orderProcessor.getOrderBook(symbol).getSellOrders().containsKey(priceKey));
        assertFalse(orderProcessor.getOrderBook(symbol).getBuyOrders().containsKey(priceKey));
    }

    @AfterAll
    @Test
    static void testGetOrderBook() {
        assertNotNull(orderProcessor.getOrderBook(symbol));
    }
}