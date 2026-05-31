package simulation;

import base.NetworkNode;

public class SimulationTest 
{
    public static void main(String[] args) 
    {
        System.out.println("Starting CyberGridSimulation Test");
        
        CyberGridSimulation testSim = new CyberGridSimulation(10, 10);
        
        System.out.println("Simulation grid created successfully.");
        
        //Retrieve and verify the grid matrix
        NetworkNode[][] grid = testSim.getGrid();
        
        if (grid == null) 
        {
            System.out.println("Error: getGrid() returned null!");
            return;
        }
        
        System.out.println("Grid dimensions: " + grid.length + "x" + 
                grid[0].length);
        
        //Print the initial text representation of the grid
        System.out.println("\nInitial Grid State (Text Output):");
        printGridText(grid);
        
        System.out.println("\nAdvancing simulation by 1 tick...");
        testSim.update();
        
        System.out.println("Grid State After Update:");
        printGridText(grid);
        
        System.out.println("\nTesting Complete");
    }
    
    /**
     * Helper method to satisfy the milestone requirement for text-based output.
     */
    private static void printGridText(NetworkNode[][] grid) 
    {
        for (int r = 0; r < grid.length; r++) 
        {
            for (int c = 0; c < grid[r].length; c++) 
            {
                if (grid[r][c] == null) 
                {
                    System.out.print(". "); //Dot represents an empty node
                } else 
                {
                    System.out.print("X "); //X represents a populated node
                }
            }
            System.out.println(); //Newline at the end of each row
        }
    }
}
