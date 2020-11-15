package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Data
public class DrawConfig {

    private int defaultThickness = 1;
    private int thicknessIncrease = 5;
    private int thicknessDecrease = 5;
    private String color = "#ff0000";
}
