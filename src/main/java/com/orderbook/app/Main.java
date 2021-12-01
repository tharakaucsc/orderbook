package com.orderbook.app;

import com.orderbook.service.OrderProcessor;
import com.orderbook.exception.OrderException;
import com.orderbook.util.DataFileReader;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        System.out.println("---- Order Book Management ----");

        if(args.length != 1) {
            System.out.println("Please provide the file path!");
            System.exit(-1);
        }
        String dataFilePath = args[0];
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
