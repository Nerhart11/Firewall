package base;

import java.awt.*;

/**
 * Abstract base class for all entities on the simulation grid.
 */
public abstract class NetworkNode {

    protected int row;
    protected int col;
    protected boolean isCorrupted;
    protected int maxHP;
    protected int currentHP;
    protected int repairThreshold;
    protected boolean beingRepaired;
    protected int repairProgress;

    /**
     * @param row grid row position
     * @param col grid column position
     * @param maxHP maximum health points before corruption
     * @param repairThreshold total repair progress needed to fully restore
     */
    public NetworkNode(int row, int col, int maxHP, int repairThreshold){
        this.row = row;
        this.col = col;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.repairThreshold = repairThreshold;
    }

    /** @return the color representing this node's current state */
    public abstract Color getColor();

    /** @return readable name of this node type */
    public abstract String getTypeName();

    /** @return single-character representation for text-based display */
    public abstract String toString();

    /** @return grid row position */
    public int getRow() { return row; }

    /** @return grid column position */
    public int getCol() { return col; }

    /**
     * Updates this node's position on the grid.
     *
     * @param row new row position
     * @param col new column position
     */
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** @return true if this node has been fully corrupted */
    public boolean isCorrupted() { return isCorrupted; }

    /** @return true if a RepairBot is currently repairing this node */
    public boolean isBeingRepaired() { return beingRepaired; }

    /**
     * Marks whether a RepairBot is currently repairing this node.
     *
     * @param status true if repair has started, false if repair ended or was interrupted
     */
    public void setBeingRepaired(boolean status) { this.beingRepaired = status; }

    /**
     * Changes between two colors based on remaining HP.
     *
     * @param baseColor color at full health
     * @param deadColor color at zero health
     * @return blended color that represents current health
     */
    protected Color getHealthColor(Color baseColor, Color deadColor) {
        int r = deadColor.getRed() + (baseColor.getRed() - deadColor.getRed()) * currentHP / maxHP;
        int g = deadColor.getGreen() + (baseColor.getGreen() - deadColor.getGreen()) * currentHP / maxHP;
        int b = deadColor.getBlue() + (baseColor.getBlue() - deadColor.getBlue()) * currentHP / maxHP;
        return new Color(r, g, b);
    }

    /**
     * Deals damage to this node, corrupting it if HP reaches zero.
     *
     * @param damage amount of HP to subtract
     */
    public void infect(int damage) {
        this.currentHP -= damage;
        if (this.currentHP <= 0) {
            this.currentHP = 0;
            this.isCorrupted = true;
            this.repairProgress = 0;
        }
    }

    /**
     * Progresses repair on this node by the given speed.
     *
     * @param repairSpeed amount of repair progress per tick
     * @return true if repair just completed, false if still in progress
     */
    public boolean repair(int repairSpeed) {
        this.repairProgress += repairSpeed;
        if (this.repairProgress >= this.repairThreshold) {
            this.isCorrupted = false;
            this.currentHP = maxHP;
            this.repairProgress = 0;
            return true;
        }
        return false;
    }
}