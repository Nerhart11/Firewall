package models;

import base.DataCell;

import java.awt.*;

/**
 * Highly secure data node that has higher HP and is slower to repair.
 */
public class EncryptedVault extends DataCell {

    private static final Color BASE_COLOR = new Color(155, 0, 245);
    private static final Color DEAD_COLOR = new Color(40, 0, 65);

    public EncryptedVault(int row, int col) {
        super(row, col, 250, 150);
    }

    @Override
    public Color getColor() {
        return getHealthColor(BASE_COLOR, DEAD_COLOR);
    }

    @Override
    public String getTypeName() {
        return "Encrypted Vault";
    }

    @Override
    public String toString() {
        return "V";
    }
}
