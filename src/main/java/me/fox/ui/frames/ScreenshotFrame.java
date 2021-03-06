package me.fox.ui.frames;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
public class ScreenshotFrame extends JFrame {

    /**
     * Constructor for {@link ScreenshotFrame}
     * <p>
     * Calls the super constructor from {@link JFrame} to set
     * the title to {@param title}.
     * Loads the basic properties for the {@link JFrame}.
     *
     * @param title for the {@link JFrame}
     */
    public ScreenshotFrame(String title) {
        super(title);
        this.loadFrame();
    }

    /**
     * Loads the basic properties for the {@link JFrame}.
     */
    private void loadFrame() {
        this.setFocusable(true);
        this.setUndecorated(true);
        this.setLayout(null);
        this.setVisible(false);
        this.setAlwaysOnTop(true);
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.calculateFrameSize();
    }

    /**
     * Calculates the {@link JFrame} {@link JFrame#getSize()} and {@link JFrame#getLocation()}
     * {@link Arrays#stream(Object[])} through all connected {@link GraphicsDevice} and calculates
     * the lowest value for x and y and the width and height for all connected {@link GraphicsDevice}.
     */
    private void calculateFrameSize() {
        AtomicInteger width = new AtomicInteger(0);
        AtomicInteger height = new AtomicInteger(0);
        AtomicInteger lowestX = new AtomicInteger(0);
        AtomicInteger lowestY = new AtomicInteger(0);

        Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()).forEach(var -> {
            Rectangle rectangle = var.getDefaultConfiguration().getBounds();

            if (rectangle.x < lowestX.get()) {
                lowestX.set(rectangle.x);
                width.addAndGet(var.getDefaultConfiguration().getBounds().width);
            } else if (rectangle.x >= width.get()) {
                width.addAndGet(var.getDefaultConfiguration().getBounds().width);
            } else if (rectangle.x + rectangle.width > lowestX.get() + width.get()) {
                width.addAndGet(rectangle.x);
            }

            if (rectangle.y < lowestY.get()) {
                lowestY.set(rectangle.y);
                height.addAndGet(var.getDefaultConfiguration().getBounds().height);
            } else if (rectangle.y >= height.get() || rectangle.y + rectangle.height > lowestY.get() + height.get()) {
                height.addAndGet(var.getDefaultConfiguration().getBounds().height);
            } else if (rectangle.y + rectangle.height > lowestY.get() + height.get()) {
                height.addAndGet(rectangle.y);
            }
        });

        this.setLocation(lowestX.get(), lowestY.get());
        this.setSize(width.get(), height.get());
    }

    /**
     * Registers a new {@link MouseAdapter} to the {@link JFrame}.
     *
     * @param mouseAdapter to register
     */
    public void registerMouseListener(MouseAdapter mouseAdapter) {
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        this.addMouseWheelListener(mouseAdapter);
    }

    /**
     * Updates the cursor.
     *
     * @param cursorType to update with {@link Cursor} class
     */
    public void updateCursor(int cursorType) {
        if (!this.isValidCursor(cursorType)) return;
        this.setCursor(Cursor.getPredefinedCursor(cursorType));
    }

    /**
     * Checks whether the {@param cursorType} is valid.
     *
     * @param cursorType to check
     * @return if the specified cursor type is invalid
     */
    private boolean isValidCursor(int cursorType) {
        try {
            Cursor.getPredefinedCursor(cursorType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
