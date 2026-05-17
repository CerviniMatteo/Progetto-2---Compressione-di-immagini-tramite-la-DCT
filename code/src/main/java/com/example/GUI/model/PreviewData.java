package com.example.GUI.model;

import java.awt.*;

/**
 * Cached preview data created off the EDT.
 */
public record PreviewData(Image scaled, String sizeText) {}