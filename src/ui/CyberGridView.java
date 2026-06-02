package ui;

import java.awt.*;
import javax.swing.*;
import simulation.CyberGridSimulation;
import simulation.SimulationConfig;
import base.NetworkNode;
import base.ActiveAgent;
import java.util.List;

public class CyberGridView 
{
    private JFrame frame;
    private SimPanel simPanel;
    
    private CyberGridSimulation sim;
    
    private Timer animationTimer;
    
    public CyberGridView(CyberGridSimulation sim)
    {
        this.sim = sim;
        initializeGUI();
        setupAnimationTimer();
    }
    
    private void initializeGUI()
    {
        frame = new JFrame("Network Simulation -- Server Under Attack");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        simPanel = new SimPanel();
        frame.add(simPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.DARK_GRAY);
        
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause Simulation");
        
        startButton.addActionListener(e -> animationTimer.start());
        pauseButton.addActionListener(e -> animationTimer.stop());
        
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        frame.add(controlPanel, BorderLayout.SOUTH);
        
        frame.setSize(new Dimension(900, 700));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void setupAnimationTimer()
    {
        animationTimer = new Timer(sim.getConfig().getTickDelay(), e ->
        {
            sim.tick();
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
        
        @Override
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
            
            java.util.List<base.ActiveAgent> agents = sim.getAgents();
            
            if (agents != null)
            {
                for (base.ActiveAgent agent : agents)
                {
                    int r = agent.getRow();
                    int c = agent.getCol();
                    
                    g.setColor(agent.getColor());
                    
                    g.fillOval(c * cellWidth, r * cellHeight, 
                                cellWidth, cellHeight);
                    
                    g.setColor(Color.WHITE);
                    
                    g.drawOval(c * cellWidth, r * cellHeight, 
                                cellWidth, cellHeight);
                }
            }
        }
    }
    
    public static void main(String[] args)
    {
        SimulationConfig config = new SimulationConfig();
        config.setRows(50);
        config.setCols(50);
        CyberGridSimulation simEngine = new CyberGridSimulation(config);

        SwingUtilities.invokeLater(() -> new CyberGridView(simEngine));
    }
}
