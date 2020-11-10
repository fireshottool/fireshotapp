package me.fox.ui.panels.ext;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.enums.ToolboxType;
import me.fox.services.DrawService;
import me.fox.ui.components.toolbox.ToolboxComponent;
import me.fox.ui.components.toolbox.ext.DefaultToolboxComponent;
import me.fox.ui.panels.Toolbox;

import java.awt.event.ActionEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
public class DrawToolbox extends Toolbox {
    private ToolboxComponent line, circle, rectangle;

    public DrawToolbox() {
        super(ToolboxType.VERTICAL);
    }

    @Override
    public void loadToolboxComponents() {
        this.line = new DefaultToolboxComponent(null, this::line, true, false);
        this.addComponent(this.line);

        this.circle = new DefaultToolboxComponent(null, this::circle, true, true);
        this.addComponent(this.circle);

        this.rectangle = new DefaultToolboxComponent(null, this::rectangle, true, true);
        this.addComponent(this.rectangle);
    }

    @Override
    public void reset() {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        if (drawService.isDraw()) {
            if (drawService.isCircle()) {
                this.circle.setStage(0);
                this.circle.select(null);
            }
            if (drawService.isLine()) {
                this.line.setStage(0);
                this.line.select(null);
            }
            if (drawService.isRectangle()) {
                this.rectangle.setStage(0);
                this.rectangle.select(null);
            }
        }
    }

    private void line(ActionEvent event) {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawService.setRectangle(false);
        this.rectangle.unselect();
        drawService.setCircle(false);
        this.circle.unselect();
        drawService.setLine(!drawService.isLine());
    }

    private void circle(ActionEvent event) {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawService.setRectangle(false);
        this.rectangle.unselect();
        drawService.setLine(false);
        this.line.unselect();
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
        this.circle.unselect();
        drawService.setLine(false);
        this.line.unselect();
        if (drawService.isRectangle() && drawService.isFillRectangle()) {
            drawService.setFillRectangle(false);
            return;
        }
        drawService.setRectangle(!drawService.isRectangle());
        drawService.setFillRectangle(true);
    }
}
