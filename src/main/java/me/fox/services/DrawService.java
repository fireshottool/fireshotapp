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

    private final List<Drawable> drawables = new ArrayList<>();

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        this.drawables.forEach(var -> var.draw(g2d));
        repaint();
    }

    /**
     * Register a new {@link Drawable}
     * @param drawable to register
     */
    public void registerDrawable(Drawable drawable) {
        this.drawables.add(drawable);
    }
}
