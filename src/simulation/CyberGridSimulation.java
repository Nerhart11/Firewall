package simulation;

import base.ActiveAgent;
import base.NetworkNode;
import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages the simulation grid and agent layer.
 */
public class CyberGridSimulation 
{
    private NetworkNode[][] grid;
    private List<ActiveAgent> agents;
    private final int rows;
    private final int cols;
    private final Random random = new Random();

    private int numSentinels = 3;
    private int numRepairBots = 2;
    private int numMalware = 4;
    private int numVaults = 12;
    private int defaultScanRange = 3;

    public CyberGridSimulation(int rows, int cols) 
    {
        this.rows = rows;
        this.cols = cols;
        this.grid = new NetworkNode[rows][cols];
        this.agents = new ArrayList<>();
        
        initializeGrid();
    }

    private void initializeGrid() {
        // Step 1: Fill everything with StandardFiles
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col] = new StandardFile(row, col);
            }
        }

        // Step 2: Place 2x2 SystemCore at center
        int centerRow = rows / 2 - 1;
        int centerCol = cols / 2 - 1;
        for (int row = centerRow; row <= centerRow + 1; row++) 
        {
            for (int col = centerCol; col <= centerCol + 1; col++) 
            {
                grid[row][col] = new SystemCore(row, col);
            }
        }

        // Step 3: Place EncryptedVaults in a ring around the core
        for (int i = 0; i < numVaults; i++) 
        {
            int row, col;
            do 
            {
                row = centerRow - 2 + random.nextInt(5);
                col = centerCol - 2 + random.nextInt(5);
            } while (row < 0 || row >= rows || col < 0 || col >= cols
                    || !(grid[row][col] instanceof StandardFile));
            grid[row][col] = new EncryptedVault(row, col);
        }

        // Step 4: Spawn AntivirusSentinels near the core
        for (int i = 0; i < numSentinels; i++) 
        {
            int row, col;
            do 
            {
                row = centerRow - 3 + random.nextInt(7);
                col = centerCol - 3 + random.nextInt(7);
            } while (row < 0 || row >= rows || col < 0 || col >= cols);
            agents.add(new AntivirusSentinel(row, col, defaultScanRange));
        }

        // Step 5: Spawn RepairBots near the core
        for (int i = 0; i < numRepairBots; i++) 
        {
            int row, col;
            do 
            {
                row = centerRow - 3 + random.nextInt(7);
                col = centerCol - 3 + random.nextInt(7);
            } while (row < 0 || row >= rows || col < 0 || col >= cols);
            agents.add(new RepairBot(row, col, 20, defaultScanRange));
        }

        // Step 6: Spawn MalwareStrains at random edge positions
        for (int i = 0; i < numMalware; i++) 
        {
            int row, col;
            int edge = random.nextInt(4);
            switch (edge) 
            {
                case 0: row = 0; col = random.nextInt(cols); break;
                case 1: row = rows - 1; col = random.nextInt(cols); break;
                case 2: row = random.nextInt(rows); col = 0; break;
                default: row = random.nextInt(rows); col = cols - 1; break;
            }
            agents.add(new MalwareStrain(row, col, defaultScanRange));
        }
    }

    /** @return the data layer grid */
    public NetworkNode[][] getGrid() { return grid; }

    /** @return the list of active agents */
    public List<ActiveAgent> getAgents() { return agents; }

    public void setNumSentinels(int n) { this.numSentinels = n; }
    public void setNumRepairBots(int n) { this.numRepairBots = n; }
    public void setNumMalware(int n) { this.numMalware = n; }
    public void setNumVaults(int n) { this.numVaults = n; }
    public void setDefaultScanRange(int r) { this.defaultScanRange = r; }

    /** Advances the simulation by one tick. */
    public void tick() {
        // TODO: implement simulation loop
    }
}

