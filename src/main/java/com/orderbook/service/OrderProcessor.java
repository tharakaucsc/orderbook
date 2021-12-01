package com.orderbook.service;

import com.orderbook.constant.OrderActionType;
import com.orderbook.domain.*;
import com.orderbook.constant.OrderSide;
import com.orderbook.exception.OrderException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Responsible for handling orders and process.
 * @author Tharaka Udayanga
 */
public class OrderProcessor {

    private final HashMap<String, OrderBook> orderBookMap;

    /**
     * <p>Constructs an OrderProcessor</p>
     */
    public OrderProcessor() {
        orderBookMap = new HashMap<>();
    }

    /**
     * <p>Processes an order record</p>
     * @param symbol value of the symbol
     * @param orderRecord values of the order properties
     * @throws OrderException when invalid order record parsed
     */
    public void processOrder(String symbol, String... orderRecord) throws OrderException {

        //validate order record
        if(!isValidOrderRecord(orderRecord)) {
            throw new IllegalArgumentException("Invalid order record:" + Arrays.toString(orderRecord));
        }

        String actionType = orderRecord[0];
        String orderId = orderRecord[1];
        String side = orderRecord[2];
        String quantity = orderRecord[3];
        String price = orderRecord[4];

        OrderSide orderSide = side.equals("B") ? OrderSide.BID : OrderSide.ASK;
        OrderActionType orderActionType = actionType.equals("A") ? OrderActionType.ADD : OrderActionType.REMOVE;

        //create an Order object
        Order order = new Order(orderId, new BigDecimal(price), new BigInteger(quantity), orderSide);

        OrderBook orderBook = createOrderBookIfNotExists(symbol);
        orderBook.executeOrder(order, orderActionType);
    }

    /**
     * <p>Returns the corresponding OrderBook</p>
     * @param symbol value of the symbol
     * @return corresponding {@link OrderBook}
     */
    public OrderBook getOrderBook(String symbol) {
        return orderBookMap.get(symbol);
    }

    /**
     * <p>Returns the OrderBook which exists or creates a new OrderBook otherwise and returns it</p>
     * @param symbol value of the symbol
     * @return corresponding {@link OrderBook}
     */
    private OrderBook createOrderBookIfNotExists(String symbol) {
        OrderBook orderBook;
        if(orderBookMap.containsKey(symbol)){
            orderBook = orderBookMap.get(symbol);
        } else {
            orderBook = new OrderBook(symbol);
            orderBookMap.put(symbol, orderBook);
        }
        return orderBook;
    }

    /**
     * <p>Validates an order record</p>
     * @param orderRecord order record properties
     * @return true if order record is valid
     */
    public boolean isValidOrderRecord(String[] orderRecord) {
        if(orderRecord.length == 5) {
            String actionType = orderRecord[0];
            String side = orderRecord[2];
            String quantity = orderRecord[3];
            String price = orderRecord[4];

            if(!isValidOrderActionTypeText(actionType) ||
                !isValidOrderSideText(side) ||
                !isValidQuantityText(quantity) ||
                !isValidPriceText(price)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean isValidOrderSideText(String orderSideText) {
        return (orderSideText.equals("B") || orderSideText.equals("S"));
    }

    public boolean isValidOrderActionTypeText(String orderActionTypeText) {
        return (orderActionTypeText.equals("A") || orderActionTypeText.equals("X"));
    }

    public boolean isValidQuantityText(String quantityText) {
        return quantityText.matches("[0-9]+");
    }

    public boolean isValidPriceText(String priceText) {
        return priceText.matches("[0-9]+\\.?[0-9]*");
    }

}
