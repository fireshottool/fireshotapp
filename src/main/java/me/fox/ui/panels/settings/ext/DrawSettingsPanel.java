package me.fox.ui.panels.settings.ext;

import lombok.Getter;
import me.fox.Fireshotapp;
import me.fox.config.Config;
import me.fox.config.DrawConfig;
import me.fox.services.JsonService;
import me.fox.ui.components.settings.ext.ColorComponent;
import me.fox.ui.components.settings.ext.SpinnerNumberComponent;
import me.fox.ui.panels.PanelManager;
import me.fox.ui.panels.settings.SettingsPanel;

import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 18.11.2020)
 */

@Getter
public class DrawSettingsPanel extends SettingsPanel {

    private final SpinnerNumberComponent defaultThicknessComponent = new SpinnerNumberComponent(
            "Pencil thickness",
            new Point(30, 20),
            this::defaultThicknessChanged,
            5,
            1
    );

    private final SpinnerNumberComponent thicknessIncreaseComponent = new SpinnerNumberComponent(
            "Increase thickness by",
            new Point(30, 100),
            this::thicknessIncreased,
            5,
            1
    );

    private final SpinnerNumberComponent thicknessDecreaseComponent = new SpinnerNumberComponent(
            "Decrease thickness by",
            new Point(30, 180),
            this::thicknessDecreased,
            5,
            1
    );

    private final ColorComponent colorComponent;


    public DrawSettingsPanel(PanelManager panelManager) {
        super(panelManager);

        this.colorComponent = new ColorComponent(
                "Draw color",
                new Point(30, 260),
                this::colorChanged,
                panelManager.getSettingsFrame()
        );
        this.setVisible(true);
        this.add(defaultThicknessComponent);
        this.add(thicknessIncreaseComponent);
        this.add(thicknessDecreaseComponent);
        this.add(colorComponent);
    }

    private void defaultThicknessChanged(ChangeEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        int value = this.defaultThicknessComponent.getValue();
        jsonService.getConfig().getDrawConfig().setDefaultThickness(value);
    }

    private void thicknessIncreased(ChangeEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        int value = this.thicknessIncreaseComponent.getValue();
        jsonService.getConfig().getDrawConfig().setThicknessIncrease(value);
    }

    private void thicknessDecreased(ChangeEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        int value = this.thicknessDecreaseComponent.getValue();
        jsonService.getConfig().getDrawConfig().setThicknessDecrease(value);
    }

    private void colorChanged(ActionEvent event) {
        this.colorComponent.getColorPickerDialog().setVisible(false);
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        Color color = this.colorComponent.getColor();
        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        jsonService.getConfig().getDrawConfig().setColor(hex);
    }

    @Override
    public void applyConfig(Config config) {
        DrawConfig drawConfig = config.getDrawConfig();
        this.defaultThicknessComponent.setValue(drawConfig.getDefaultThickness());
        this.thicknessIncreaseComponent.setValue(drawConfig.getThicknessIncrease());
        this.thicknessDecreaseComponent.setValue(drawConfig.getThicknessDecrease());
        this.colorComponent.setColor(Color.decode(drawConfig.getColor()));
    }
}
