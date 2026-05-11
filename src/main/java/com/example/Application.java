package com.example;

import com.example.UI.ImagePicker;
import com.example.assignment.Part1;
import com.example.assignment.Part2;

import static com.example.lib.constants.ProjectHelperConstants.IMG_ARRIVED;

/**
 * Main entry point for the DCT-based image processing application.
 * <p>
 * This application consists of two parts:
 * </p>
 * <ol>
 *   <li><strong>Part 1:</strong> Runs a DCT benchmark comparing the custom DCT implementation
 *       with the JTransforms library DCT across various matrix sizes. Results are plotted and
 *       exported to CSV.</li>
 *   <li><strong>Part 2:</strong> Provides a UI for image selection and applies DCT-based lossy
 *       compression with user-configurable parameters. The compressed image is displayed
 *       side-by-side with the original.</li>
 * </ol>
 */
public class Application {

    /**
     * Main method that orchestrates the application workflow.
     * <p>
     * Execution flow:
     * </p>
     * <ol>
     *   <li>Instantiates Part1 and runs a DCT benchmark on matrices of sizes
     *       {@code [8, 16, 32, 64, 128, 256, 512, 1048, 2048]}.
     *       Results are plotted to a PNG file and exported to {@code output/times_vs_size.csv}.</li>
     *   <li>After the benchmark completes, displays an image picker UI allowing the user to select
     *       an image file from the file system.</li>
     *   <li>Upon image selection, instantiates Part2 and applies DCT-based compression with
     *       user-selected compression parameters (block size and frequency cutoff).</li>
     *   <li>The compressed image is saved to {@code output/} and displayed alongside the original image.</li>
     * </ol>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        Part1 part1 = new Part1();
        int[] sizes = {8, 16, 32, 64, 128, 256, 512, 1048, 2048};
        part1.benchmark(sizes);

       ImagePicker picker = new ImagePicker();
        picker.subscribe(imageInfo -> {
            System.out.println(IMG_ARRIVED);
            Part2 part2 = new Part2();
            part2.compress(imageInfo);
        });
        picker.showUI();
    }
}