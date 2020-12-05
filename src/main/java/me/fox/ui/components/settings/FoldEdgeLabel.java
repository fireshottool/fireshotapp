package me.fox.ui.components.settings;


import lombok.Getter;
import me.fox.listeners.mouse.FoldEdgeLabelListener;

import javax.swing.*;
import java.awt.*;

public class FoldEdgeLabel extends JLabel {

    private final Switcher<String, String> switcher = new Switcher<>("hex", "rgb");

    @Getter
    private Color color;

    public FoldEdgeLabel(String text) {
        super(text, SwingConstants.CENTER);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        this.addMouseListener(new FoldEdgeLabelListener(this));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillPolygon(this.makeEdge());

        g.setColor(this.color);
        g.drawRoundRect(15, 15,
                this.getWidth() - 30, this.getHeight() - 30, 5, 5);
        repaint();
    }

    public Polygon makeEdge() {
        int bottomRightX = this.getWidth();
        int bottomRightY = this.getHeight();

        return new Polygon(
                new int[]{bottomRightX, bottomRightX, bottomRightX - 14},
                new int[]{bottomRightY, bottomRightY - 14, bottomRightY},
                3
        );
    }

    public void setColor(Color color) {
        this.color = color;
        this.setText(this.currentColorRepr());
    }

    public void setColor(String colorCode) {
        this.setColor(Color.decode(colorCode));
    }

    private String currentColorRepr() {
        if (this.switcher.isLeft()) {
            return this.colorToHexString();
        }

        return this.colorToRGBString();
    }

    public void switchColorDisplay() {
        System.out.println("test");
        this.switcher.switchSides();
        this.setText(this.currentColorRepr());
    }

    private String colorToHexString() {
        return String.format("#%02x%02x%02x", this.color.getRed(), this.color.getGreen(), this.color.getBlue());
    }

    private String colorToRGBString() {
        return String.format("(%d,%d,%d)", this.color.getRed(), this.color.getGreen(), this.color.getBlue());
    }

}
