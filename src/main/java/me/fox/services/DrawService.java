package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.ConfigManager;
import me.fox.components.ResourceManager;
import me.fox.config.Config;
import me.fox.config.DrawConfig;
import me.fox.listeners.mouse.DrawListener;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.components.draw.impl.Circle;
import me.fox.ui.components.draw.impl.Line;
import me.fox.ui.components.draw.impl.Rectangle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
@Setter
public class DrawService extends JComponent implements Drawable, ConfigManager, ResourceManager {

    private final DrawListener drawListener;

    private final List<Drawable> firstLayer = new ArrayList<>();
    private final List<Drawable> secondLayer = new ArrayList<>();
    private final List<Drawable> thirdLayer = new ArrayList<>();
    private final List<Drawable> drawings = new CopyOnWriteArrayList<>();
    private final List<Drawable> undoDrawings = new CopyOnWriteArrayList<>();

    private Cursor drawCursor;
    private Color drawColor;

    private boolean draw = false, line = false, circle = false, rectangle;
    private boolean fillCircle = true, fillRectangle = true;
    private int currentIndex = -1;
    private float currentStrokeWidth;
    private float decreaseThickness, increaseThickness;

    /**
     * Constructor for {@link DrawService}
     * Set {@link DrawService#drawListener}
     */
    public DrawService() {
        this.drawListener = new DrawListener(this);
        this.registerDrawable(this, 1);
    }

    /**
     * Decreases the {@link DrawService#currentStrokeWidth} by
     * {@link DrawService#decreaseThickness} if {@link DrawService#currentStrokeWidth}
     * minus {@link DrawService#decreaseThickness} is not equal to zero or smaller than zero.
     */
    public void decreaseThickness() {
        if (this.currentStrokeWidth - this.decreaseThickness <= 0) return;
        this.currentStrokeWidth -= this.decreaseThickness;
    }

    /**
     * Increases the {@link DrawService#currentStrokeWidth} with {@link DrawService#increaseThickness}.
     */
    public void increaseThickness() {
        this.currentStrokeWidth += this.increaseThickness;
    }

    /**
     * Resets all draw values which could be changed while taking a screenshot.
     */
    public void resetDraw() {
        this.drawings.clear();
        this.undoDrawings.clear();
        this.currentIndex = -1;
        this.draw         = false;
        this.line         = false;
        this.circle       = false;
        this.rectangle    = false;
    }

    /**
     * Registers a new {@link Drawable}.
     *
     * @param drawable to register
     */
    public void registerDrawable(Drawable drawable, int layer) {
        switch (layer) {
            case 0:
                this.firstLayer.add(drawable);
                break;
            case 1:
                this.secondLayer.add(drawable);
                break;
            case 2:
                this.thirdLayer.add(drawable);
                break;
        }
    }

    /**
     * Increases the {@link DrawService#currentIndex} by one.
     * Adds a new {@link Rectangle} to {@link DrawService#drawings} with
     * the {@code point} and the current {@link DrawService#drawColor},
     * {@link DrawService#currentStrokeWidth} and {@link DrawService#fillRectangle}.
     *
     * @param point where the rectangle starts
     */
    public void addRectangle(Point point) {
        this.currentIndex++;
        this.drawings.add(new Rectangle(point.x, point.y, this.drawColor, currentStrokeWidth, fillRectangle));
    }

    /**
     * Resizes the {@link DrawService#currentIndex} {@link Rectangle} in
     * {@link DrawService#drawings} and sets the size to the {@code point}
     * minus the start {@link Point} which was set in {@link DrawService#addRectangle(Point)},
     * because this is the width and height.
     *
     * @param point to set the size of the {@link Rectangle}
     */
    public void resizeRectangle(Point point) {
        Rectangle rectangle = (Rectangle) this.drawings.get(this.currentIndex);
        rectangle.setSize(point.x - rectangle.x, point.y - rectangle.y);
    }

    /**
     * Increases the {@link DrawService#currentIndex} by one.
     * Adds a new {@link Circle} to {@link DrawService#drawings} with the {@code point}
     * and the current {@link DrawService#drawColor}, {@link DrawService#currentStrokeWidth}
     * and {@link DrawService#fillCircle}.
     *
     * @param point where the circle center is
     */
    public void addCircle(Point point) {
        this.currentIndex++;
        this.drawings.add(new Circle(point.x, point.y, this.drawColor, this.currentStrokeWidth, this.fillCircle));
    }

    /**
     * Resizes the {@link DrawService#currentIndex} {@link Circle} in {@link DrawService#drawings}.
     * If control (keyChar 17) is pressed, it sets the radius of the {@link Circle} (1:1).
     * Otherwise, it sets the width and height of the {@link Circle}.
     *
     * @param point to set the radius or width and height of the {@link Circle}
     */
    public void resizeCurrentCircle(Point point) {
        Circle circle = (Circle) this.drawings.get(this.currentIndex);
        if (Fireshotapp.getInstance().getHotkeyService().getPressedKeys().contains(17)) {
            int radius = (int) Math.sqrt(Math.pow(circle.getX() - point.x, 2) + (Math.pow(circle.getY() - point.y, 2)));
            circle.setSize(radius * 2, radius * 2);
            return;
        }
        int width = Math.abs(point.x - circle.getX()) * 2;
        int height = Math.abs(point.y - circle.getY()) * 2;

        circle.setSize(width, height);
    }

    /**
     * Increases the {@link DrawService#currentIndex} by one.
     * Adds a new {@link Line} to {@link DrawService#drawings}.
     */
    public void addLine() {
        this.currentIndex++;
        this.drawings.add(new Line());
    }

    /**
     * Adds a new {@link Point} to the {@link DrawService#currentIndex} {@link Line}.
     *
     * @param point to add to the line
     */
    public void addPoint(Point point) {
        ((Line) this.drawings.get(this.currentIndex)).addPoint(point);
    }

    /**
     * Removes the latest {@link Drawable} from the list and adds it to the
     * {@link DrawService#undoDrawings} {@link List}, so the user is able to redo it.
     *
     * @see DrawService#redoDrawing()
     */
    public void undoDrawing() {
        if (this.drawings.size() != 0) {
            this.currentIndex--;
            this.undoDrawings.add(this.drawings.get(this.currentIndex + 1));
            this.drawings.remove(this.currentIndex + 1);
        }
    }

    /**
     * Removes the latest {@link Drawable} from the list and adds it
     * to the {@link DrawService#drawings} {@link List} if drawings
     * contains a {@link Drawable}, so the user is able to undo it.
     *
     * @see DrawService#undoDrawing()
     */
    public void redoDrawing() {
        if (this.undoDrawings.size() > 0) {
            this.currentIndex++;
            int size = this.undoDrawings.size();
            this.drawings.add(this.undoDrawings.get(size - 1));
            this.undoDrawings.remove(size - 1);
        }
    }

    @Override
    public void applyConfig(Config config) {
        DrawConfig drawConfig = config.getDrawConfig();
        this.currentStrokeWidth = drawConfig.getDefaultThickness();
        this.decreaseThickness  = drawConfig.getThicknessDecrease();
        this.increaseThickness  = drawConfig.getThicknessIncrease();
        this.drawColor          = Color.decode(drawConfig.getColor());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        this.firstLayer.forEach(var -> var.draw(g2d));
        this.secondLayer.forEach(var -> var.draw(g2d));
        this.thirdLayer.forEach(var -> var.draw(g2d));
        repaint();
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.drawings.forEach(var -> var.draw(g2d));
    }

    @Override
    public void applyResources(List<File> files) {
        Optional<File> fileOptional = files.stream().filter(var -> var.getName().equals("pencilw.png")).findFirst();

        fileOptional.ifPresent(var -> {
            try {
                this.drawCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                        ImageIO.read(var),
                        new Point(5, 25),
                        "drawing"
                );
                System.out.println("Test" + this.drawCursor);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}