package me.fox.ui.panels.toolbox.ext;

import lombok.Getter;
import me.fox.Fireshotapp;
import me.fox.enums.ToolboxType;
import me.fox.services.DrawService;
import me.fox.ui.components.toolbox.ToolboxComponent;
import me.fox.ui.components.toolbox.ext.DefaultToolboxComponent;
import me.fox.ui.components.toolbox.ext.PaintedToolBoxComponent;
import me.fox.ui.frames.ColorPickerDialog;
import me.fox.ui.frames.ScreenshotFrame;
import me.fox.ui.panels.toolbox.Toolbox;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
public class DrawToolbox extends Toolbox {

    private final Stroke stroke = new BasicStroke(3);

    private final Shape rectangle = new Rectangle(8, 8, 16, 16);
    private final Shape circle = new Ellipse2D.Double(8, 8, 16, 16);

    private final ColorPickerDialog colorPickerDialog;

    private Color oldColor;

    private ToolboxComponent lineComponent,
            colorPickerComponent,
            circleComponent,
            rectangleComponent,
            increaseComponent,
            decreaseComponent,
            undoComponent,
            redoComponent;

    public DrawToolbox(ScreenshotFrame screenshotFrame) {
        super(ToolboxType.VERTICAL);
        this.colorPickerDialog = new ColorPickerDialog(screenshotFrame, this::colorChanged);
        this.colorPickerDialog.getCancel().addActionListener(this::colorPickerCancel);
        this.colorPickerDialog.getOk().addActionListener(this::colorPickerConfirm);
    }

    private void line(ActionEvent event) {
        DrawService drawService = Fireshotapp.getInstance().use(DrawService.class);
        drawService.setRectangle(false);
        this.rectangleComponent.unselect();
        drawService.setCircle(false);
        this.circleComponent.unselect();
        drawService.setLine(!drawService.isLine());
    }

    private void circle(ActionEvent event) {
        DrawService drawService = Fireshotapp.getInstance().use(DrawService.class);
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
        DrawService drawService = Fireshotapp.getInstance().use(DrawService.class);
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
        DrawService drawService = Fireshotapp.getInstance().use(DrawService.class);

        g2d.setStroke(this.stroke);
        g2d.setColor(drawService.getDrawColor());

        if (drawService.isCircle()) {
            if (drawService.isFillCircle()) {
                g2d.fill(this.circle);
                return;
            }
            g2d.draw(this.circle);
            return;
        }
        g2d.fill(this.circle);
    }

    private void drawRect(Graphics2D g2d) {
        DrawService drawService = Fireshotapp.getInstance().use(DrawService.class);

        g2d.setStroke(this.stroke);
        g2d.setColor(drawService.getDrawColor());

        if (drawService.isRectangle()) {
            if (drawService.isFillRectangle()) {
                g2d.fill(this.rectangle);
                return;
            }
            g2d.draw(this.rectangle);
            return;
        }
        g2d.fill(this.rectangle);
    }

    private void drawLine(Graphics2D g2d) {
        DrawService drawService = Fireshotapp.getInstance().use(DrawService.class);

        g2d.setStroke(this.stroke);
        g2d.setColor(drawService.getDrawColor());

        g2d.drawLine(8, 8, 22, 22);
    }

    private void undo(ActionEvent event) {
        Fireshotapp.getInstance().use(DrawService.class).undoDrawing();
    }

    private void redo(ActionEvent event) {
        Fireshotapp.getInstance().use(DrawService.class).redoDrawing();
    }

    private void increase(ActionEvent event) {
        Fireshotapp.getInstance().use(DrawService.class).increaseThickness();
    }

    private void decrease(ActionEvent event) {
        Fireshotapp.getInstance().use(DrawService.class).decreaseThickness();
    }

    private void colorPicker(ActionEvent event) {
        this.colorPickerDialog.setVisible(true);
    }

    private void colorChanged(ChangeEvent event) {
        DrawService drawService = Fireshotapp.getInstance().use(DrawService.class);
        if (this.oldColor == null) {
            this.oldColor = drawService.getDrawColor();
        }
        drawService.setDrawColor(this.colorPickerDialog.getColorPicker().getColor());
    }

    private void colorPickerCancel(ActionEvent event) {
        Fireshotapp.getInstance().use(DrawService.class).setDrawColor(this.oldColor);
        this.colorPickerDialog.setVisible(false);
        this.oldColor = null;
    }

    private void colorPickerConfirm(ActionEvent event) {
        this.colorPickerDialog.setVisible(false);
        this.oldColor = null;
    }

    @Override
    public void loadToolboxComponents() {
        this.colorPickerComponent = new DefaultToolboxComponent(this::colorPicker);
        this.addComponent(this.colorPickerComponent);
        this.colorPickerComponent.setToolTipText("Pick color");

        this.lineComponent = new PaintedToolBoxComponent(
                this::line,
                true,
                false,
                this::drawLine);
        this.addComponent(this.lineComponent);
        this.lineComponent.setToolTipText("Draw Line");

        this.circleComponent = new PaintedToolBoxComponent(
                this::circle,
                true,
                true,
                this::drawCircle);
        this.addComponent(this.circleComponent);
        this.circleComponent.setToolTipText("Draw Circle");

        this.rectangleComponent = new PaintedToolBoxComponent(
                this::rectangle,
                true,
                true,
                this::drawRect);
        this.addComponent(this.rectangleComponent);
        this.rectangleComponent.setToolTipText("Draw Rectangle");

        this.increaseComponent = new DefaultToolboxComponent(this::increase);
        this.addComponent(this.increaseComponent);
        this.increaseComponent.setToolTipText("Increase pencil width");
        this.decreaseComponent = new DefaultToolboxComponent(this::decrease);
        this.addComponent(this.decreaseComponent);
        this.decreaseComponent.setToolTipText("Decrease pencil width");
        this.undoComponent = new DefaultToolboxComponent(this::undo);
        this.addComponent(this.undoComponent);
        this.undoComponent.setToolTipText("Undo last drawing");
        this.redoComponent = new DefaultToolboxComponent(this::redo);
        this.addComponent(this.redoComponent);
        this.redoComponent.setToolTipText("Redo last undone drawing");
    }

    @Override
    public void resetResources() {
        this.redoComponent.setIcon(null);
        this.undoComponent.setIcon(null);
        this.increaseComponent.setIcon(null);
        this.decreaseComponent.setIcon(null);
    }

    @Override
    public void reset() {
        if (this.colorPickerDialog.isVisible())
            this.colorPickerDialog.setVisible(false);

        DrawService drawService = Fireshotapp.getInstance().use(DrawService.class);
        if (drawService.isDraw()) {
            if (drawService.isCircle()) {
                this.circleComponent.unselect();
            }
            if (drawService.isLine()) {
                this.lineComponent.unselect();
            }
            if (drawService.isRectangle()) {
                this.rectangleComponent.unselect();
            }
        }
    }

    @Override
    public void applyResources(List<File> files) {
        super.applyResources(files);
        this.resetResources();

        files.stream().filter(var -> !var.getName().equals("toolboxbg.png")).forEach(var -> {
            try {
                if (var.getName().equals("increase.png")) {
                    this.increaseComponent.setIcon(new ImageIcon(
                            ImageIO.read(var).getScaledInstance(26, 26, Image.SCALE_SMOOTH)
                    ));
                } else if (var.getName().equals("decrease.png")) {
                    this.decreaseComponent.setIcon(new ImageIcon(
                            ImageIO.read(var).getScaledInstance(26, 26, Image.SCALE_SMOOTH)
                    ));
                } else if (var.getName().equals("undo.png")) {
                    this.undoComponent.setIcon(new ImageIcon(
                            ImageIO.read(var).getScaledInstance(26, 26, Image.SCALE_SMOOTH)
                    ));
                } else if (var.getName().equals("redo.png")) {
                    this.redoComponent.setIcon(new ImageIcon(
                            ImageIO.read(var).getScaledInstance(26, 26, Image.SCALE_SMOOTH)
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
