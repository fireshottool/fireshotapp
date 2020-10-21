package me.fox.ui.components;

import lombok.Getter;
import lombok.Setter;
import me.fox.adapter.Rectangle;
import me.fox.listener.ScalableRectListener;

import java.awt.*;
import java.util.Arrays;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
@Setter
public class ScalableRectangle extends Rectangle {

    private final ScalePoint[] scalePoints = this.createPoints();
    private final ScalableRectListener listener = new ScalableRectListener(this);

    private int cursor = 1;

    private ScalePoint[] createPoints() {
        ScalePoint[] scalePoints = new ScalePoint[8];
        int dirCount = 4;
        for (int i = 0; i < scalePoints.length; i++) {
            scalePoints[i] = new ScalePoint(dirCount);
            dirCount++;
        }
        System.out.println(Arrays.toString(scalePoints));
        return scalePoints;
    }

    public boolean isPointInRect(Point point) {
        return point.x > this.x && point.y > this.y && point.x < this.x + this.width && point.y < this.y + this.height;
    }
}
