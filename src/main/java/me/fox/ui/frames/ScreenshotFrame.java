package me.fox.ui.frames;

import lombok.Getter;
import me.fox.adapter.MouseListenerAdapter;
import me.fox.services.DrawService;
import me.fox.ui.components.ScalableRectangle;
import me.fox.utils.Util;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
public class ScreenshotFrame extends JFrame {

    private final ScalableRectangle scalableRectangle;

    /**
     * Constructor for {@link ScreenshotFrame}
     * Call the super Constructor from {@link JFrame} to set the title to {@param title}
     * Load the basic properties for the {@link JFrame}
     *
     * @param title for the {@link JFrame}
     */
    public ScreenshotFrame(String title, DrawService drawService) {
        super(title);
        this.loadFrame();
        this.setContentPane(drawService);
        scalableRectangle = new ScalableRectangle(drawService);
        this.registerMouseListener(scalableRectangle.getListener());
    }

    /**
     * Load the basic properties for the {@link JFrame}
     */
    private void loadFrame() {
        this.setFocusable(true);
        this.setUndecorated(true);
        this.setLayout(null);
        this.setVisible(true);
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.calculateFrameSize();
    }

    /**
     * Calculate the {@link JFrame} {@link JFrame#getSize()} and {@link JFrame#getLocation()}
     * {@link Arrays#stream(Object[])} through all connected {@link GraphicsDevice} and calculate the lowest
     * value for x and y and the width and height for all connected {@link GraphicsDevice} together
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
     * Register a new {@link MouseListenerAdapter} to the {@link JFrame}
     *
     * @param mouseListenerAdapter to register
     */
    public void registerMouseListener(MouseListenerAdapter mouseListenerAdapter) {
        this.addMouseListener(mouseListenerAdapter);
        this.addMouseMotionListener(mouseListenerAdapter);
        this.addMouseWheelListener(mouseListenerAdapter);
    }

    /**
     * Update the cursor
     * @param cursorType to update with {@link Cursor} class
     */
    public void updateCursor(int cursorType) {
        if (!Util.isValidCursor(cursorType)) return;
        this.setCursor(Cursor.getPredefinedCursor(cursorType));
    }
}
