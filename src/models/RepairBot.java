package models;

import base.ActiveAgent;
import base.NetworkNode;

import java.awt.*;

/**
 * Specialized agent that scans for corrupted nodes and restores them.
 */
public class RepairBot extends ActiveAgent {

    private static final Color BASE_COLOR = new Color(170, 221, 255);
    private static final Color DEAD_COLOR = new Color(35, 45, 55);

    private int repairPower;
    private NetworkNode currentTarget;

    /**
     * @param row grid row position
     * @param col grid column position
     * @param repairPower amount of repair progress applied per tick
     * @param scanRange how far this bot can detect corrupted nodes
     */
    public RepairBot(int row, int col, int repairPower, int scanRange) {
        super(row, col, 100, 5, scanRange);
        this.repairPower = repairPower;
        this.currentTarget = null;
    }

    public int getRepairPower() { return repairPower; }
    public void setRepairPower(int repairPower) { this.repairPower = repairPower; }

    @Override
    public Color getColor() {
        return getHealthColor(BASE_COLOR, DEAD_COLOR);
    }

    @Override
    public String getTypeName() {
        return "Repair Bot";
    }

    @Override
    public String toString() {
        return "R";
    }
}
