package com.orderbook.domain;

import com.orderbook.constant.OrderActionType;
import com.orderbook.constant.OrderSide;
import com.orderbook.exception.OrderException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderBookTest {
    private static OrderBook orderBook;
    private static final String symbol = "XYZ";

    @BeforeAll
    static void setUpAll() {
        orderBook = new OrderBook(symbol);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testOrderBookCreationShouldThrowIllegalArgumentExceptionWhenSymbolIsNullOrEmptyOrWhiteOrOnlySpaces() {
        assertThrows(IllegalArgumentException.class, () -> {
           new OrderBook(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new OrderBook("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new OrderBook("   ");
        });
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetSymbol() {
        assertEquals(symbol, orderBook.getSymbol());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testAddBuyOrder() throws OrderException {
        Order order = new Order("1", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);

        orderBook.addOrder(order);
        assertNotNull(orderBook.getBuyOrders().get(order.getPrice()));
        assertNotNull(orderBook.getBuyOrders().get(order.getPrice()).peek());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testAddSellOrder() throws OrderException {
        Order order = new Order("2", new BigDecimal("105"), new BigInteger("10"), OrderSide.ASK);
        orderBook.addOrder(order);
        assertNotNull(orderBook.getSellOrders().get(order.getPrice()));
        assertNotNull(orderBook.getSellOrders().get(order.getPrice()).peek());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testOrderBookToString() {
        String orderBookString = "======================\nASK\n"
                +"105: 10"
                +"\n-------------\n"
                +"100: 10\n"
                +"BID\n======================\n";
        assertEquals(orderBookString, orderBook.toString());
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testRemoveBuyOrder() throws OrderException {
        Order order = new Order("1", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);
        orderBook.removeOrder(order);
        assertFalse(orderBook.getBuyOrders().containsKey(order.getPrice()));
        assertNull(orderBook.getBuyOrders().get(order.getPrice()));
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testRemoveSellOrder() throws OrderException {
        Order order = new Order("2", new BigDecimal("105"), new BigInteger("10"), OrderSide.ASK);
        orderBook.removeOrder(order);
        assertFalse(orderBook.getSellOrders().containsKey(order.getPrice()));
        assertNull(orderBook.getSellOrders().get(order.getPrice()));
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testAddOrderShouldThrowOrderExceptionWhenAddingOrderWithDuplicateOrderId() {
        Order order1 = new Order("3", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);
        Order order2 = new Order("3", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);

        OrderException exception = assertThrows(OrderException.class, () -> {
            orderBook.addOrder(order1);
            orderBook.addOrder(order2);
        });

        String exceptionMsg = exception.getMessage();
        String expectedMsg = "Duplicate Order:" + order2.getOrderId();
        assertTrue(exceptionMsg.contains(expectedMsg));
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testExecuteOrderShouldThrowOrderExceptionWhenExecutingOrderWithDuplicateOrderId() {
        Order order1 = new Order("4", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);
        Order order2 = new Order("4", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);

        OrderException exception = assertThrows(OrderException.class, () -> {
            orderBook.executeOrder(order1, OrderActionType.ADD);
            orderBook.executeOrder(order2, OrderActionType.ADD);
        });

        String exceptionMsg = exception.getMessage();
        String expectedMsg = "Duplicate Order:" + order2.getOrderId();
        assertTrue(exceptionMsg.contains(expectedMsg));
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void testExecuteOrderShouldRemoveExistingOrderWhenExecutedWithActionTypeRemove() throws OrderException {
        Order order1 = new Order("5", new BigDecimal("200"), new BigInteger("10"), OrderSide.BID);
        orderBook.addOrder(order1);
        orderBook.executeOrder(order1, OrderActionType.REMOVE);

        assertFalse(orderBook.getBuyOrders().containsKey(order1.getPrice()));
        assertNull(orderBook.getBuyOrders().get(order1.getPrice()));
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void testRemoveOrderShouldThrowOrderExceptionWhenRemovingOrderWithNonExistingId() throws OrderException {
        Order order1 = new Order("6", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);
        Order order2 = new Order("20", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);

        orderBook.addOrder(order1);

        OrderException exception = assertThrows(OrderException.class, () -> {
            orderBook.removeOrder(order2);
        });

        String exceptionMsg = exception.getMessage();
        String expectedMsg = "Not found, Order:" + order2.getOrderId();
        assertTrue(exceptionMsg.contains(expectedMsg));
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    void testRemoveOrderShouldThrowOrderExceptionWhenRemovingAlreadyExecuted() throws OrderException {
        orderBook.clear();
        Order order1 = new Order("7", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);
        Order order2 = new Order("8", new BigDecimal("100"), new BigInteger("10"), OrderSide.BID);
        Order order3 = new Order("9", new BigDecimal("90"), new BigInteger("10"), OrderSide.ASK);

        orderBook.executeOrder(order1, OrderActionType.ADD);
        orderBook.executeOrder(order2, OrderActionType.ADD);
        orderBook.executeOrder(order3, OrderActionType.ADD);

        OrderException exception = assertThrows(OrderException.class, () -> {
            orderBook.executeOrder(order1, OrderActionType.REMOVE);
        });

        String exceptionMsg = exception.getMessage();
        String expectedMsg = "Remove fail.Not found, Order:" + order1.getOrderId();
        assertTrue(exceptionMsg.contains(expectedMsg));

        exception = assertThrows(OrderException.class, () -> {
            orderBook.executeOrder(order3, OrderActionType.REMOVE);
        });

        exceptionMsg = exception.getMessage();
        expectedMsg = "Remove fail.Not found, Order:" + order3.getOrderId();
        assertTrue(exceptionMsg.contains(expectedMsg));
    }

    @Test
    @org.junit.jupiter.api.Order(13)
    void testClearOrderBook() {
        orderBook.clear();
        assertTrue(orderBook.getBuyOrders().isEmpty());
        assertTrue(orderBook.getSellOrders().isEmpty());
        assertNotNull(orderBook.getSymbol());
        assertNotEquals("", orderBook.getSymbol());
    }

    @Test
    @org.junit.jupiter.api.Order(14)
    void testGivenScenarioInAssignment() throws OrderException {
        Order order1 = new Order("1", new BigDecimal("110"), new BigInteger("5"), OrderSide.ASK);
        Order order2 = new Order("2", new BigDecimal("90"), new BigInteger("10"), OrderSide.BID);
        Order order3 = new Order("3", new BigDecimal("110"), new BigInteger("10"), OrderSide.ASK);
        Order order4 = new Order("4", new BigDecimal("105"), new BigInteger("3"), OrderSide.ASK);
        Order order5 = new Order("5", new BigDecimal("105"), new BigInteger("7"), OrderSide.ASK);
        Order order6 = new Order("6", new BigDecimal("90"), new BigInteger("2"), OrderSide.BID);
        Order order7 = new Order("7", new BigDecimal("90"), new BigInteger("3"), OrderSide.BID);
        Order order8 = new Order("8", new BigDecimal("100"), new BigInteger("4"), OrderSide.BID);
        Order order9 = new Order("9", new BigDecimal("100"), new BigInteger("6"), OrderSide.BID);
        Order order10 = new Order("10", new BigDecimal("105"), new BigInteger("4"), OrderSide.BID);
        Order order11 = new Order("11", new BigDecimal("80"), new BigInteger("23"), OrderSide.ASK);
        Order order12 = new Order("12", new BigDecimal("107"), new BigInteger("8"), OrderSide.BID);

        orderBook.clear();

        orderBook.executeOrder(order1, OrderActionType.ADD);
        orderBook.executeOrder(order2, OrderActionType.ADD);
        orderBook.executeOrder(order3, OrderActionType.ADD);
        orderBook.executeOrder(order4, OrderActionType.ADD);
        orderBook.executeOrder(order5, OrderActionType.ADD);
        orderBook.executeOrder(order6, OrderActionType.ADD);
        orderBook.executeOrder(order7, OrderActionType.ADD);
        orderBook.executeOrder(order8, OrderActionType.ADD);
        orderBook.executeOrder(order9, OrderActionType.ADD);
        orderBook.executeOrder(order10, OrderActionType.ADD);
        orderBook.executeOrder(order11, OrderActionType.ADD);
        orderBook.executeOrder(order12, OrderActionType.ADD);

        String orderBookString = "======================\nASK\n"
                                +"110: 5 10\n"
                                +"-------------\n"
                                +"107: 2\n"
                                +"90: 2\n"
                                +"BID\n======================\n";

        assertEquals(orderBookString, orderBook.toString());
    }
}