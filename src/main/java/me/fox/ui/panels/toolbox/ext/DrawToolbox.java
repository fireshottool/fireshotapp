package me.fox.ui.panels.toolbox.ext;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.enums.ToolboxType;
import me.fox.services.DrawService;
import me.fox.ui.components.toolbox.ToolboxComponent;
import me.fox.ui.components.toolbox.ext.PaintedToolBoxComponent;
import me.fox.ui.panels.toolbox.Toolbox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
public class DrawToolbox extends Toolbox {

    private final Stroke stroke = new BasicStroke(3);

    private final Shape rectangle = new Rectangle(8, 8, 16, 16);
    private final Shape circle = new Ellipse2D.Double(8, 8, 16, 16);

    private ToolboxComponent lineComponent, circleComponent, rectangleComponent;

    public DrawToolbox() {
        super(ToolboxType.VERTICAL);
    }

    @Override
    public void loadToolboxComponents() {
        this.lineComponent = new PaintedToolBoxComponent(
                null,
                this::line,
                true,
                false,
                this::drawLine);
        this.addComponent(this.lineComponent);

        this.circleComponent = new PaintedToolBoxComponent(
                null,
                this::circle,
                true,
                true,
                this::drawCircle);
        this.addComponent(this.circleComponent);

        this.rectangleComponent = new PaintedToolBoxComponent(
                null,
                this::rectangle,
                true,
                true,
                this::drawRect);
        this.addComponent(this.rectangleComponent);
    }

    @Override
    public void reset() {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        if (drawService.isDraw()) {
            if (drawService.isCircle()) {
                this.circleComponent.unselect();
            }
            if (drawService.isLine()) {
                this.lineComponent.unselect();
            }
            if (drawService.isRectangle()) {
                this.rectangleComponent.unselect();
            }
        }
    }

    private void line(ActionEvent event) {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawService.setRectangle(false);
        this.rectangleComponent.unselect();
        drawService.setCircle(false);
        this.circleComponent.unselect();
        drawService.setLine(!drawService.isLine());
    }

    private void circle(ActionEvent event) {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawService.setRectangle(false);
        this.rectangleComponent.unselect();
        drawService.setLine(false);
        this.lineComponent.unselect();
        if (drawService.isCircle() && drawService.isFillCircle()) {
            drawService.setFillCircle(false);
            return;
        }
        drawService.setCircle(!drawService.isCircle());
        drawService.setFillCircle(true);
    }

    private void rectangle(ActionEvent event) {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawService.setCircle(false);
        this.circleComponent.unselect();
        drawService.setLine(false);
        this.lineComponent.unselect();
        if (drawService.isRectangle() && drawService.isFillRectangle()) {
            drawService.setFillRectangle(false);
            return;
        }
        drawService.setRectangle(!drawService.isRectangle());
        drawService.setFillRectangle(true);
    }

    private void drawCircle(Graphics2D g2d) {
        DrawService drawService = Fireshot.getInstance().getDrawService();

        g2d.setStroke(this.stroke);
        g2d.setColor(drawService.getDrawColor());

        if (drawService.isCircle()) {
            if (drawService.isFillCircle()) {
                g2d.fill(this.circle);
                return;
            }
            g2d.draw(this.circle);
            return;
        }
        g2d.fill(this.circle);
    }

    private void drawRect(Graphics2D g2d) {
        DrawService drawService = Fireshot.getInstance().getDrawService();

        g2d.setStroke(this.stroke);
        g2d.setColor(drawService.getDrawColor());

        if (drawService.isRectangle()) {
            if (drawService.isFillRectangle()) {
                g2d.fill(this.rectangle);
                return;
            }
            g2d.draw(this.rectangle);
            return;
        }
        g2d.fill(this.rectangle);
    }

    private void drawLine(Graphics2D g2d) {
        DrawService drawService = Fireshot.getInstance().getDrawService();

        g2d.setStroke(this.stroke);
        g2d.setColor(drawService.getDrawColor());

        g2d.drawLine(8, 8, 22, 22);
    }
}
