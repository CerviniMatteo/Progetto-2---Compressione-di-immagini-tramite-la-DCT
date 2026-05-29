package com.unimib.assignment.backend.utils;

import com.unimib.assignment.backend.constants.CSVConstants;
import com.unimib.assignment.backend.constants.LogConstants;
import com.unimib.assignment.backend.model.BenchmarkMeasurement;
import com.unimib.assignment.backend.constants.BenchmarkConstants;
import com.opencsv.CSVWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class OpenCsvUtils {

    private static final Logger log = LogManager.getLogger(OpenCsvUtils.class);

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Creates a CSV file containing mean, min, max, sum, and sample count
     * for both implementations, plus the performance ratio.
     *
     * @param path         destination file path
     * @param measurements benchmark measurements to export
     */
    public static void createCSVFile(String path, List<BenchmarkMeasurement> measurements) {
        List<BenchmarkMeasurement> safe = measurements == null ? List.of() : measurements;
        log.debug(LogConstants.LOG_CSV_CREATE, path, safe.size());

        ensureParentDirectoryExists(path);

        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeNext(buildHeader());

             for (BenchmarkMeasurement m : safe) {
                 writer.writeNext(buildRow(m));
             }

             log.info(LogConstants.LOG_CSV_CREATED, safe.size());
         } catch (IOException e) {
             log.error(LogConstants.LOG_CSV_CREATE_FAILED, path, e);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private static String[] buildHeader() {
        return new String[]{
                CSVConstants.CSV_HEADER_SIZE,
                CSVConstants.CSV_HEADER_CUSTOM_AVG_SECONDS,
                CSVConstants.CSV_HEADER_LIBRARY_AVG_SECONDS,
        };
    }

    private static String[] buildRow(BenchmarkMeasurement m) {
        return new String[]{
                Integer.toString(m.size()),
                formatSeconds(m.customMeanSeconds()),
                formatSeconds(m.libraryMeanSeconds()),
        };
    }


    private static String formatSeconds(double seconds) {
        return Double.isNaN(seconds) ? "" : String.format(BenchmarkConstants.TIME_FORMAT_HIGH_PRECISION, seconds);
    }

    private static void ensureParentDirectoryExists(String path) {
        File parent = new File(path).getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            log.warn(LogConstants.LOG_CSV_CREATE_FAILED, parent.getAbsolutePath());
        }
    }
}