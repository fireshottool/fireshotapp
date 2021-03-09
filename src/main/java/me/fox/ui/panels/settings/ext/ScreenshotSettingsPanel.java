package me.fox.ui.panels.settings.ext;

import lombok.Getter;
import me.fox.Fireshotapp;
import me.fox.config.Config;
import me.fox.config.FileConfig;
import me.fox.config.ScreenshotConfig;
import me.fox.services.JsonService;
import me.fox.ui.components.settings.ext.CheckBoxComponent;
import me.fox.ui.components.settings.ext.ColorComponent;
import me.fox.ui.components.settings.ext.ComboBoxComponent;
import me.fox.ui.components.settings.ext.LocationChooserComponent;
import me.fox.ui.panels.PanelManager;
import me.fox.ui.panels.settings.SettingsPanel;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */

@Getter
public class ScreenshotSettingsPanel extends SettingsPanel {

    private final LocationChooserComponent locationChooserComponent = new LocationChooserComponent(
            new Point(30, 10),
            "Image Location: "
    );

    private final ComboBoxComponent comboBoxComponent = new ComboBoxComponent(
            new Point(30, 90),
            "Image type ",
            "png", "jpg"
    );

    private final CheckBoxComponent uploadCheckBox = new CheckBoxComponent(
            new Point(30, 140),
            "Upload image",
            this::uploadCheckBoxActionPerformed
    );

    private final CheckBoxComponent saveCheckBox = new CheckBoxComponent(
            new Point(30, 190),
            "Save image",
            this::saveCheckBoxActionPerformed
    );

    private final CheckBoxComponent zoomCheckBox = new CheckBoxComponent(
            new Point(30, 240),
            "Show zoom",
            this::zoomCheckBoxActionPerformed
    );

    private final ColorComponent dimColorComponent, zoomCrossColorComponent, zoomGridColorComponent;

    public ScreenshotSettingsPanel(PanelManager panelManager) {
        super(panelManager);

        this.dimColorComponent = new ColorComponent(
                "Dim color",
                new Point(30, 290),
                this::dimColorChanged,
                panelManager.getSettingsFrame()
        );
        this.zoomCrossColorComponent = new ColorComponent(
                "Zoom Cross color",
                new Point(30, 340),
                this::zoomCrossColorChanged,
                panelManager.getSettingsFrame()
        );
        this.zoomGridColorComponent = new ColorComponent(
                "Zoom Raster color",
                new Point(30, 390),
                this::zoomGridColorChanged,
                panelManager.getSettingsFrame()
        );

        this.add(this.locationChooserComponent);
        this.add(this.comboBoxComponent);
        this.add(this.dimColorComponent);
        this.add(this.uploadCheckBox);
        this.add(this.saveCheckBox);
        this.add(this.zoomCheckBox);
        this.add(this.zoomCrossColorComponent);
        this.add(this.zoomGridColorComponent);
    }

    private void zoomCrossColorChanged(ActionEvent event) {
        this.zoomCrossColorComponent.getColorPickerDialog().setVisible(false);
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        Color color = this.zoomCrossColorComponent.getColor();
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        jsonService.getConfig().getScreenshotConfig().setZoomCrossColor(hex);
    }

    private void zoomGridColorChanged(ActionEvent event) {
        this.zoomGridColorComponent.getColorPickerDialog().setVisible(false);
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        Color color = this.zoomGridColorComponent.getColor();
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        jsonService.getConfig().getScreenshotConfig().setZoomGridColor(hex);
    }

    private void dimColorChanged(ActionEvent event) {
        this.dimColorComponent.getColorPickerDialog().setVisible(false);
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        Color color = this.dimColorComponent.getColor();
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        jsonService.getConfig().getScreenshotConfig().setDimColor(hex);
    }

    private void saveCheckBoxActionPerformed(ActionEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        jsonService.getConfig().getScreenshotConfig().setLocalSave(this.saveCheckBox.getCheckBox().isSelected());
    }

    private void uploadCheckBoxActionPerformed(ActionEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        jsonService.getConfig().getScreenshotConfig().setUpload(this.uploadCheckBox.getCheckBox().isSelected());
    }

    private void zoomCheckBoxActionPerformed(ActionEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        jsonService.getConfig().getScreenshotConfig().setZoom(this.zoomCheckBox.getCheckBox().isSelected());
    }

    @Override
    public void applyConfig(Config config) {
        ScreenshotConfig screenshotConfig = config.getScreenshotConfig();
        FileConfig fileConfig = config.getFileConfig();
        this.dimColorComponent.setColor(Color.decode(screenshotConfig.getDimColor()));
        this.zoomCrossColorComponent.setColor(Color.decode(screenshotConfig.getZoomCrossColor()));
        this.zoomGridColorComponent.setColor(Color.decode(screenshotConfig.getZoomGridColor()));
        this.comboBoxComponent.getComboBox().setSelectedItem(fileConfig.getImageType());
        this.locationChooserComponent.setLocationText(fileConfig.getImageLocation());
        this.uploadCheckBox.getCheckBox().setSelected(screenshotConfig.isUpload());
        this.saveCheckBox.getCheckBox().setSelected(screenshotConfig.isLocalSave());
        this.zoomCheckBox.getCheckBox().setSelected(screenshotConfig.isZoom());
    }
}
