package models;

import base.ActiveAgent;

import java.awt.*;

/**
 * Defensive agent that scans the grid for malware and attacks it.
 */
public class AntivirusSentinel extends ActiveAgent 
{

    private static final Color BASE_COLOR = new Color(0, 170, 255);
    private static final Color DEAD_COLOR = new Color(0, 35, 55);

    public AntivirusSentinel(int row, int col, int scanRange) 
    {
        super(row, col, 150, 120, scanRange);
    }

    /**
     * Executes security patrol sweeps on every clock tick.
     * @param maxRows
     * @param maxCols
     */
    public void step(int maxRows, int maxCols)
    {
        java.util.Random rand = new java.util.Random();
        
        int dRow = rand.nextInt(3) - 1;
        int dCol = rand.nextInt(3) - 1;
        
        int newRow = getRow() + dRow;
        int newCol = getCol() + dCol;
        
        if (newRow >= 0 && newRow < maxRows && newCol >= 0 && newCol < maxCols)
        {
            this.row = newRow;
            this.col = newCol;
        }
    }
    
    @Override
    public Color getColor() 
    {
        return getHealthColor(BASE_COLOR, DEAD_COLOR);
    }

    @Override
    public String getTypeName() 
    {
        return "Antivirus Sentinel";
    }

    @Override
    public String toString() 
    {
        return "S";
    }


}
