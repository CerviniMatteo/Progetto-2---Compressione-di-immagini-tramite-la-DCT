package com.example.lib.utils;

import org.knowm.xchart.*;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.constants.UtilsConstants.*;

/**
 * Utility methods for building and exporting benchmark plots for the DCT comparison.
 */
public class PlotUtils {
    private static final Logger LOGGER = Logger.getLogger(PlotUtils.class.getName());

    /**
     * Builds a benchmark chart comparing the custom DCT implementation with the library DCT
     * implementation, saves it to the {@code output/} directory, and displays it in a Swing window.
     * <p>
     * The chart uses a logarithmic Y axis, so the provided timing values should be strictly positive.
     * The {@code nValues}, {@code myTimes}, and {@code libTimes} arrays are expected to have matching
     * lengths so that each X value has a corresponding value in both series.
     * </p>
     *
     * @param nValues the input sizes used as the X axis values
     * @param myTimes the execution times for the custom DCT implementation
     * @param libTimes the execution times for the library DCT implementation
     * @param title the chart title and the base name used when saving the PNG file
     */
    public static void plotDCTBenchmark(double[] nValues,
                                                double[] myTimes,
                                                double[] libTimes,
                                                String title) {

        XYChart chart = new XYChartBuilder()
                .width(CHART_WIDTH)
                .height(CHART_HEIGHT)
                .title(title)
                .xAxisTitle(X_AXIS_TITLE)
                .yAxisTitle(Y_AXIS_TITLE)
                .build();

        chart.getStyler().setYAxisLogarithmic(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setMarkerSize(MARKER_SIZE);

        // Serie benchmark
        chart.addSeries(SERIES_MY_DCT, nValues, myTimes)
                .setMarker(SeriesMarkers.CIRCLE);

        chart.addSeries(SERIES_LIBRARY_DCT, nValues, libTimes)
                .setMarker(SeriesMarkers.DIAMOND);

        try {
            saveChart(chart, title);
            new SwingWrapper<>(chart).displayChart();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, FAILED_BUILD_DISPLAY_MESSAGE, e);
        }
    }

    /**
     * Saves the provided chart as a PNG file inside the {@code output/} directory.
     * <p>
     * Spaces in the title are replaced with underscores before generating the file name.
     * The method creates the output directory if it does not already exist.
     * </p>
     *
     * @param chart the chart to export
     * @param title the chart title used to derive the output file name
     */
    private static void saveChart(XYChart chart, String title) {
        try {
            String filename = title.replaceAll(WHITESPACE_REGEX, WHITESPACE_REPLACEMENT);
            File outputDirectory = new File(OUTPUT_PATH);
            if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                LOGGER.warning(UNABLE_CREATE_OUTPUT_DIR_MESSAGE + outputDirectory.getAbsolutePath());
            }

            BitmapEncoder.saveBitmap(
                    chart,
                    OUTPUT_PATH + filename,
                    BitmapFormat.PNG
            );

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, FAILED_SAVE_MESSAGE, e);
        }
    }
}