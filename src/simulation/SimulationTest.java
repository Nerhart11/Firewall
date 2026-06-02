package simulation;

import base.ActiveAgent;
import base.NetworkNode;

public class SimulationTest 
{
    // set to false to hide agents on the grid and only show their details in 
    // the Agents section. This is here because the text version overrites the 
    // dataCell layer when an activeAgent is present, which can make it hard to 
    // see the grid layout when agents are present. In a GUI version, this 
    // would not be an issue.
    
    private static final boolean SHOW_AGENTS_ON_GRID = false;

    public static void main(String[] args) 
    {
        CyberGridSimulation simulation = new CyberGridSimulation(new SimulationConfig());
        NetworkNode[][] grid = simulation.getGrid();

        System.out.println("=== Grid ===");
        for (int row = 0; row < grid.length; row++) 
        {
            for (int col = 0; col < grid[row].length; col++) 
            {
                String symbol = grid[row][col].toString();

                if (SHOW_AGENTS_ON_GRID) 
                {
                    for (ActiveAgent agent : simulation.getAgents()) 
                    {
                        if (agent.getRow() == row && agent.getCol() == col) 
                        {
                            symbol = agent.toString();
                            break;
                        }
                    }
                }

                System.out.print(symbol + " ");
            }
            System.out.println();
        }

        System.out.println("\n=== Agents ===");
        for (ActiveAgent agent : simulation.getAgents()) 
        {
            System.out.println(agent.getTypeName() + " at (" + agent.getRow() 
                    + ", " + agent.getCol() + ")");
        }
    }
}
