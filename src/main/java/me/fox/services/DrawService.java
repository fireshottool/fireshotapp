package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.listeners.mouse.DrawListener;
import me.fox.ui.components.Drawable;
import me.fox.ui.components.Line;

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

    private final DrawListener drawListener;

    private final List<Drawable> firstLayer = new ArrayList<>();
    private final List<Drawable> secondLayer = new ArrayList<>();
    private final List<Drawable> thirdLayer = new ArrayList<>();
    private final List<Line> lines = new CopyOnWriteArrayList<>();
    private final List<Line> undoLines = new CopyOnWriteArrayList<>();

    private boolean draw = false;
    private int currentIndex = -1;
    private float currentStrokeWidth;
    private float decreaseThickness, increaseThickness;
    private Color drawColor;

    public DrawService() {
        this.drawListener = new DrawListener(this);
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
        this.lines.clear();
        this.undoLines.clear();
        this.currentIndex = -1;
    }

    /**
     * Register a new {@link Drawable}
     *
     * @param drawable to register
     */
    public void registerDrawable(Drawable drawable, int layer) {
        switch (layer) {
            case 0:
                firstLayer.add(drawable);
                break;
            case 1:
                secondLayer.add(drawable);
                break;
            case 2:
                thirdLayer.add(drawable);
                break;
        }
    }

    public void addPoint(Point point) {
        lines.get(currentIndex).addPoint(point);
    }

    public void addLine() {
        currentIndex++;
        lines.add(new Line());
    }

    public void undoLine() {
        if (lines.size() != 0) {
            currentIndex--;
            undoLines.add(lines.get(currentIndex + 1));
            lines.remove(currentIndex + 1);
        }
    }

    public void redoLine() {
        if (undoLines.size() > 0) {
            currentIndex++;
            int size = undoLines.size();
            lines.add(undoLines.get(size - 1));
            undoLines.remove(size - 1);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.drawLines(g2d);
    }

    public void drawLines(Graphics2D g2d) {
        this.lines.forEach(var -> {
            g2d.setStroke(var.getStroke());

            for (int j = 0; j < var.getPoints().size(); j++) {
                List<Point> points = var.getPoints();
                g2d.setColor(var.getColor());

                if (j + 1 != points.size()) {
                    g2d.drawLine(points.get(j).x, points.get(j).y, points.get(j + 1).x, points.get(j + 1).y);
                } else {
                    g2d.fillRect(points.get(j).x, points.get(j).y, 1, 1);
                }
            }
        });
    }
}
