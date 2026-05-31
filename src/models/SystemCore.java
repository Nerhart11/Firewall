package models;

import base.DataCell;

import java.awt.*;

/**
 * Critical target node. corruption triggers increased malware aggression.
 */
public class SystemCore extends DataCell {

    private static final Color BASE_COLOR = new Color(255, 215, 0);
    private static final Color DEAD_COLOR = new Color(65, 50, 0);

    public SystemCore(int row, int col) {
        super(row, col, 500, 300);
    }

    @Override
    public Color getColor() {
        return getHealthColor(BASE_COLOR, DEAD_COLOR);
    }

    @Override
    public String getTypeName() {
        return "System Core";
    }

    @Override
    public String toString() {
        return "C";
    }
}
