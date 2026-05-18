package com.example.lib.utils;

import com.example.assignment.model.BenchmarkMeasurement;
import com.example.assignment.constants.BenchmarkConstants;
import com.opencsv.CSVWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility methods for exporting DCT benchmark data to CSV files.
 *
 * <h2>Ratio / percentage convention</h2>
 * <p>{@link BenchmarkMeasurement#ratioOnMean()} returns a pure ratio
 * {@code libraryMean / customMean}. {@link #formatRatio(double)} converts that
 * to a human-readable percentage:</p>
 * <pre>
 *   percentageDifference = (1 / ratio − 1) × 100
 * </pre>
 * <ul>
 *   <li>Positive → custom is slower than library (e.g. +934% means custom takes ~10× longer).</li>
 *   <li>Negative → custom is faster than library.</li>
 * </ul>
 */
public class OpenCsvUtils {

    private static final Log log = LogFactory.getLog(OpenCsvUtils.class);

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
        log.debug(String.format(BenchmarkConstants.LOG_CSV_CREATE, path, safe.size()));

        ensureParentDirectoryExists(path);

        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeNext(buildHeader());

            for (BenchmarkMeasurement m : safe) {
                if (m == null) continue;
                writer.writeNext(buildRow(m));
            }

            log.info(String.format(BenchmarkConstants.LOG_CSV_CREATED, safe.size()));
        } catch (IOException e) {
            log.error(String.format(BenchmarkConstants.LOG_CSV_CREATE_FAILED, path), e);
        }
    }

    /**
     * Legacy CSV writer that keeps the original compact output format.
     *
     * @param path     destination file path
     * @param sizes    input sizes (first column)
     * @param myTimes  execution times for the custom DCT implementation
     * @param libTimes execution times for the library DCT implementation
     * @param ratios   pre-computed {@code library / custom} ratios
     */
    @SuppressWarnings("unused")
    public static void createCSVFile(String path,
                                     int[] sizes,
                                     double[] myTimes,
                                     double[] libTimes,
                                     double[] ratios) {
        log.debug(String.format(BenchmarkConstants.LOG_CSV_CREATE, path, sizes.length));

        ensureParentDirectoryExists(path);

        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeNext(new String[]{
                    BenchmarkConstants.CSV_HEADER_SIZE,
                    BenchmarkConstants.CSV_HEADER_MY_DCT_MS,
                    BenchmarkConstants.CSV_HEADER_LIB_DCT_MS,
                    BenchmarkConstants.CSV_HEADER_RATIO
            });

            for (int i = 0; i < sizes.length; i++) {
                writer.writeNext(new String[]{
                        Integer.toString(sizes[i]),
                        String.format(BenchmarkConstants.CSV_TIME_FORMAT, myTimes[i]),
                        String.format(BenchmarkConstants.CSV_TIME_FORMAT, libTimes[i]),
                        formatRatio(ratios[i])
                });
            }

            log.info(String.format(BenchmarkConstants.LOG_CSV_CREATED, sizes.length));
        } catch (IOException e) {
            log.error(String.format(BenchmarkConstants.LOG_CSV_CREATE_FAILED, path), e);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private static String[] buildHeader() {
        return new String[]{
                BenchmarkConstants.CSV_HEADER_SIZE,
                "Custom Avg (s)",
                "Custom Min (s)",
                "Custom Max (s)",
                "Custom Sum (s)",
                "Custom N",
                "Library Avg (s)",
                "Library Min (s)",
                "Library Max (s)",
                "Library Sum (s)",
                "Library N",
                BenchmarkConstants.CSV_HEADER_RATIO
        };
    }

    private static String[] buildRow(BenchmarkMeasurement m) {
        return new String[]{
                Integer.toString(m.size()),
                formatSeconds(m.customMeanSeconds()),
                formatSeconds(m.customMinSeconds()),
                formatSeconds(m.customMaxSeconds()),
                formatSeconds(m.customSumSeconds()),
                Long.toString(m.customN()),
                formatSeconds(m.libraryMeanSeconds()),
                formatSeconds(m.libraryMinSeconds()),
                formatSeconds(m.libraryMaxSeconds()),
                formatSeconds(m.librarySumSeconds()),
                Long.toString(m.libraryN()),
                formatRatio(m.ratioOnMean())
        };
    }

    /**
     * Converts a pure {@code libraryMean / customMean} ratio to a percentage string.
     *
     * <pre>
     *   percentage = (1 / ratio − 1) × 100
     * </pre>
     *
     * <p>Examples:
     * <ul>
     *   <li>ratio = 0.0967 → {@code +934.00%} (custom ~10× slower)</li>
     *   <li>ratio = 2.0    → {@code -50.00%}  (custom 2× faster)</li>
     * </ul>
     *
     * @param ratio pure {@code libraryMean / customMean} ratio
     * @return formatted percentage string, or {@code ""} if ratio is unavailable
     */
    private static String formatRatio(double ratio) {
        if (Double.isNaN(ratio) || ratio <= 0) return "";
        double percentageDifference = (1.0 / ratio - 1.0) * 100.0;
        return String.format("%.2f%%", percentageDifference);
    }

    private static String formatSeconds(double seconds) {
        return Double.isNaN(seconds) ? "" : String.format("%.9f", seconds);
    }

    private static void ensureParentDirectoryExists(String path) {
        File parent = new File(path).getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            log.warn(String.format(BenchmarkConstants.LOG_CSV_CREATE_FAILED, parent.getAbsolutePath()));
        }
    }
}