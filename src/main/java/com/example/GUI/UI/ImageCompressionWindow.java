package com.example.GUI.UI;

import com.example.GUI.Style;
import com.example.assignment.Part2;
import com.example.lib.utils.ImageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.example.GUI.factory.StylingFactory.getStyledButton;
import static com.example.lib.utils.ImageUtils.saveAsBMP;

/**
 * Main application window for the DCT image compression tool.
 * <p>
 * This Swing {@link JFrame} provides:
 * <ul>
 *   <li>A left control panel with buttons to choose an image and to compress it.</li>
 *   <li>A right area showing side-by-side "Original" and "Compressed" image previews.</li>
 *   <li>Integration with {@link ImagePicker} to select images and {@link IntegersPicker} to select
 *       compression parameters (block size F and diagonal threshold d).</li>
 * </ul>
 * <p>
 * Compression itself is delegated to {@link Part2#compress(Pair, int, int)}; compressed images are saved
 * in BMP format to the project's output directory and displayed in the UI.
 * </p>
 */
public class ImageCompressionWindow extends JFrame {

    /**
     * Logger used to track UI actions and warnings.
     */
    private static final Log log =
            LogFactory.getLog(ImageCompressionWindow.class);

    /**
     * The currently selected image to compress. Set by the image picker callback.
     */
    private BufferedImage selectedImage;

    /**
     * Name (base filename without extension) of the currently selected image.
     * Used to label previews and to build the output filename.
     */
    private String selectedImageName;

    /**
     * Constructs and shows the Image Compression Tool window.
     * <p>
     * The constructor:
     * <ul>
     *   <li>Builds a left-side control panel with "Choose Image" and "Compress" buttons</li>
     *   <li>Builds a right-side display area with "Original" and "Compressed" image boxes</li>
     *   <li>Wires up the {@link ImagePicker} to set the selected image and update the preview</li>
     *   <li>Wires up the {@link IntegersPicker} to obtain compression parameters F and d, then calls
     *       {@link Part2#compress(Pair, int, int)} and displays/saves the result</li>
     * </ul>
     * The frame is maximized on creation and made visible.
     */
    public ImageCompressionWindow() {
        super("DCT Image Compression Tool");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // =========================
        // LEFT PANEL (CONTROL PANEL)
        // =========================
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(240, 1024));
        leftPanel.setBackground(new Color(30, 30, 30));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        JButton chooseImageButton = getStyledButton("Choose Image", Style.STYLE2);
        JButton compressButton = getStyledButton("Compress Image", Style.STYLE3);


        chooseImageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        compressButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Space the buttons vertically within the left panel
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(chooseImageButton);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(compressButton);
        leftPanel.add(Box.createVerticalGlue());

        // =========================
        // RIGHT PANEL (IMAGES)
        // =========================
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(new Color(45, 45, 45));

        // Two boxes to preview images before and after compression
        JPanel originalBox = createImageBox("Original");
        JPanel compressedBox = createImageBox("Compressed");

        // A vertical separator is prepared (not added between the boxes in this specific layout,
        // but left in code if desired to add later).
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(3, 500));
        separator.setBackground(new Color(180, 180, 180));
        separator.setOpaque(true);

        // GridBag constraints for left and right panes to ensure they share space evenly
        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0;
        left.gridy = 0;
        left.weightx = 1.0;
        left.fill = GridBagConstraints.BOTH;
        left.insets = new Insets(10, 10, 10, 10);
        left.anchor = GridBagConstraints.CENTER;

        GridBagConstraints sep = new GridBagConstraints();
        sep.gridx = 1;
        sep.gridy = 0;
        sep.weightx = 0;
        sep.fill = GridBagConstraints.VERTICAL;
        sep.insets = new Insets(0, 20, 0, 20);

        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 2;
        right.gridy = 0;
        right.weightx = 1.0;
        right.fill = GridBagConstraints.BOTH;
        right.insets = new Insets(10, 10, 10, 10);
        right.anchor = GridBagConstraints.CENTER;

        rightPanel.add(originalBox, left);
        rightPanel.add(compressedBox, right);

        // =========================
        // PICKERS
        // =========================
        ImagePicker imagePicker = new ImagePicker();
        IntegersPicker integerPicker = new IntegersPicker();

        // Subscribe to the ImagePicker: when the user selects an image, store both the
        // image and its base filename (without extension), log the selection, and show
        // the original image in the left preview box.
        imagePicker.subscribe(pair -> {
            // Extract a name without its extension for display and file naming
            selectedImageName = pair.getFirst().substring(0, pair.getFirst().lastIndexOf('.'));
            selectedImage = pair.getSecond();

            log.info("Selected image: " + selectedImageName);

            // Render the original image preview
            showImage(originalBox, selectedImage, selectedImageName);
        });

        // Show the image picker when the "Choose Image" button is clicked
        chooseImageButton.addActionListener(e -> imagePicker.showUI());

        // When the "Compress" button is clicked, verify an image is selected, then
        // show the integer picker to obtain F and d. After the user confirms, compress
        // with Part2, save the compressed BMP and update the compressed-image preview.
        compressButton.addActionListener(e -> {

            if (selectedImage == null) {
                log.warn("No image selected!");
                return;
            }

            integerPicker.subscribe(pair -> {

                int F = pair.getFirst();
                int d = pair.getSecond();

                log.info("Compressing with F=" + F + " d=" + d);

                Part2 part2 = new Part2();
                BufferedImage selectedCopy = ImageUtils.copyBufferedImage(selectedImage);
                // Part2.compress expects a Pair<String, BufferedImage> where the first is a filename
                BufferedImage compressed =
                        part2.compress(new Pair<>(selectedImageName, selectedCopy), F, d);

                // Save the compressed image with a "_compressed" suffix and display it
                saveAsBMP(compressed, "output/" + selectedImageName + "_compressed");
                showImage(compressedBox, compressed, selectedImageName + "_compressed");
            });

            // Display the integer picker UI to obtain (F, d)
            integerPicker.showUI();
        });

        // =========================
        // FRAME
        // =========================
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // =========================
    // IMAGE BOX
    // =========================

    /**
     * Creates a stylized panel that serves as a placeholder for an image preview.
     * <p>
     * Each box is a white panel with a subtle rounded border and an initial centered title label.
     *
     * @param title the placeholder title displayed before an image is rendered (e.g., "Original")
     * @return a configured {@link JPanel} ready to receive an image preview
     */
    private JPanel createImageBox(String title) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(450, 320));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(80, 80, 80));

        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    // =========================
    // IMAGE DISPLAY
    // =========================

    /**
     * Renders the provided image into the specified image box along with metadata (dimensions and file size).
     * <p>
     * This method:
     * <ol>
     *   <li>Ensures the image is in RGB format by drawing it on an RGB BufferedImage</li>
     *   <li>Attempts to determine the saved BMP file size from {@code output/<name>.bmp} (in kB)</li>
     *   <li>Scales the image to fit the preview area and places it inside a small labeled container</li>
     * </ol>
     *
     * Note: The file size shown depends on the existence and contents of {@code output/<name>.bmp}.
     *
     * @param box  the target preview panel (one created by {@link #createImageBox})
     * @param image the image to display
     * @param name  base filename used for the preview title and for checking an output file
     */
    private void showImage(JPanel box, BufferedImage image, String name) {

        // Convert the image to a TYPE_INT_RGB BufferedImage for consistent display
        BufferedImage rgb = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgb.createGraphics();
        // Fill background with white to avoid black/transparent borders when scaling
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g.drawImage(image, 0, 0, null);
        g.dispose();

        // =========================
        // SIZE IN MEMORY (KB)
        // =========================
        // Attempt to obtain the file size from the saved BMP file in the output directory.
        File file = new File("output/" + name + ".bmp");
        long bytes = file.length();
        double kb = bytes / 1024.0;

        String sizeText = String.format("<html>%d x %d pixel <br> %.2f kB</html>", image.getWidth(), image.getHeight(), kb);

        // Scale the image for the preview (maintains aspect ratio via getScaledInstance)
        Image scaled = rgb.getScaledInstance(
                420,
                280,
                Image.SCALE_SMOOTH
        );

        // Build a small container with image, name, and size labels
        JPanel container = createImageLabel(name, scaled, sizeText);

        // Replace the placeholder content in the image box and refresh UI
        box.removeAll();
        box.add(container, BorderLayout.CENTER);
        box.revalidate();
        box.repaint();
    }

    /**
     * Builds a container holding an image preview and two labels (name and size).
     *
     * @param name      the display name shown above the image
     * @param scaled    the already scaled Image instance to embed in an {@link ImageIcon}
     * @param sizeText  a small HTML string with width x height and file size (kB)
     * @return a configured {@link JPanel} with the image and labels
     */
    private static JPanel createImageLabel(String name, Image scaled, String sizeText) {

        JLabel imageLabel = new JLabel(new ImageIcon(scaled));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);

        JLabel sizeLabel = new JLabel(sizeText, SwingConstants.CENTER);

        JPanel container = new JPanel(new BorderLayout());

        container.add(imageLabel, BorderLayout.CENTER);
        container.add(nameLabel, BorderLayout.NORTH);
        container.add(sizeLabel, BorderLayout.SOUTH);

        return container;
    }

}