package models;

import base.DataCell;

import java.awt.*;

/**
 * Basic data node and primary fuel for malware spread. Low HP, nothing special about it.
 */
public class StandardFile extends DataCell {

    private static final Color BASE_COLOR = new Color(0, 255, 204);
    private static final Color DEAD_COLOR = new Color(26, 10, 10);

    public StandardFile(int row, int col) {
        super(row, col, 100, 100);
    }

    @Override
    public Color getColor() {
        return getHealthColor(BASE_COLOR, DEAD_COLOR);
    }

    @Override
    public String getTypeName() {
        return "Standard File";
    }

    @Override
    public String toString() {
        return "F";
    }

}
