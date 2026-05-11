package com.example.lib.utils;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods for exporting DCT benchmark data to CSV files.
 */
public class OpenCsvUtils {

    private static final Logger LOGGER = Logger.getLogger(OpenCsvUtils.class.getName());

    /**
     * Creates a CSV file containing the benchmark results for the provided input sizes.
     * <p>
     * The generated file includes a header row with the columns {@code Size},
     * {@code MyDCTTime}, and {@code LibDCTTime}, followed by one row per entry in
     * the {@code sizes} array.
     * </p>
     *
     * @param path the destination file path for the CSV output
     * @param sizes the input sizes to write as the first column
     * @param myTimes the execution times for the custom DCT implementation
     * @param libTimes the execution times for the library DCT implementation
     */
    public static void createCSVFile(String path, int[] sizes, double[] myTimes, double[] libTimes){


        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {

            String[] header = {"Size", "MyDCTTime", "LibDCTTime"};
            writer.writeNext(header);
            for(int i = 0; i < sizes.length; i++){
                writer.writeNext(new String[]{
                        Integer.toString(sizes[i]),
                        Double.toString(myTimes[i]),
                        Double.toString(libTimes[i])
                });
            }
            System.out.println("CSV creation terminated");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create CSV file at " + path, e);
        }
    }
}