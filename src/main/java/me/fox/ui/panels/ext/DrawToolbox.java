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
    private ToolboxComponent line, circle;

    public DrawToolbox() {
        super(ToolboxType.VERTICAL);
    }

    @Override
    public void loadToolboxComponents() {
        this.line = new DefaultToolboxComponent(null, this::line, true);
        this.addComponent(this.line);

        this.circle = new DefaultToolboxComponent(null, this::circle, true);
        this.addComponent(this.circle);
    }

    private void line(ActionEvent event) {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawService.setLine(!drawService.isLine());
    }

    private void circle(ActionEvent event) {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawService.setCircle(!drawService.isCircle());
    }
}
