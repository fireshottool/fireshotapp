package me.fox.ui.components.draw.impl;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.ui.components.draw.Drawable;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

@Getter
public class Line implements Drawable {

    private final List<Point> points = new CopyOnWriteArrayList<>();

    private final Color color = Fireshot.getInstance().getDrawService().getDrawColor();
    private final Stroke stroke = new BasicStroke(Fireshot.getInstance().getDrawService().getCurrentStrokeWidth(),
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND);

    public void addPoint(Point point) {
        this.points.add(point);
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setStroke(this.getStroke());

        for (int j = 0; j < this.getPoints().size(); j++) {
            List<Point> points = this.getPoints();
            g2d.setColor(this.getColor());

            if (j + 1 != points.size()) {
                g2d.drawLine(points.get(j).x, points.get(j).y, points.get(j + 1).x, points.get(j + 1).y);
            } else {
                g2d.fillRect(points.get(j).x, points.get(j).y, 1, 1);
            }
        }
    }
}
