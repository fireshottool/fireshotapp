package me.fox.ui.components.toolbox.ext;

import me.fox.ui.components.toolbox.ToolboxComponent;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * @author (Ausgefuchster)
 * @version (~ 25.10.2020)
 */

public class DefaultToolboxComponent extends ToolboxComponent {

    public DefaultToolboxComponent(BufferedImage icon, ActionListener actionListener) {
        super(icon);
        this.setSize(32, 32);
        this.addActionListener(actionListener);
    }

    public DefaultToolboxComponent(ActionListener actionListener) {
        super(null);
        this.setSize(32, 32);
        this.addActionListener(actionListener);
    }

    public DefaultToolboxComponent(BufferedImage icon, ActionListener actionListener, boolean selectable, boolean doubleSelect) {
        super(icon, doubleSelect);
        this.setSize(32, 32);
        this.addActionListener(actionListener);
        if (selectable) {
            this.addActionListener(this::select);
        }
    }


    public DefaultToolboxComponent(ActionListener actionListener, boolean selectable, boolean doubleSelect) {
        super(null, doubleSelect);
        this.setSize(32, 32);
        this.addActionListener(actionListener);
        if (selectable) {
            this.addActionListener(this::select);
        }
    }
}
