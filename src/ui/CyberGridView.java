package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import simulation.CyberGridSimulation;
import simulation.SimulationConfig;
import base.NetworkNode;

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

    // Kept as fields so they can be disabled once the simulation ends.
    private JButton startButton;
    private JButton pauseButton;
    private JSlider speedSlider;
    
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
        JLabel malwareHeader = new JLabel("ACTIVE MALWARE:");
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
        
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause Simulation");

        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 8);
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
            sentinelValueLabel.setText(String.valueOf(sim.getDefenderCount()));
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

            // A finished run overrides the live status and stops the simulation.
            if (sim.getOutcome() != CyberGridSimulation.Outcome.ONGOING)
            {
                animationTimer.stop();
                frame.dispose();
                createNewEndScreen(sim.getOutcome(), sim.getTickCount());
            }

            simPanel.repaint();
        });
    }

    /**
     * Terminates the active simulation view by destroying the primary window framework 
     * and launching a dedicated, standalone endgame console dashboard.
     * This method swaps the layout to display final mission metrics, including total 
     * operational cycles (ticks), final malware threat levels, surviving defenders, 
     * and total grid corruption percentages, presented inside a specialized dark-themed
     * terminal window container.
     *
     * @param outcome    the final resolution state of the environment engine, indicating 
     * either a structural victory or a critical system compromise.
     * @param totalTicks the total number of processing simulation loops executed by the 
     * back-end background timer before termination.
     */
    
    private void createNewEndScreen(CyberGridSimulation.Outcome outcome, int totalTicks)
    {
        boolean victory = outcome == CyberGridSimulation.Outcome.VICTORY;
        
        JFrame endFrame = new JFrame(victory ? "SYSTEM SECURED" : "SYSTEM TAKEOVER");
        endFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        endFrame.setSize(new Dimension(500, 400));
        endFrame.setLocationRelativeTo(null);
        endFrame.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.decode("#121212"));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel resultLabel = new JLabel(victory ? "VICTORY!" : "MALWARE TOOK OVER");
        resultLabel.setForeground(victory ? Color.GREEN : Color.RED);
        resultLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(resultLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel subLabel = new JLabel(victory ? "All malware was successfully eliminated." : "System has been compromised.");
        subLabel.setForeground(Color.LIGHT_GRAY);
        subLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        JLabel statsHeader = new JLabel("FINAL Sim Stats");
        statsHeader.setForeground(Color.CYAN);
        statsHeader.setFont(new Font("Monospaced", Font.BOLD, 14));
        statsHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statsHeader);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        String[] statLines;
        
        if (victory) 
        {
            // If defenders won, show how many malware were killed, and show surviving defenders
            int totalMalwareKilled = sim.getConfig().getNumMalware() - sim.getMalwareCount();
            statLines = new String[] {
                "Total Cycles (Ticks): " + totalTicks,
                "Malware Eliminated: " + totalMalwareKilled,
                "Surviving Defenders: " + sentinelValueLabel.getText(),
                "Grid Corruption: " + infectionValueLabel.getText()
            };
        } 
        else 
        {
            // If malware won, hide surviving defenders entirely and only show how many died
            int totalStartingDefenders = sim.getConfig().getNumSentinels() + sim.getConfig().getNumRepairBots();
            int totalDefendersKilled = totalStartingDefenders - sim.getDefenderCount();
            statLines = new String[] {
                "Total Cycles (Ticks): " + totalTicks,
                "Defenders Defeated: " + totalDefendersKilled,
                "Surviving Malware: " + malwareValueLabel.getText(),
                "Grid Corruption: " + infectionValueLabel.getText()
            };
        }
        
        for (String line : statLines) {
            JLabel statLabel = new JLabel(line);
            statLabel.setForeground(Color.WHITE);
            statLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
            statLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(statLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        JButton exitButton = new JButton("Terminate Sim");
        exitButton.setFont(new Font("Monospaced", Font.BOLD, 12));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));
        mainPanel.add(exitButton);

        endFrame.add(mainPanel);
        endFrame.setVisible(true);
    }
    
    /**
     * Prompts the user with a configuration window prior to spawning the primary graphics panel.
     * @param defaults fallback default properties object template to pull from
     * @return true if input variables parsed cleanly, false if user exited or chose default configuration
     */
    private static boolean showSetupDialog(SimulationConfig defaults)
    {
        // Fields start empty so their range hint shows as placeholder text. Leaving a
        // field blank keeps the program default for that value.
        LimitedField rowField = new LimitedField("10 - 150", 10, 150, 6);
        LimitedField colField = new LimitedField("10 - 150", 10, 150, 6);
        LimitedField malwareField = new LimitedField("0 - 100", 0, 100, 6);
        LimitedField sentinelField = new LimitedField("0 - 100", 0, 100, 6);
        LimitedField repairField = new LimitedField("0 - 100", 0, 100, 6);
        // Vaults can occupy any non-core cell, the hard cap of (rows x cols - 4) is applied due to this
        // once the grid size is known
        LimitedField vaultField = new LimitedField("0 - rows x cols - 4", 0, 150 * 150 - 4, 6);

        JComboBox<SimulationConfig.MalwareMovement> malwareMoveBox =
                new JComboBox<>(SimulationConfig.MalwareMovement.values());
        malwareMoveBox.setSelectedItem(SimulationConfig.MalwareMovement.SEEK_TARGET);

        // Only the implemented death behaviors are offered.
        JComboBox<SimulationConfig.DeathBehavior> deathBehaviorBox =
                new JComboBox<>(new SimulationConfig.DeathBehavior[]{
                        SimulationConfig.DeathBehavior.REMOVE,
                        SimulationConfig.DeathBehavior.RESPAWN});
        deathBehaviorBox.setSelectedItem(SimulationConfig.DeathBehavior.REMOVE);

        LimitedField respawnDelayField = new LimitedField("0 - 500", 0, 500, 6);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2, 10, 10));
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
        panel.add(new JLabel("Malware Movement:"));
        panel.add(malwareMoveBox);
        panel.add(new JLabel("On Death:"));
        panel.add(deathBehaviorBox);
        panel.add(new JLabel("Respawn Delay (ticks):"));
        panel.add(respawnDelayField);

        int result = JOptionPane.showConfirmDialog(
            null, 
            panel, 
            "CYBER GRID ENVIRONMENT SETUP", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION)
        {
            // The fields already block out-of-range input, so here we just read each
            // value (falling back to the existing default when a field was left blank).
            int rows = rowField.getValueOrDefault(defaults.getRows());
            int cols = colField.getValueOrDefault(defaults.getCols());

            // Vaults can only fill non-core cells, so cap to what the chosen grid holds.
            int vaults = Math.min(vaultField.getValueOrDefault(defaults.getNumVaults()), rows * cols - 4);

            defaults.setRows(rows);
            defaults.setCols(cols);
            defaults.setNumMalware(malwareField.getValueOrDefault(defaults.getNumMalware()));
            defaults.setNumSentinels(sentinelField.getValueOrDefault(defaults.getNumSentinels()));
            defaults.setNumRepairBots(repairField.getValueOrDefault(defaults.getNumRepairBots()));
            defaults.setNumVaults(vaults);
            defaults.setMalwareMovement((SimulationConfig.MalwareMovement) malwareMoveBox.getSelectedItem());
            defaults.setDeathBehavior((SimulationConfig.DeathBehavior) deathBehaviorBox.getSelectedItem());
            defaults.setRespawnDelay(respawnDelayField.getValueOrDefault(defaults.getRespawnDelay()));
            return true;
        }
        return false;
    }

    /**
     * A {@link JTextField} for the setup dialog that enforces a numeric range inline:
     * it accepts digits only, rejects keystrokes that would push the value above
     * {@code max}, and snaps a too-low value up to {@code min} when focus leaves the
     * field. While empty it paints greyed-out placeholder text describing the range.
     */
    private static class LimitedField extends JTextField
    {
        private final String placeholder;
        private final int min;
        private final int max;

        LimitedField(String placeholder, int min, int max, int columns)
        {
            super(columns);
            this.placeholder = placeholder;
            this.min = min;
            this.max = max;

            // Reject non-digits and anything that would exceed the maximum as it is typed.
            ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter()
            {
                @Override
                public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                        throws BadLocationException
                {
                    replace(fb, offset, 0, text, attr);
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                        throws BadLocationException
                {
                    String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                    String proposed = current.substring(0, offset)
                            + (text == null ? "" : text)
                            + current.substring(offset + length);

                    if (proposed.isEmpty())
                    {
                        fb.replace(offset, length, text, attr);   // allow clearing the field
                        return;
                    }
                    if (!proposed.matches("\\d+"))
                    {
                        return;   // digits only
                    }
                    try
                    {
                        if (Long.parseLong(proposed) > max)
                        {
                            return;   // would exceed the maximum
                        }
                    }
                    catch (NumberFormatException tooLong)
                    {
                        return;   // absurdly long number, definitely over max
                    }
                    fb.replace(offset, length, text, attr);
                }
            });

            // The minimum can't be enforced mid-typing (e.g. "1" on the way to "15"),
            // so snap any too-low value up once the user leaves the field.
            addFocusListener(new FocusAdapter()
            {
                @Override
                public void focusLost(FocusEvent e)
                {
                    String t = getText().trim();
                    if (!t.isEmpty() && Integer.parseInt(t) < min)
                    {
                        setText(String.valueOf(min));
                    }
                }
            });
        }

        /** @return the field's value clamped to [min, max], or {@code fallback} if blank. */
        int getValueOrDefault(int fallback)
        {
            String t = getText().trim();
            if (t.isEmpty())
            {
                return fallback;
            }
            return Math.max(min, Math.min(max, Integer.parseInt(t)));
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (getText().isEmpty())
            {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                Insets insets = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(placeholder, insets.left + 1, y);
                g2.dispose();
            }
        }
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