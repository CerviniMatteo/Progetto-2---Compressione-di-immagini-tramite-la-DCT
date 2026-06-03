package com.unimib.assignment.backend.utils;

import com.unimib.assignment.backend.constants.CSVConstants;
import com.unimib.assignment.backend.constants.LogConstants;
import com.unimib.assignment.backend.constants.BenchmarkConstants;
import com.unimib.assignment.backend.model.BenchmarkMeasurement;
import com.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


/**
 * Utility class responsible for exporting benchmark measurements to CSV files.
 *
 * <p>This helper creates the output directory when needed, writes a fixed CSV
 * header, and serializes each {@link BenchmarkMeasurement} using the configured
 * time formatting. It is used by the benchmarking layer to persist comparison
 * results between the custom implementation and the library implementation.</p>
 */
public class OpenCsvUtils {

    // Logger used to trace CSV export attempts and failures.
    private static final Logger log = LogManager.getLogger(OpenCsvUtils.class);

    /**
     * Creates a CSV file with benchmark measurements.
     *
     * <p>The generated file contains a header row followed by one row per
     * {@link BenchmarkMeasurement}. Each row stores the measurement size,
     * the custom implementation mean time, and the library implementation
     * mean time, formatted using the configured time precision.</p>
     *
     * @param path destination file path
     * @param measurements benchmark measurements to export
     */
    public static void createCSVFile(String path, List<BenchmarkMeasurement> measurements) {
        // Normalize null input to an empty list so the export loop can run safely.
        // Use an empty list when the input is null so the export logic can run safely.
        List<BenchmarkMeasurement> safe = measurements == null ? List.of() : measurements;
        // Record the file path and how many rows will be written.
        log.debug(LogConstants.LOG_CSV_CREATE, path, safe.size());

        // Create the parent directory tree if the output file is inside a nested folder.
        ensureParentDirectoryExists(path);

        // Write the CSV header and all measurement rows, closing the writer automatically.
        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeNext(buildHeader());

             // Write one CSV row for each benchmark measurement.
             for (BenchmarkMeasurement m : safe) {
                 writer.writeNext(buildRow(m));
             }

             // Confirm successful export in the logs.
             log.info(LogConstants.LOG_CSV_CREATED, safe.size());
         } catch (IOException e) {
             // Report any file creation or write errors.
             log.error(LogConstants.LOG_CSV_CREATE_FAILED, path, e);
        }
    }


    /**
     * Builds the CSV header row.
     *
     * @return the header columns for the exported CSV file
     */
    private static String[] buildHeader() {
        // Keep the exported column order stable for downstream consumers.
        return new String[]{
                CSVConstants.CSV_HEADER_SIZE,
                CSVConstants.CSV_HEADER_CUSTOM_AVG_SECONDS,
                CSVConstants.CSV_HEADER_LIBRARY_AVG_SECONDS,
        };
    }

    /**
     * Builds a CSV row for a single benchmark measurement.
     *
     * @param m the benchmark measurement to export
     * @return a CSV row containing the measurement data
     */
    private static String[] buildRow(BenchmarkMeasurement m) {
        // Convert the measurement fields to strings so they can be written to the CSV file.
        return new String[]{
                Integer.toString(m.size()),
                formatSeconds(m.customMeanSeconds()),
                formatSeconds(m.libraryMeanSeconds()),
        };
    }


    /**
     * Formats a duration in seconds using the configured high-precision pattern.
     *
     * @param seconds the duration to format
     * @return an empty string when the value is {@code NaN}; otherwise the formatted duration
     */
    private static String formatSeconds(double seconds) {
        // Leave missing values blank; otherwise format with the configured precision.
        return Double.isNaN(seconds) ? "" : String.format(BenchmarkConstants.TIME_FORMAT_HIGH_PRECISION, seconds);
    }

    /**
     * Ensures the parent directory of the output file exists.
     *
     * @param path the target file path whose parent directory should be created if needed
     */
    private static void ensureParentDirectoryExists(String path) {
        // Resolve the parent folder of the output file, if there is one.
        File parent = new File(path).getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            // Warn when the directory cannot be created so the caller can investigate.
            log.warn(LogConstants.LOG_CSV_CREATE_FAILED, parent.getAbsolutePath());
        }
    }
}