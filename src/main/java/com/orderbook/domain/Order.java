package com.orderbook.domain;

import com.orderbook.constant.OrderSide;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Order represents a Buy or Sell order which is taken place in a market.
 * @author Tharaka Udayanga
 */
public class Order {

    private String orderId;
    private BigInteger quantity;
    private BigDecimal price;
    private OrderSide side;

    /**
     * <p>Returns the quantity of the order</p>
     * @return the quantity of order
     */
    public BigInteger getQuantity() {
        return quantity;
    }

    /**
     * <p>Sets the quantity of the order with the specified value</p>
     * @param quantity value for quantity of the order
     */
    public void setQuantity(BigInteger quantity) {
        if(isValidQuantity(quantity)){
            this.quantity = quantity;
        } else {
            throw new IllegalArgumentException("Invalid Order Quantity:" + quantity);
        }
    }

    /**
     * <p>Returns the price of the order</p>
     * @return the price of order
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * <p>Sets the price of the order with the specified value</p>
     * @param price value for price of the order
     */
    public void setPrice(BigDecimal price) {
        if(isValidPrice(price)){
            this.price = price;
        } else {
            throw new IllegalArgumentException("Invalid Order Price:" + price);
        }
    }

    /**
     * <p>Returns the side of the order</p>
     * @return the side of order
     */
    public OrderSide getSide() {
        return side;
    }

    /**
     * <p>Sets the side of the order with the specified value</p>
     * @param side value for side of the order {@link OrderSide}
     */
    public void setSide(OrderSide side) {
        if(isValidOrderSide(side)) {
            this.side = side;
        } else {
            throw new IllegalArgumentException("Invalid Order Side");
        }
    }

    /**
     * <p>Returns the id of the order</p>
     * @return the id of order
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * <p>Sets the if of the order with the specified value</p>
     * @param orderId value for id of the order
     */
    public void setOrderId(String orderId) {
        if(isValidOrderId(orderId)) {
            this.orderId = orderId;
        } else {
            throw new IllegalArgumentException("Invalid Order Id:" + orderId);
        }
    }

    /**
     *
     * @param orderId
     * @param price
     * @param quantity
     * @param side
     */
    public Order(String orderId, BigDecimal price, BigInteger quantity, OrderSide side) {
        setOrderId(orderId);
        setPrice(price);
        setQuantity(quantity);
        setSide(side);
    }

    /**
     * <p>Returns string representation of the order with only quantity</p>
     * @return quantity of order as a string
     */
    public String toString() {
        return String.valueOf(this.quantity);
    }

    private boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isValidQuantity(BigInteger quantity) {
        return quantity != null && quantity.compareTo(BigInteger.ZERO) >= 0;
    }

    private boolean isValidOrderId(String orderId) {
        return orderId != null && !orderId.trim().equals("");
    }

    private boolean isValidOrderSide(OrderSide side) {
        return side == OrderSide.BID || side == OrderSide.ASK;
    }

    /**
     * <p>Checks whether the order is able to execute</p>
     * @return true if the order is executable
     */
    public boolean isExecutable() {
        return quantity.compareTo(BigInteger.ZERO) > 0;
    }

    /**
     * <p>Checks whether the order is able to execute against a given price</p>
     * @param comparingPrice value of the price to compare against the price of the order
     * @return true if the order is executable
     */
    public boolean isExecutable(BigDecimal comparingPrice) {
        if(isExecutable()) {
            if(this.getSide() == OrderSide.BID) {
                return this.getPrice().compareTo(comparingPrice) >= 0;
            } else {
                return this.getPrice().compareTo(comparingPrice) <= 0;
            }
        }
        return false;
    }

}
