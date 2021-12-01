package com.orderbook.domain;

import com.orderbook.constant.OrderActionType;
import com.orderbook.constant.OrderSide;
import com.orderbook.exception.OrderException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * OrderBook stores buy,sell orders. When orders are executable this executes such orders and updates self state.
 * @author Tharaka Udayanga
 */
public class OrderBook {

    private String symbol;
    private final TreeMap<BigDecimal, Queue<Order>> sellOrders;
    private final TreeMap<BigDecimal, Queue<Order>> buyOrders;
    private final Set<String> orderIds;

    /**
     * <p>Sets the value of the symbol</p>
     * @param symbol the value of symbol
     */
    private void setSymbol(String symbol) {
        if(isValidSymbol(symbol)) {
            this.symbol = symbol;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * <p>Returns the value of the symbol</p>
     * @return value of symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * <p>Returns the sell orders stored in the order book</p>
     * @return sell orders
     */
    public TreeMap<BigDecimal, Queue<Order>> getSellOrders() {
        return sellOrders;
    }

    /**
     * <p>Returns the bu yorders stored in the order book</p>
     * @return buy orders
     */
    public TreeMap<BigDecimal, Queue<Order>> getBuyOrders() {
        return buyOrders;
    }

    /**
     * <p>Constructs an OrderBook for the given symbol</p>
     * @param symbol value of the symbol
     */
    public OrderBook(String symbol) {
        setSymbol(symbol);
        sellOrders = new TreeMap<>();
        buyOrders = new TreeMap<>(Collections.reverseOrder());
        orderIds = new HashSet<>();
    }

    private boolean isValidSymbol(String symbol) {
        return symbol != null && !symbol.trim().equals("");
    }

    /**
     * <p>Executes an order with matching orders</p>
     * @param order the order to execute
     * @param orderActionType order action type {@link OrderActionType}
     * @throws OrderException when Duplicate Order added, when invalid orderActionType parsed, when Order not found to remove
     */
    public void executeOrder(Order order, OrderActionType orderActionType) throws OrderException {
        switch (orderActionType) {
            case ADD:
                if(!isDuplicateOrder(order.getOrderId())) {
                    runExecution(getOrderMap(order.getSide(), false), order);
                    if(order.isExecutable()) {
                        addOrder(order);
                    }
                } else {
                    throw new OrderException("Duplicate Order:" + order.getOrderId());
                }
                break;
            case REMOVE:
                removeOrder(order);
                break;
            default:
                throw new OrderException("Unexpected Order Action Type:" + orderActionType);
        }
    }

    /**
     * <p>Runs execution of order</p>
     * @param orderMapToCompare order map to execute against
     * @param order order for execution
     */
    private void runExecution(TreeMap<BigDecimal, Queue<Order>> orderMapToCompare, Order order) {
        // iterate through comparing order map
        Iterator<Map.Entry<BigDecimal, Queue<Order>>> iterator = orderMapToCompare.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BigDecimal, Queue<Order>> entry = iterator.next();
            BigDecimal comparingPrice = entry.getKey();

            if(order.isExecutable(comparingPrice)) {
                // iterate through order queue and execute order
                Queue<Order> orderQueue = orderMapToCompare.get(comparingPrice);
                Iterator<Order> queueIterator = orderQueue.iterator();
                while(queueIterator.hasNext()) {
                    Order orderInFront = queueIterator.next();
                    if(orderInFront.getQuantity().compareTo(order.getQuantity()) > 0) {
                        orderInFront.setQuantity(orderInFront.getQuantity().subtract(order.getQuantity()));
                        order.setQuantity(BigInteger.ZERO);
                        break;
                    } else {
                        order.setQuantity(order.getQuantity().subtract(orderInFront.getQuantity()));
                        orderIds.remove(orderInFront.getOrderId());
                        queueIterator.remove();
                    }
                }

                // remove entry from Order Book if no more orders in the queue
                if(orderQueue.isEmpty()) {
                    iterator.remove();
                }
                // exit if order is fully executed
                if(!order.isExecutable()) {
                    break;
                }
            } else {
                // exit if no matching price found for execution
                break;
            }
        }
    }

    /**
     * <p>Adds an Order to corresponding order map</p>
     * @param order order for add
     */
    public void addOrder(Order order) throws OrderException {
        if(!isDuplicateOrder(order.getOrderId()) && order.isExecutable()) {
            TreeMap<BigDecimal, Queue<Order>> orderMap = getOrderMap(order.getSide(), true);
            // if price exist add to the end of the existing queue else add a new queue
            if (orderMap.containsKey(order.getPrice())) {
                Queue<Order> orderQueue = orderMap.get(order.getPrice());
                orderQueue.add(order);
            } else {
                Queue<Order> orderQueue = new LinkedList<>();
                orderQueue.add(order);
                orderMap.put(order.getPrice(), orderQueue);
            }
            orderIds.add(order.getOrderId());
        } else {
            if(isDuplicateOrder(order.getOrderId())) {
                throw new OrderException("Duplicate Order:" + order.getOrderId());
            } else {
                throw new OrderException("Not executable, Order:" + order.getOrderId());
            }
        }
    }

    /**
     * <p>Removes an order from the corresponding order map</p>
     * @param order order for remove
     */
    public void removeOrder(Order order) throws OrderException {
        TreeMap<BigDecimal, Queue<Order>>  orderMap = getOrderMap(order.getSide(), true);
        boolean orderNotFound = true;
        // check a matching price available in the corresponding map
        if(orderMap.containsKey(order.getPrice())) {
            Queue<Order> orderQueue = orderMap.get(order.getPrice());
            Iterator<Order> queueIterator = orderQueue.iterator();

            while(queueIterator.hasNext()) {
                if(order.getOrderId().equals(queueIterator.next().getOrderId())) {
                    queueIterator.remove();
                    orderIds.remove(order.getOrderId());
                    orderNotFound = false;
                    break;
                }
            }
            if(orderQueue.isEmpty()) {
                orderMap.remove(order.getPrice());
            }
        }
        if(orderNotFound) {
            throw new OrderException("Remove fail.Not found, Order:" + order.getOrderId());
        }
    }

    /**
     * <p>Checks whether an order already exists</p>
     * @param orderId value of the id of the order
     * @return true if the id of the order is available in OrderBook
     */
    private boolean isDuplicateOrder(String orderId) {
        return orderIds.contains(orderId);
    }

    /**
     * <p>Returns the order map</p>
     * @param side value of the side
     * @param isSameSide true if the order map returns should be same side
     * @return order map of same side when isSameSide is true, order map of opposite side otherwise
     */
    private TreeMap<BigDecimal, Queue<Order>> getOrderMap(OrderSide side, boolean isSameSide) {
        TreeMap<BigDecimal, Queue<Order>>  orderMap;
        OrderSide orderMapSide = side;
        if(!isSameSide) {
            orderMapSide = (side == OrderSide.BID) ? OrderSide.ASK : OrderSide.BID;
        }
        switch (orderMapSide) {
            case BID:
                orderMap = buyOrders;
                break;
            case ASK:
                orderMap = sellOrders;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + side);
        }
        return orderMap;
    }

    /**
     * <p>Returns the content of the OrderBook</p>
     * @return order book content string
     */
    public String toString() {
        StringBuilder orderBookString = new StringBuilder();

        orderBookString.append("======================\nASK\n");
        for (Map.Entry<BigDecimal, Queue<Order>> entry : sellOrders.descendingMap().entrySet()) {
            orderBookString.append(getOrderBookEntryString(entry));
        }
        orderBookString.append("-------------\n");
        for (Map.Entry<BigDecimal, Queue<Order>> entry : buyOrders.entrySet()) {
            orderBookString.append(getOrderBookEntryString(entry));
        }
        orderBookString.append("BID\n======================\n");
        return orderBookString.toString();
    }

    private StringBuilder getOrderBookEntryString(Map.Entry<BigDecimal, Queue<Order>> entry) {
        StringBuilder orderBookEntryString = new StringBuilder();
        orderBookEntryString.append(entry.getKey().toString());
        orderBookEntryString.append(": ");
        // remove ',' from quantity string
        String quantityString = entry.getValue().toString().replaceAll(",","");
        // get order string without brackets '[' and ']'
        orderBookEntryString.append(quantityString.substring(1,quantityString.length() - 1));
        orderBookEntryString.append("\n");

        return orderBookEntryString;
    }

    /**
     * <p>Clears the OrderBook content, except the symbol</p>
     */
    public void clear() {
        sellOrders.clear();
        buyOrders.clear();
        orderIds.clear();
    }

}
