package com.example.lib.constants;

public class GuiConstants {
    public static final String APP_TITLE = "DCT Image Compression Tool";
    public static final String PART_CHOOSER_TITLE = "Choose what assignment part to be run";

    public static final String BUTTON_CHOOSE_IMAGE = "Choose Image";
    public static final String BUTTON_COMPRESS_IMAGE = "Compress Image";
    public static final String BUTTON_PART1 = "Part1";
    public static final String BUTTON_PART2 = "Part2";

    public static final String LABEL_ORIGINAL = "Original";
    public static final String LABEL_COMPRESSED = "Compressed";

    public static final String FONT_ARIAL = "Arial";
    public static final String FONT_SANS_SERIF = "SansSerif";

    public static final String COMPRESSED_SUFFIX = "_compressed";
    public static final String OUTPUT_DIR_NAME = "output";
    public static final String FILE_EXTENSION_BMP = ".bmp";

    public static final String IMAGE_METADATA_TEMPLATE = "<html>%d x %d pixel <br> %.2f kB</html>";

    public static final String LOG_OPENING_IMAGE_PICKER = "Opening image picker dialog";
    public static final String LOG_IMAGE_SELECTED = "Image selected: %s (size: %dx%d pixels)";
    public static final String LOG_COMPRESS_WITHOUT_IMAGE = "Compress action triggered but no image selected";
    public static final String LOG_OPENING_PARAMETERS_PICKER = "Opening compression parameters picker";
    public static final String LOG_COMPRESSION_START = "Compression started with parameters: F=%d, d=%d";
    public static final String LOG_COMPRESSION_DONE = "Compression completed: %s (size: %dx%d pixels)";
    public static final String LOG_COMPRESSION_FAILED_PREFIX = "Compression failed: ";

    public static final String LOG_PART1_SELECTED = "Part 1 (Benchmark) selected - launching DCT benchmark";
    public static final String LOG_PART2_SELECTED = "Part 2 (Compression GUI) selected - launching image compression tool";
    public static final String LOG_BENCHMARK_THREAD_START = "Benchmark thread started";
    public static final String LOG_BENCHMARK_THREAD_DONE = "Benchmark thread completed";
}

