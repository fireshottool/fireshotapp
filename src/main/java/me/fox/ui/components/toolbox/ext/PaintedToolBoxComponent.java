package me.fox.ui.components.toolbox.ext;

import me.fox.ui.components.draw.Drawable;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author (Ausgefuchster)
 * @version (~ 10.11.2020)
 */

public class PaintedToolBoxComponent extends DefaultToolboxComponent {

    private final Drawable drawable;

    public PaintedToolBoxComponent(ActionListener actionListener, Drawable drawable) {
        super(null, actionListener);
        this.drawable = drawable;
    }

    public PaintedToolBoxComponent(ActionListener actionListener,
                                   boolean selectable, boolean doubleSelect, Drawable drawable) {
        super(null, actionListener, selectable, doubleSelect);
        this.drawable = drawable;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawable.draw((Graphics2D) g);
    }
}
