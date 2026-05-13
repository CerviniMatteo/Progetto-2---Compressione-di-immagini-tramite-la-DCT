package com.example.lib.utils;

import com.example.lib.constants.BenchmarkConstants;
import com.opencsv.CSVWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility methods for exporting DCT benchmark data to CSV files.
 */
public class OpenCsvUtils {

    private static final Log log = LogFactory.getLog(OpenCsvUtils.class);

    /**
     * Creates a CSV file containing the benchmark results for the provided input sizes.
     * <p>
     * The generated file includes a header row with the columns {@code Size},
     * {@code MyDCTTime}, {@code LibDCTTime}, and {@code Ratio} (LibDCTTime/MyDCTTime),
     * followed by one row per entry in the {@code sizes} array.
     * </p>
     *
     * @param path the destination file path for the CSV output
     * @param sizes the input sizes to write as the first column
     * @param myTimes the execution times for the custom DCT implementation
     * @param libTimes the execution times for the library DCT implementation
     * @param ratios the ratio of library time to custom implementation time (LibDCTTime / MyDCTTime)
     */
    public static void createCSVFile(String path, int[] sizes, double[] myTimes, double[] libTimes, double[] ratios){
        log.debug(String.format(BenchmarkConstants.LOG_CSV_CREATE, path, sizes.length));

        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {

            String[] header = {
                    BenchmarkConstants.CSV_HEADER_SIZE,
                    BenchmarkConstants.CSV_HEADER_MY_DCT_MS,
                    BenchmarkConstants.CSV_HEADER_LIB_DCT_MS,
                    BenchmarkConstants.CSV_HEADER_RATIO
            };
            writer.writeNext(header);
            for(int i = 0; i < sizes.length; i++){
                writer.writeNext(new String[]{
                        Integer.toString(sizes[i]),
                        String.format(BenchmarkConstants.CSV_TIME_FORMAT, myTimes[i]),
                        String.format(BenchmarkConstants.CSV_TIME_FORMAT, libTimes[i]),
                        String.format(BenchmarkConstants.CSV_RATIO_FORMAT, ratios[i])
                });
            }
            log.info(String.format(BenchmarkConstants.LOG_CSV_CREATED, sizes.length));

        } catch (IOException e) {
            log.error(String.format(BenchmarkConstants.LOG_CSV_CREATE_FAILED, path), e);
        }
    }
}