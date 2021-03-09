package me.fox.ui.panels.toolbox;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.ResourceManager;
import me.fox.constants.ColorPalette;
import me.fox.enums.ToolboxType;
import me.fox.listeners.mouse.ToolboxListener;
import me.fox.services.ScreenshotService;
import me.fox.ui.components.ScalableRectangle;
import me.fox.ui.components.toolbox.ToolboxComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
@Setter
public abstract class Toolbox extends JPanel implements ResourceManager {

    private final ToolboxListener toolboxListener = new ToolboxListener(this);
    private final List<ToolboxComponent> toolboxComponents = new ArrayList<>();

    private final ToolboxType toolboxType;

    private BufferedImage backgroundImage;

    /**
     * Constructor for {@link Toolbox}
     *
     * @param toolboxType type of the toolbox
     */
    public Toolbox(ToolboxType toolboxType) {
        this.toolboxType = toolboxType;
        this.load();
        this.loadToolboxComponents();
        this.registerMouseListener(this.toolboxListener);
    }

    public abstract void loadToolboxComponents();

    public abstract void resetResources();

    public abstract void reset();

    /**
     * Loads the fundamentals of the {@link JPanel}.
     */
    private void load() {
        this.setVisible(false);
        this.setLayout(null);
    }

    /**
     * Adds a new {@link ToolboxComponent} to {@link Toolbox#toolboxComponents}.
     *
     * @param toolboxComponent the {@link ToolboxComponent} add
     */
    public void addComponent(ToolboxComponent toolboxComponent) {
        this.add(toolboxComponent);
        this.toolboxComponents.add(toolboxComponent);
        this.adjust();
    }

    /**
     * Updates the location of the {@link Toolbox}.
     *
     * @param scalableRectangle rectangle needed to update the location
     */
    public void updateLocation(ScalableRectangle scalableRectangle) {
        int x = 0;
        int y = 0;
        switch (this.getToolboxType()) {
            case HORIZONTAL:
                x = scalableRectangle.x + scalableRectangle.width - this.getWidth();
                y = scalableRectangle.y - this.getHeight() - 10;
                break;
            case VERTICAL:
                x = scalableRectangle.x + scalableRectangle.width + 10;
                y = scalableRectangle.y + scalableRectangle.height - this.getHeight();
                break;
        }
        this.setLocation(x, y);
    }

    /**
     * Adjusts the {@link Toolbox} size.
     */
    private void adjust() {
        int height = 0;
        int width = 0;
        switch (this.toolboxType) {
            case HORIZONTAL:
                if (this.toolboxComponents.size() == 0) return;
                width = this.calculateWidthAndReplace();
                height = this.calculateMinHeightAndReplace();
                break;
            case VERTICAL:
                if (this.toolboxComponents.size() == 0) return;
                width = this.calculateMinWidthAndReplace();
                height = this.calculateHeightAndReplace();
                break;
        }
        this.setSize(width, height);
    }

    /**
     * Calculates the new {@link Toolbox#getWidth()}.
     * Relocates the x coordinate {@link Toolbox#toolboxComponents}.
     *
     * @return the new {@link Toolbox#getWidth()}
     */
    private int calculateWidthAndReplace() {
        AtomicInteger width = new AtomicInteger(10);
        this.toolboxComponents.stream().filter(var -> var.getWidth() > 0).forEach(var -> {
            var.setLocation(width.get(), var.getY());
            width.set(width.get() + var.getWidth());
            width.set(width.get() + 10);
        });

        return width.get();
    }

    /**
     * Calculates the new {@link Toolbox#getHeight()}.
     * Relocates the y coordinate {@link Toolbox#toolboxComponents}.
     *
     * @return the new {@link Toolbox#getHeight()}
     */
    private int calculateHeightAndReplace() {
        AtomicInteger height = new AtomicInteger(10);
        this.toolboxComponents.stream().filter(var -> var.getHeight() > 0).forEach(var -> {
            var.setLocation(var.getX(), height.get());
            height.set(height.get() + var.getHeight());
            height.set(height.get() + 10);
        });

        return height.get();
    }

    /**
     * Calculates the new minimum needed {@link Toolbox#getWidth()}.
     *
     * @return the new minimum needed {@link Toolbox#getWidth()}
     */
    private int calculateMinWidthAndReplace() {
        AtomicInteger width = new AtomicInteger(0);
        this.toolboxComponents.stream().filter(var -> var.getWidth() > 0).forEach(var -> {
            if (var.getWidth() > width.get()) {
                width.set(var.getWidth());
            }
            var.setLocation(10, var.getHeight());
        });
        width.set(width.get() + 20);
        return width.get();
    }

    /**
     * Calculates the new minimum needed {@link Toolbox#getHeight()}.
     *
     * @return the new minimum needed {@link Toolbox#getHeight()}
     */
    private int calculateMinHeightAndReplace() {
        AtomicInteger height = new AtomicInteger(0);
        this.toolboxComponents.stream().filter(var -> var.getHeight() > 0).forEach(var -> {
            if (var.getHeight() > height.get()) {
                height.set(var.getHeight());
            }
            var.setLocation(var.getX(), 10);
        });
        height.set(height.get() + 20);
        return height.get();
    }

    /**
     * Register a new {@link MouseAdapter} to the {@link JPanel}.
     *
     * @param mouseListenerAdapter to register
     */
    public void registerMouseListener(MouseAdapter mouseListenerAdapter) {
        this.addMouseListener(mouseListenerAdapter);
        this.addMouseMotionListener(mouseListenerAdapter);
        this.addMouseWheelListener(mouseListenerAdapter);
    }

    /**
     * Hides the {@link Toolbox}.
     */
    public void hideSelf() {
        this.setVisible(false);
    }

    /**
     * Updates the location of the {@link Toolbox}.
     * Shows the {@link Toolbox}.
     */
    public void showSelf() {
        this.updateLocation(Fireshotapp.getInstance().use(ScreenshotService.class).getSelectionRectangle());
        this.setVisible(true);
    }

    @Override
    public void applyResources(List<File> files) {
        Optional<File> optionalFile = files.stream().filter(var -> var.getName().equals("toolboxbg.png")).findFirst();
        optionalFile.ifPresent(var -> {
            try {
                this.setBackgroundImage(ImageIO.read(var));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        if (optionalFile.isPresent()) return;
        this.setBackgroundImage(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(ColorPalette.DARK_BLUE_200);
        g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
        g2d.setColor(ColorPalette.DARK_BLUE_LIGHTER_200);
        g2d.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1, 10, 10);
    }
}
