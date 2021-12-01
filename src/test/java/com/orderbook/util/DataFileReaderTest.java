package com.orderbook.util;

import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataFileReaderTest {

    private static String path = "src/test/resources/MarketDataFile-XYZ.csv";
    private static DataFileReader dataFileReader;
    private static final String[] recordLines = {"A,100000,S,1,1075",
                                                "A,100001,B,9,1000",
                                                "A,100002,B,30,975",
                                                "A,100003,S,10,1050",
                                                "A,100004,B,10,950",
                                                "A,100005,S,2,1025",
                                                "A,100006,B,1,1000",
                                                "X,100004,B,10,950",
                                                "A,100007,S,5,1025",
                                                "A,100008,B,3,1050",
                                                "X,100008,B,3,1050",
                                                "X,100005,S,2,1025"};

    @BeforeAll
    @Test
    static void setUpAll() throws FileNotFoundException {
        dataFileReader = new DataFileReader(path);
    }

    @Test
    @Order(1)
    void testCreateDataFileReaderShouldThrowFileNotFoundExceptionWhenNoFileFound() {
        String invalidPath = "test/resources/MarketDataFile-XYZ.csv";
        assertThrows(FileNotFoundException.class, () -> {
            new DataFileReader(invalidPath);
        });
    }

    @Test
    @Order(2)
    void testCreateDataFileReader() throws FileNotFoundException {
        assertNotNull(dataFileReader);
    }

    @Test
    @Order(3)
    void testHasNext() {
        assertTrue(dataFileReader.hasNext());
    }

    void testNextLine(int lineNo) {
        assertEquals(recordLines[lineNo], dataFileReader.nextLine());
    }

    @Test
    @Order(4)
    void testHasNextShouldReadTillEndOfFile() {
        int recordCountInFile =  12;
        int recordCountFromDataFileReader = 0;
        while (dataFileReader.hasNext()){
            testNextLine(recordCountFromDataFileReader);
            recordCountFromDataFileReader++;
        }
        assertEquals(recordCountInFile, recordCountFromDataFileReader);
    }

    @Test
    @Order(5)
    void testClose() {
        dataFileReader.close();
        assertThrows(IllegalStateException.class, () -> {
            dataFileReader.hasNext();
        });
        assertThrows(IllegalStateException.class, () -> {
            dataFileReader.nextLine();
        });
    }

    @AfterAll
    static void cleanUp() {
        dataFileReader.close();
    }
}