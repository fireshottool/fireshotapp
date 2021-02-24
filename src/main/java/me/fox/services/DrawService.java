package me.fox.services;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.ConfigManager;
import me.fox.components.ResourceManager;
import me.fox.config.Config;
import me.fox.config.DrawConfig;
import me.fox.enums.LayerType;
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
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
@Setter
public class DrawService extends JComponent implements ConfigManager, ResourceManager {

    private final DrawListener drawListener;

    private final Stack<Drawable> drawings = new Stack<>();
    private final Stack<Drawable> removedDrawings = new Stack<>();

    private final List<Drawable> backgroundLayer = new CopyOnWriteArrayList<>();
    private final List<Drawable> foregroundLayer = new CopyOnWriteArrayList<>();

    private Cursor drawCursor;
    private Color drawColor;

    private boolean draw = false;
    private boolean line = false;
    private boolean circle = false;
    private boolean rectangle;
    private boolean fillCircle = true;
    private boolean fillRectangle = true;

    private float currentStrokeWidth;
    private float decreaseThickness, increaseThickness;

    /**
     * Constructor for {@link DrawService}
     * Set {@link DrawService#drawListener}
     */
    public DrawService() {
        this.drawListener = new DrawListener(this);
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
        this.removedDrawings.clear();
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
    public void registerDrawable(Drawable drawable, @NonNull LayerType type) {
        if (type == LayerType.FOREGROUND) {
            this.foregroundLayer.add(drawable);
        } else if (type == LayerType.BACKGROUND) {
            this.backgroundLayer.add(drawable);
        }
    }

    /**
     * Adds a new {@link Rectangle} to {@link DrawService#drawings} with
     * the {@code point} and the current {@link DrawService#drawColor},
     * {@link DrawService#currentStrokeWidth} and {@link DrawService#fillRectangle}.
     *
     * @param point where the rectangle starts
     */
    public void addRectangle(Point point) {
        this.drawings.push(new Rectangle(point.x, point.y, this.drawColor, currentStrokeWidth, fillRectangle));
    }

    /**
     * Sets the size to the {@code point}
     * minus the start {@link Point} which was set in {@link DrawService#addRectangle(Point)},
     * because this is the width and height.
     *
     * @param point to set the size of the {@link Rectangle}
     */
    public void resizeRectangle(Point point) {
        Rectangle rectangle = (Rectangle) this.drawings.peek();
        rectangle.setSize(point.x - rectangle.x, point.y - rectangle.y);
    }

    /**
     * Adds a new {@link Circle} to {@link DrawService#drawings} with the {@code point}
     * and the current {@link DrawService#drawColor}, {@link DrawService#currentStrokeWidth}
     * and {@link DrawService#fillCircle}.
     *
     * @param point where the circle center is
     */
    public void addCircle(Point point) {
        this.drawings.push(new Circle(point.x, point.y, this.drawColor, this.currentStrokeWidth, this.fillCircle));
    }

    /**
     * Resizes the {@link Circle} in {@link DrawService#drawings}.
     * If control (keyChar 17) is pressed, it sets the radius of the {@link Circle} (1:1).
     * Otherwise, it sets the width and height of the {@link Circle}.
     *
     * @param point to set the radius or width and height of the {@link Circle}
     */
    public void resizeCurrentCircle(Point point) {
        Circle circle = (Circle) this.drawings.peek();
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
     * Adds a new {@link Line} to {@link DrawService#drawings}.
     */
    public void addLine() {
        this.drawings.push(new Line());
    }

    /**
     * Adds a new {@link Point} to the {@link Line}.
     *
     * @param point to add to the line
     */
    public void addPoint(Point point) {
        ((Line) this.drawings.peek()).addPoint(point);
    }

    /**
     * Removes the latest {@link Drawable} from the list and adds it to the
     * {@link DrawService#removedDrawings} {@link List}, so the user is able to redo it.
     *
     * @see DrawService#redoDrawing()
     */
    public void undoDrawing() {
        if (this.drawings.size() != 0) {
            this.removedDrawings.push(this.drawings.peek());
            this.drawings.pop();
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
        if (this.removedDrawings.size() > 0) {
            int size = this.removedDrawings.size();
            this.drawings.push(this.removedDrawings.get(size - 1));
            this.removedDrawings.remove(size - 1);
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

        this.backgroundLayer.forEach(var -> var.draw(g2d));
        this.drawings.forEach(var -> var.draw(g2d));
        this.foregroundLayer.forEach(var -> var.draw(g2d));
        repaint();
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