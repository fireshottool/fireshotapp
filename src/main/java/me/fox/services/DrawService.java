package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshot;
import me.fox.listeners.keyboard.DrawListenerK;
import me.fox.listeners.mouse.DrawListenerM;
import me.fox.ui.components.draw.Circle;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.components.draw.Line;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
@Setter
public class DrawService extends JComponent implements Drawable {

    private final DrawListenerM drawListenerM;
    private final DrawListenerK drawListenerK;

    private final List<Drawable> firstLayer = new ArrayList<>();
    private final List<Drawable> secondLayer = new ArrayList<>();
    private final List<Drawable> thirdLayer = new ArrayList<>();
    private final List<Drawable> drawings = new CopyOnWriteArrayList<>();
    private final List<Drawable> undoDrawings = new CopyOnWriteArrayList<>();

    private boolean draw = false, line = false, circle = false;
    private int currentIndex = -1;
    private float currentStrokeWidth;
    private float decreaseThickness, increaseThickness;
    private Color drawColor;

    public DrawService() {
        this.drawListenerM = new DrawListenerM(this);
        this.drawListenerK = new DrawListenerK(this);
        this.registerDrawable(this, 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        this.firstLayer.forEach(var -> var.draw(g2d));
        this.secondLayer.forEach(var -> var.draw(g2d));
        this.thirdLayer.forEach(var -> var.draw(g2d));
        repaint();
    }

    public void resetDraw() {
        this.drawings.clear();
        this.undoDrawings.clear();
        this.currentIndex = -1;
        this.draw = false;
        this.line = false;
        this.circle = false;
    }

    /**
     * Register a new {@link Drawable}
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

    public void addCircle(Point point) {
        this.currentIndex++;
        this.drawings.add(new Circle(point.x, point.y, drawColor));
    }

    public void resizeCurrentCircle(Point point) {
        Circle circle = (Circle) this.drawings.get(this.currentIndex);
        if (Fireshot.getInstance().getHotkeyService().getPressedKeys().contains(17)) {
            int radius = (int) Math.sqrt(Math.pow(circle.getX() - point.x, 2) + (Math.pow(circle.getY() - point.y, 2)));
            circle.setSize(radius * 2, radius * 2);
            return;
        }
        int width = Math.abs(point.x - circle.getX()) * 2;
        int height = Math.abs(point.y - circle.getY()) * 2;

        circle.setSize(width, height);
    }

    public void addPoint(Point point) {
        ((Line) this.drawings.get(this.currentIndex)).addPoint(point);
    }

    public void addLine() {
        this.currentIndex++;
        this.drawings.add(new Line());
    }

    public void undoDrawing() {
        if (this.drawings.size() != 0) {
            this.currentIndex--;
            this.undoDrawings.add(this.drawings.get(this.currentIndex + 1));
            this.drawings.remove(this.currentIndex + 1);
        }
    }

    public void redoDrawing() {
        if (this.undoDrawings.size() > 0) {
            this.currentIndex++;
            int size = this.undoDrawings.size();
            this.drawings.add(this.undoDrawings.get(size - 1));
            this.undoDrawings.remove(size - 1);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.drawings.forEach(var -> var.draw(g2d));
    }
}
