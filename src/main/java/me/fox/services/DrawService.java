package me.fox.services;

import lombok.Getter;
import me.fox.ui.components.Drawable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
public class DrawService extends JComponent {

    private final List<Drawable> firstLayer = new ArrayList<>();
    private final List<Drawable> secondLayer = new ArrayList<>();
    private final List<Drawable> thirdLayer = new ArrayList<>();

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
}
