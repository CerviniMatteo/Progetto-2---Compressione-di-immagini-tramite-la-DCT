package com.example.UI;

import com.example.lib.utils.Observable;
import org.apache.commons.math3.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.lib.constants.PickerConstants.*;

public class ImagePicker {

    private final Observable<Pair<String, BufferedImage>> observable =
            new Observable<>();

    public void subscribe(java.util.function.Consumer<Pair<String, BufferedImage>> p) {
        observable.subscribe(p);
    }

    public void showUI() {
        String home = System.getProperty(HOME_PATH);

        Path downloads = Paths.get(home, DOWNLOAD_PATH);

        if (!Files.exists(downloads)) {
            downloads = Paths.get(home, SCARICATI_PATH);
        }

        Path fullPath = downloads.resolve(
                PROJECT_DIR_PATH
        ).resolve(IMMAGINI);


        JFileChooser fc = new JFileChooser();
        if (Files.exists(fullPath)) {
            fc.setCurrentDirectory(fullPath.toFile());
        } else {
            fc.setCurrentDirectory(downloads.toFile());
        }

        fc.setSize(new Dimension(1240, 1240));
        int result = fc.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();

        try {
            BufferedImage img = ImageIO.read(file);

            if (img == null) return;

            observable.set(new Pair<>(file.getName(), img));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}