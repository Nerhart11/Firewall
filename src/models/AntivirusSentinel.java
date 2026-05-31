package models;

import base.ActiveAgent;

import java.awt.*;

/**
 * Defensive agent that scans the grid for malware and attacks it.
 */
public class AntivirusSentinel extends ActiveAgent {

    private static final Color BASE_COLOR = new Color(0, 170, 255);
    private static final Color DEAD_COLOR = new Color(0, 35, 55);

    public AntivirusSentinel(int row, int col, int scanRange) {
        super(row, col, 150, 120, scanRange);
    }

    @Override
    public Color getColor() {
        return getHealthColor(BASE_COLOR, DEAD_COLOR);
    }

    @Override
    public String getTypeName() {
        return "Antivirus Sentinel";
    }

    @Override
    public String toString() {
        return "S";
    }


}
