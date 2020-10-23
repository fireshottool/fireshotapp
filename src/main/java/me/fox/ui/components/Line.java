package me.fox.ui.components;

import lombok.Getter;
import me.fox.Fireshot;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

@Getter
public class Line {

    private final List<Point> points = new CopyOnWriteArrayList<>();
    private final Color color = Fireshot.getInstance().getDrawService().getDrawColor();
    private final Stroke stroke = new BasicStroke(Fireshot.getInstance().getDrawService().getCurrentStrokeWidth(),
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND);

    public void addPoint(Point point) {
        this.points.add(point);
    }
}
