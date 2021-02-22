package me.fox.listeners.mouse;

import lombok.AllArgsConstructor;
import me.fox.ui.components.settings.FoldEdgeLabel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.11.2020)
 */
@AllArgsConstructor
public class FoldEdgeLabelListener extends MouseAdapter {

    private final FoldEdgeLabel foldEdgeLabel;

    @Override
    public void mouseClicked(MouseEvent event) {
        if (foldEdgeLabel.makeEdge().contains(event.getPoint())) {
            this.foldEdgeLabel.switchColorDisplay();
        }
    }
}
