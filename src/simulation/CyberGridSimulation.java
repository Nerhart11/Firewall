package simulation;

import base.NetworkNode;
import models.StandardFile;

public class CyberGridSimulation 
{
    protected NetworkNode[][] grid;
    protected int rows;
    protected int columns;

    public CyberGridSimulation(int rows, int columns)
    {
        this.rows = rows;
        this.columns = columns;
        grid = new NetworkNode[rows][columns];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                grid[row][column] = new StandardFile(row, column);
            }
        }
    }

    public NetworkNode[][] getGrid(){ return grid; }

    public void update()
    {
        
    }
}
