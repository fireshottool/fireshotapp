package me.fox.ui.panels.ext;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.enums.ToolboxType;
import me.fox.services.DrawService;
import me.fox.services.ScreenService;
import me.fox.ui.components.toolbox.ToolboxComponent;
import me.fox.ui.components.toolbox.ext.DefaultToolboxComponent;
import me.fox.ui.panels.Toolbox;

import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
public class ScreenshotToolbox extends Toolbox {
    private ToolboxComponent draw;

    public ScreenshotToolbox() {
        super(ToolboxType.HORIZONTAL);
    }

    @Override
    public void loadToolboxComponents() {
        ToolboxComponent confirmScreenshot = new DefaultToolboxComponent(null, this::confirmScreenshot);
        this.addComponent(confirmScreenshot);

        try {
            draw = new DefaultToolboxComponent(ImageIO.read(new File("C:\\Users\\niki\\Pictures\\fireshot\\1.png")), this::switchDraw, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.addComponent(draw);

        ToolboxComponent cancel = new DefaultToolboxComponent(null, this::cancel);
        this.addComponent(cancel);
    }

    private void confirmScreenshot(ActionEvent event) {
        System.out.println("Confirm");
        Fireshot.getInstance().getScreenService().hideAndConfirm();
    }

    private void switchDraw(ActionEvent event) {
        System.out.println("Draw");
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawService.setDraw(!drawService.isDraw());
        ScreenService screenService = Fireshot.getInstance().getScreenService();
        if (screenService.getDrawToolbox().isVisible()) {
            screenService.getDrawToolbox().hideSelf();
        } else {
            screenService.getDrawToolbox().showSelf();
        }
    }

    private void cancel(ActionEvent event) {
        System.out.println("Cancel");
        Fireshot.getInstance().getScreenService().resetAndHide();
    }
}
