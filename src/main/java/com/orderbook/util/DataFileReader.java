package com.orderbook.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Represents Data File Reader
 * @author Tharaka Udayanga
 */
public class DataFileReader {

    private final FileInputStream inputStream;
    private final Scanner scanner;

    /**
     * <p>Constructs a DataFileReader for a given file</p>
     * @param path value of the file path
     * @throws FileNotFoundException when file is not found
     */
    public DataFileReader(String path) throws FileNotFoundException{
        inputStream = new FileInputStream(path);
        scanner = new Scanner(inputStream);
    }

    /**
     * <p>Returns whether the file has content which is readable</p>
     * @return true if there is content to be read in the file
     */
    public boolean hasNext() {
        boolean hasNext = false;
        return  scanner.hasNext();
    }

    /**
     * <p>Returns the current reading line of the file</p>
     * @return current line of the file
     */
    public String nextLine() {
        return scanner.nextLine();
    }

    /**
     * <p>Close all resources</p>
     */
    public void close() {
        try {
            inputStream.close();
        } catch (IOException e){
            System.out.println("Resource closing failed:" + e);
        }
        scanner.close();
    }

}
