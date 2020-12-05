package me.fox.ui.panels.settings.ext;

import lombok.Getter;
import me.fox.Fireshot;
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
            new Point(30, 170),
            "Upload image",
            this::uploadCheckBoxActionPerformed
    );

    private final CheckBoxComponent saveCheckBox = new CheckBoxComponent(
            new Point(30, 250),
            "Save image",
            this::saveCheckBoxActionPerformed
    );

    private final ColorComponent colorComponent;

    public ScreenshotSettingsPanel(PanelManager panelManager) {
        super(panelManager);

        this.colorComponent = new ColorComponent(
                "Dim color",
                new Point(30, 330),
                this::colorChanged,
                panelManager.getSettingsFrame()
        );

        this.add(this.locationChooserComponent);
        this.add(this.comboBoxComponent);
        this.add(this.colorComponent);
        this.add(this.uploadCheckBox);
        this.add(this.saveCheckBox);
    }

    private void colorChanged(ActionEvent event) {
        this.colorComponent.getColorPickerDialog().setVisible(false);
        JsonService jsonService = Fireshot.getInstance().getJsonService();
        Color color = this.colorComponent.getColor();
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        jsonService.getConfig().getScreenshotConfig().setDimColor(hex);
    }

    private void saveCheckBoxActionPerformed(ActionEvent event) {
        JsonService jsonService = Fireshot.getInstance().getJsonService();
        jsonService.getConfig().getScreenshotConfig().setLocalSave(this.uploadCheckBox.getCheckBox().isSelected());
    }

    private void uploadCheckBoxActionPerformed(ActionEvent event) {
        JsonService jsonService = Fireshot.getInstance().getJsonService();
        jsonService.getConfig().getScreenshotConfig().setUpload(this.uploadCheckBox.getCheckBox().isSelected());
    }

    @Override
    public void applyConfig(Config config) {
        ScreenshotConfig screenshotConfig = config.getScreenshotConfig();
        FileConfig fileConfig = config.getFileConfig();
        this.colorComponent.setColor(Color.decode(screenshotConfig.getDimColor()));
        this.comboBoxComponent.getComboBox().setSelectedItem(fileConfig.getImageType());
        this.locationChooserComponent.setLocationText(fileConfig.getImageLocation());
        this.uploadCheckBox.getCheckBox().setSelected(screenshotConfig.isUpload());
        this.saveCheckBox.getCheckBox().setSelected(screenshotConfig.isLocalSave());
    }
}
