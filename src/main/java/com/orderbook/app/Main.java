package com.orderbook.app;

import com.orderbook.control.OrderProcessor;
import com.orderbook.exception.OrderException;
import com.orderbook.util.DataFileReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("---- Order Book Management ----");

        String dataFilePath = "src/main/resources/data/MarketDataFile-XYZ.csv";
        // assumption : file name contains the symbol
        // file name pattern : MarketDataFile-<SYMBOL>.csv
        if(!dataFilePath.matches(".*-.*\\.csv")) {
            System.out.println("File Name Error!\nExpected:MarketDataFile-<SYMBOL>.csv");
            System.exit(-1);
        }
        String symbol = dataFilePath.substring(dataFilePath.lastIndexOf('-') + 1, dataFilePath.lastIndexOf(".csv"));
        OrderProcessor orderProcessor = new OrderProcessor();

        DataFileReader dataFileReader = null;
        try {
            dataFileReader = new DataFileReader(dataFilePath);
            while (dataFileReader.hasNext()) {
                String[] recordDetail = dataFileReader.nextLine().split(",");
                try {
                    orderProcessor.processOrder(symbol, recordDetail);
                    System.out.println(orderProcessor.getOrderBook(symbol));
                } catch (OrderException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Failed reading file:" + e.getMessage());
        } finally {
            if(dataFileReader != null)
                dataFileReader.close();
        }

        System.out.println("\nFinal state of Order Book\n" + orderProcessor.getOrderBook(symbol));
    }
}
