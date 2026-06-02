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
        frame = new JFrame("Network Simulation -- Server Is Safe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        simPanel = new SimPanel();
        frame.add(simPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.DARK_GRAY);
        
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause Simulation");
        
        startButton.addActionListener(e -> {
            animationTimer.start();
            frame.setTitle("Network Simulation -- Server Under Attack");
        });
        pauseButton.addActionListener(e -> {
            animationTimer.stop();
            frame.setTitle("Network Simulation -- Simulation Paused");
        });
        
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
        // Create a config with defaults, then override whatever you need:
        SimulationConfig config = new SimulationConfig();

        // Grid size
        config.setRows(50);
        config.setCols(50);

        // Agent counts
        // config.setNumSentinels(5);
        // config.setNumRepairBots(3);
        // config.setNumMalware(4);
        // config.setNumVaults(10);

        // Agent stats
        // config.setDefaultScanRange(3);
        // config.setMalwareDamage(20);
        // config.setSentinelDamage(20);
        // config.setRepairBotPower(20);

        // Behavior modes
        // config.setInfectionMode(SimulationConfig.InfectionMode.DIRECT);   // DIRECT, ADJACENT, or AURA
        // config.setDeathBehavior(SimulationConfig.DeathBehavior.REMOVE);    // REMOVE, RESPAWN, or MALWARE_ONLY
        // config.setRepairPriority(SimulationConfig.RepairPriority.CORE_AGENTS_GRID);

        // Timing
        // config.setTickDelay(150);    // ms between ticks
        // config.setMaxTicks(0);       // 0 = unlimited
        // config.setRespawnDelay(10);  // ticks before respawn

        CyberGridSimulation simEngine = new CyberGridSimulation(config);

        SwingUtilities.invokeLater(() -> new CyberGridView(simEngine));
    }
}
