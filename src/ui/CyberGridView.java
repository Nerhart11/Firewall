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
    
    private JLabel malwareValueLabel;
    private JLabel sentinelValueLabel;
    private JLabel infectionValueLabel;
    private JLabel statusValueLabel;
    
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
        
        // Setup console telemetry sidebar layout panel
        // Setup console telemetry sidebar layout panel
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(Color.decode("#1E1E1E")); 
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidePanel.setPreferredSize(new Dimension(220, 700));

        JLabel titleLabel = new JLabel("SIMULATION STATS");
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        sidePanel.add(titleLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 25))); 

        // 1. Threat Level Section
        JLabel malwareHeader = new JLabel("THREAT LEVEL");
        malwareHeader.setForeground(Color.GRAY);
        malwareHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        sidePanel.add(malwareHeader);

        malwareValueLabel = new JLabel("0");
        malwareValueLabel.setForeground(Color.decode("#FF3333")); 
        malwareValueLabel.setFont(new Font("Monospaced", Font.BOLD, 18)); // Made bigger for visibility
        sidePanel.add(malwareValueLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20))); // Padding between groups

        // 2. Active Defenders Section
        JLabel sentinelHeader = new JLabel("ACTIVE DEFENDERS");
        sentinelHeader.setForeground(Color.GRAY);
        sentinelHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        sidePanel.add(sentinelHeader);

        sentinelValueLabel = new JLabel("0");
        sentinelValueLabel.setForeground(Color.decode("#3366FF")); 
        sentinelValueLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        sidePanel.add(sentinelValueLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Grid Corruption Section
        JLabel infectionHeader = new JLabel("INFECTED GRID (%)");
        infectionHeader.setForeground(Color.GRAY);
        infectionHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        sidePanel.add(infectionHeader);

        infectionValueLabel = new JLabel("0.0%");
        infectionValueLabel.setForeground(Color.ORANGE); 
        infectionValueLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        sidePanel.add(infectionValueLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 4. System Status Section
        JLabel statusHeader = new JLabel("SYSTEM STATUS");
        statusHeader.setForeground(Color.GRAY);
        statusHeader.setFont(new Font("Monospaced", Font.BOLD, 12));
        sidePanel.add(statusHeader);

        statusValueLabel = new JLabel("SECURE");
        statusValueLabel.setForeground(Color.GREEN);
        statusValueLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        sidePanel.add(statusValueLabel);

        frame.add(sidePanel, BorderLayout.EAST);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.DARK_GRAY);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause Simulation");
        
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 8);
        speedSlider.setBackground(Color.DARK_GRAY);
        speedSlider.setForeground(Color.WHITE);
        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        
        JLabel sliderLabel = new JLabel("Speed: 150ms");
        sliderLabel.setForeground(Color.WHITE);
        sliderLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        speedSlider.addChangeListener(e -> {
            int sliderValue = speedSlider.getValue();
            int calculatedDelay;
            
            if (sliderValue == 1) calculatedDelay = 500;
            else if (sliderValue == 2) calculatedDelay = 450;
            else if (sliderValue == 3) calculatedDelay = 400;
            else if (sliderValue == 4) calculatedDelay = 350;
            else if (sliderValue == 5) calculatedDelay = 300;
            else if (sliderValue == 6) calculatedDelay = 250;
            else if (sliderValue == 7) calculatedDelay = 200;
            else if (sliderValue == 8) calculatedDelay = 150;
            else if (sliderValue == 9) calculatedDelay = 100;
            else calculatedDelay = 50;
            
            sliderLabel.setText("Speed: " + calculatedDelay + "ms");
            animationTimer.setDelay(calculatedDelay);
        });
        
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
        controlPanel.add(speedSlider);
        controlPanel.add(sliderLabel);
        
        frame.add(controlPanel, BorderLayout.SOUTH);
        
        frame.setSize(new Dimension(1120, 700)); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
    private void setupAnimationTimer()
    {
        animationTimer = new Timer(sim.getConfig().getTickDelay(), e ->
        {
            sim.tick();
            
            // Directly update the clean number labels on every tick
            malwareValueLabel.setText(String.valueOf(sim.getMalwareCount()));
            sentinelValueLabel.setText(String.valueOf(sim.getSentinelCount()));
            infectionValueLabel.setText(String.format("%.1f%%", sim.getInfectionPercentage()));
            
            // Handle active security state shifts dynamically
            if (sim.getInfectionPercentage() > 25.0 || sim.getMalwareCount() > 5) 
            {
                statusValueLabel.setText("DANGER");
                statusValueLabel.setForeground(Color.RED);
                frame.setTitle("Network Simulation -- Server Under Attack");
            } 
            else 
            {
                statusValueLabel.setText("SECURE");
                statusValueLabel.setForeground(Color.GREEN);
            }
            
            simPanel.repaint();
        });
    }
    
    /**
     * Prompts the user with a configuration window prior to spawning the primary graphics panel.
     * @param defaults fallback default properties object template to pull from
     * @return true if input variables parsed cleanly, false if user exited or chose default configuration
     */
    private static boolean showSetupDialog(SimulationConfig defaults)
    {
        JTextField rowField = new JTextField("50", 5);
        JTextField colField = new JTextField("50", 5);
        JTextField malwareField = new JTextField("4", 5);
        JTextField sentinelField = new JTextField("5", 5);
        JTextField repairField = new JTextField("3", 5);
        JTextField vaultField = new JTextField("10", 5);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Grid Rows:"));
        panel.add(rowField);
        panel.add(new JLabel("Grid Columns:"));
        panel.add(colField);
        panel.add(new JLabel("Initial Malware Count:"));
        panel.add(malwareField);
        panel.add(new JLabel("Antivirus Sentinels:"));
        panel.add(sentinelField);
        panel.add(new JLabel("Network Repair Bots:"));
        panel.add(repairField);
        panel.add(new JLabel("Encrypted Vaults:"));
        panel.add(vaultField);

        int result = JOptionPane.showConfirmDialog(
            null, 
            panel, 
            "CYBER GRID ENVIRONMENT SETUP", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) 
        {
            try 
            {
                defaults.setRows(Integer.parseInt(rowField.getText().trim()));
                defaults.setCols(Integer.parseInt(colField.getText().trim()));
                defaults.setNumMalware(Integer.parseInt(malwareField.getText().trim()));
                defaults.setNumSentinels(Integer.parseInt(sentinelField.getText().trim()));
                defaults.setNumRepairBots(Integer.parseInt(repairField.getText().trim()));
                defaults.setNumVaults(Integer.parseInt(vaultField.getText().trim()));
                return true;
            } 
            catch (NumberFormatException e) 
            {
                JOptionPane.showMessageDialog(null, "Invalid entry! Reverting to program defaults.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
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
        config.setNumSentinels(5);
        config.setNumRepairBots(3);
        config.setNumMalware(4);
        config.setNumVaults(10);
        
        config.setDefaultScanRange(3);
        config.setMalwareDamage(20);
        config.setSentinelDamage(20);
        config.setRepairBotPower(20);
        config.setTickDelay(150);    

        // Launches configuration prompt. If canceled, drops directly down to execution defaults.
        showSetupDialog(config);

        CyberGridSimulation simEngine = new CyberGridSimulation(config);

        SwingUtilities.invokeLater(() -> new CyberGridView(simEngine));
    }
}