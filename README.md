# Order Book
Order Book implementation with price/time order priority.

## About The Project
Application program to manage an Order Book with price/time priority. Orders can be submitted via a data file which should be in **csv** format.
This is able to handle Order Books for multiple instruments. Orders are stored based on their side (**BID/ASK**) in two TreeMap data structures.

Order storing data structure is **TreeMap<BigDecimal, Queue<Order>>** type. Here the 'key' is 'Price' and the Orders are stored in a queue which 
are of the same price. Queue is ordered according to the order processed time.

ASK(SELL) Orders are stored in natural price order and BID(BUY) Orders are stored in reverse price order.

## Assumptions
- File Name Format : MarketDataFile-\<Symbol\>.csv
- Single Threaded

## Unit Testing
Below classes are unit tested.
- Order
- OrderBook
- OrderProcessor
- DataFileReader

