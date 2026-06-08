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
    
    /**
     * Steps the repair bot's movement randomly within the grid boundaries.
     * @param maxRows the maximum row boundary of the simulation grid
     * @param maxCols the maximum column boundary of the simulation grid
     */
    public void step(int maxRows, int maxCols) 
    {
        java.util.Random rand = new java.util.Random();
        
        // Generate a random shift: -1 (up/left), 0 (stay), or 1 (down/right)
        int deltaRow = rand.nextInt(3) - 1;
        int deltaCol = rand.nextInt(3) - 1;
        
        // Calculate new potential coordinates
        int newRow = this.getRow() + deltaRow;
        int newCol = this.getCol() + deltaCol;
        
        // Clamp the values so the bot never walks off the edge of the grid layout map
        if (newRow >= 0 && newRow < maxRows) 
        {
            this.row = newRow;
        }
        if (newCol >= 0 && newCol < maxCols) 
        {
            this.col = newCol;
        }
    }
}
