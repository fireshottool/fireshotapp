package me.fox.ui.panels.toolbox.ext;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.enums.ToolboxType;
import me.fox.services.DrawService;
import me.fox.services.ScreenService;
import me.fox.ui.components.toolbox.ToolboxComponent;
import me.fox.ui.components.toolbox.ext.DefaultToolboxComponent;
import me.fox.ui.panels.toolbox.Toolbox;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
public class ScreenshotToolbox extends Toolbox {

    private ToolboxComponent drawComponent,
            confirmComponent,
            googleSearchComponent,
            textRecognition,
            cancel;

    public ScreenshotToolbox() {
        super(ToolboxType.HORIZONTAL);
    }

    @Override
    public void loadToolboxComponents() {
        this.googleSearchComponent = new DefaultToolboxComponent(null, this::googleSearch);
        this.addComponent(this.googleSearchComponent);
        this.googleSearchComponent.setToolTipText("Search the image on google");

        this.textRecognition = new DefaultToolboxComponent(null, this::googleSearch);
        this.addComponent(this.textRecognition);
        this.textRecognition.setToolTipText("Get the text on the image (text recognition)");

        this.cancel = new DefaultToolboxComponent(null, this::cancel);
        this.addComponent(this.cancel);
        this.cancel.setToolTipText("Cancel the screenshot");

        this.drawComponent = new DefaultToolboxComponent(null, this::switchDraw, true, false);
        this.addComponent(this.drawComponent);
        this.drawComponent.setToolTipText("Switch to draw");

        this.confirmComponent = new DefaultToolboxComponent(null, this::confirmScreenshot);
        this.addComponent(this.confirmComponent);
        this.confirmComponent.setToolTipText("Confirm Screenshot");
    }

    @Override
    public void reset() {
        DrawService drawService = Fireshot.getInstance().getDrawService();
        if (drawService.isDraw()) {
            this.drawComponent.unselect();
        }
    }

    private void confirmScreenshot(ActionEvent event) {
        Fireshot.getInstance().getScreenService().hideAndConfirm();
    }

    private void switchDraw(ActionEvent event) {
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
        Fireshot.getInstance().getScreenService().resetAndHide();
    }

    private void googleSearch(ActionEvent event) {

    }

    @Override
    public void applyResources(List<File> files) {
        files.forEach(var -> {
            try {
                if (var.getName().equals("save.png")) {
                    this.confirmComponent.setIcon(new ImageIcon(
                            ImageIO.read(var).getScaledInstance(26, 26, Image.SCALE_SMOOTH)
                    ));
                } else if (var.getName().equals("pencil.png")) {
                    this.drawComponent.setIcon(new ImageIcon(
                            ImageIO.read(var).getScaledInstance(26, 26, Image.SCALE_SMOOTH)
                    ));
                } else if (var.getName().equals("toolboxbg.png")) {
                    this.setBackgroundImage(ImageIO.read(var));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
