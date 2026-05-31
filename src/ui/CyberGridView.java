package ui;

import java.awt.*;
import javax.swing.*;
import simulation.CyberGridSimulation;
import base.NetworkNode;

public class CyberGridView 
{
    private JFrame frame;
    private SimPanel simPanel;
    
    private CyberGridSimulation sim;
    
    private Timer animationTimer;
    private final int FRAME_DELAY = 100;
    
    public CyberGridView(CyberGridSimulation sim)
    {
        this.sim = sim;
        initializeGUI();
        setupAnimationTimer();
    }
    
    private void initializeGUI()
    {
        frame = new JFrame("Network Simulation -- Team Firewall");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        simPanel = new SimPanel();
        frame.add(simPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.DARK_GRAY);
        
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause Sim");
        
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        frame.add(controlPanel, BorderLayout.SOUTH);
        
        frame.setSize(new Dimension(900, 650));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void setupAnimationTimer()
    {
        animationTimer = new Timer(FRAME_DELAY, e ->
        {
            sim.update();
            
            simPanel.repaint();
        });
        
        animationTimer.start();
    }
    
    private class SimPanel extends JPanel
    {
        public SimPanel()
        {
            setBackground(Color.BLACK);
        }
        
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            
            if (sim == null)
            {
                return;
            }
            
            NetworkNode[][] grid = sim.getGrid();
            int rows = grid.length;
            int columns = grid[0].length;
            
            int cellWidth = getWidth() / columns;
            int cellHeight = getHeight() / rows;
            
            for (int r = 0; r < rows; r++)
            {
                for (int c = 0; c < columns; c++)
                {
                    NetworkNode node = grid[r][c];
                    
                    if (node != null)
                    {
                        g.setColor(node.getColor());
                        
                        g.fillRect(c * cellWidth, r * cellHeight, 
                                cellWidth, cellHeight);
                        
                        g.setColor(Color.decode("#111111"));
                        
                        g.drawRect(c * cellWidth, r * cellHeight, 
                                cellWidth, cellHeight);
                    }
                }
            }
        }
    }
    
    public static void main(String[] args)
    {
        CyberGridSimulation simEngine = new CyberGridSimulation(50, 50);
        
        SwingUtilities.invokeLater(() -> new CyberGridView(simEngine));
    }
}
