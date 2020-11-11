package me.fox.ui.panels.ext;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.enums.ToolboxType;
import me.fox.services.DrawService;
import me.fox.ui.components.toolbox.ToolboxComponent;
import me.fox.ui.components.toolbox.ext.DefaultToolboxComponent;
import me.fox.ui.components.toolbox.ext.PaintedToolBoxComponent;
import me.fox.ui.panels.Toolbox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
public class DrawToolbox extends Toolbox {

    private ToolboxComponent lineComponent, circleComponent, rectangleComponent;

    private final Shape rectangle = new Rectangle(5, 5, 21, 21);
    private final Shape circle = new Ellipse2D.Double(5, 5, 21, 21);


    public DrawToolbox() {
        super(ToolboxType.VERTICAL);
    }

    @Override
    public void loadToolboxComponents() {
        this.lineComponent = new DefaultToolboxComponent(null, this::line, true, false);
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
                this.circleComponent.setStage(0);
                this.circleComponent.select(null);
            }
            if (drawService.isLine()) {
                this.lineComponent.setStage(0);
                this.lineComponent.select(null);
            }
            if (drawService.isRectangle()) {
                this.rectangleComponent.setStage(0);
                this.rectangleComponent.select(null);
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

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(drawService.getDrawColor());

        if (drawService.isCircle()) {
            if (drawService.isFillCircle()) {
                g2d.fill(this.circle);
                return;
            }
            g2d.draw(this.circle);
        }
    }

    private void drawRect(Graphics2D g2d) {
        DrawService drawService = Fireshot.getInstance().getDrawService();

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(drawService.getDrawColor());

        if (drawService.isRectangle()) {
            if (drawService.isFillRectangle()) {
                g2d.fill(this.rectangle);
                return;
            }
            g2d.draw(this.rectangle);
        }

    }
}
