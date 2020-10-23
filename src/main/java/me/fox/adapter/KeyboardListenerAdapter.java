package me.fox.adapter;

import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

public abstract class KeyboardListenerAdapter implements GlobalKeyListener, KeyListener {

    @Override
    public void keyPressed(GlobalKeyEvent event) {
    }

    @Override
    public void keyReleased(GlobalKeyEvent event) {
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyPressed(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }
}
