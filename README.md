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

## Run Order Book program with a sample file
#### Using command line - assuming you have installed JDK1.8 or higher version
A sample file is packed with the project and it is available in the '**src/main/java/resources/data/**' folder.
1. Download project 'zip' file.
2. Extract the zip file in your downloaded directory. Now you will see a folder with 'orderbook-develop' name.
3. Open a terminal and change your workign directory to '**orderbook-develop**'.
4. Change your directory to '**src/main/**'.
5. Create a new folder named '**classes**'
6. Execute this command. This should compile the program.
   - **javac -sourcepath java -d classes java/com/orderbook/app/Main.java**
7. Execute this command to run the program by processing sample Market Data File.
   - **java -cp classes/ com.orderbook.app.Main resources/data/MarketDataFile-XYZ.csv**
Final result should be like this.
  <img width="243" alt="Screenshot 2021-12-01 at 8 00 44 PM" src="https://user-images.githubusercontent.com/6348101/144231066-d1b254f3-7bed-464e-bfa4-73008c48cd0c.png">
  
#### Using and IDE
1. Download project 'zip' file.
2. Extract the zip file in your downloaded directory. Now you will see a folder with 'orderbook-develop' name.
3. Now from IDE select open/import existing project.
4. Navigate to the project extracted directory and select '**pom.xml**' to import as a Maven Project.
5. Once imported you can build and run the program.

