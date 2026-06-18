package simulation;

import base.ActiveAgent;
import base.NetworkNode;
import models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Manages the simulation grid and agent layer.
 */
public class CyberGridSimulation
{
    /** Result of the simulation. ONGOING until a win or loss condition is met. */
    public enum Outcome { ONGOING, VICTORY, DEFEAT }

    private NetworkNode[][] grid;
    private List<ActiveAgent> agents;
    private final int rows;
    private final int cols;
    private final Random random = new Random();
    private final SimulationConfig config;

    // Countdown timers (in ticks) for malware waiting to respawn under RESPAWN mode.
    private List<Integer> respawnTimers = new ArrayList<>();

    // Win/loss tracking.
    private Outcome outcome = Outcome.ONGOING;
    private int tickCount = 0;
    private SystemCore.CoreHealth coreHealth;
    private final boolean hadDefenders;
    private final boolean hadMalware;

    public CyberGridSimulation(SimulationConfig config)
    {
        this.config = config;
        this.rows = config.getRows();
        this.cols = config.getCols();
        this.grid = new NetworkNode[rows][cols];
        this.agents = new ArrayList<>();

        // A run can only be lost by losing defenders / won by clearing malware if it
        // actually started with some; otherwise those checks would fire on tick one.
        this.hadDefenders = config.getNumSentinels() + config.getNumRepairBots() > 0;
        this.hadMalware = config.getNumMalware() > 0;

        initializeGrid();
    }

    private void initializeGrid() 
    {
        // Step 1: Fill everything with StandardFiles
        for (int row = 0; row < rows; row++) 
        {
            for (int col = 0; col < cols; col++) 
            {
                grid[row][col] = new StandardFile(row, col);
            }
        }

        // Step 2: Place 2x2 SystemCore at center. All four cells share one health pool
        // so the core behaves as a single high-health target.
        int centerRow = rows / 2 - 1;
        int centerCol = cols / 2 - 1;
        coreHealth = new SystemCore.CoreHealth(SystemCore.CORE_MAX_HP, SystemCore.CORE_REPAIR_THRESHOLD);
        for (int row = centerRow; row <= centerRow + 1; row++)
        {
            for (int col = centerCol; col <= centerCol + 1; col++)
            {
                grid[row][col] = new SystemCore(row, col, coreHealth);
            }
        }

        // Step 3: Place EncryptedVaults on the StandardFile cells nearest the core.
        // We gather every free cell, shuffle it, then stably sort by ring distance to
        // the core. Because List.sort is stable, vaults fill the innermost ring first
        // but land at random spots within each ring -- and the exact requested count is
        // always placed as long as the grid physically has room (guaranteed by the
        // config caps applied in the setup dialog).
        List<int[]> freeCells = new ArrayList<>();
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                if (grid[row][col] instanceof StandardFile)
                {
                    freeCells.add(new int[]{row, col});
                }
            }
        }

        final int coreRow = centerRow;
        final int coreCol = centerCol;
        Collections.shuffle(freeCells, random);
        freeCells.sort(Comparator.comparingInt(
                cell -> Math.max(Math.abs(cell[0] - coreRow), Math.abs(cell[1] - coreCol))));

        int vaultsToPlace = Math.min(config.getNumVaults(), freeCells.size());
        for (int i = 0; i < vaultsToPlace; i++)
        {
            int[] cell = freeCells.get(i);
            grid[cell[0]][cell[1]] = new EncryptedVault(cell[0], cell[1]);
        }

        // Step 4: Spawn AntivirusSentinels near the core
        for (int i = 0; i < config.getNumSentinels(); i++)
        {
            int row, col;
            do
            {
                row = centerRow - 3 + random.nextInt(7);
                col = centerCol - 3 + random.nextInt(7);
            } while (row < 0 || row >= rows || col < 0 || col >= cols);
            agents.add(new AntivirusSentinel(row, col, config.getSentinelDamage(), config.getDefaultScanRange()));
        }

        // Step 5: Spawn RepairBots near the core
        for (int i = 0; i < config.getNumRepairBots(); i++)
        {
            int row, col;
            do
            {
                row = centerRow - 3 + random.nextInt(7);
                col = centerCol - 3 + random.nextInt(7);
            } while (row < 0 || row >= rows || col < 0 || col >= cols);
            agents.add(new RepairBot(row, col, config.getRepairBotPower(), config.getDefaultScanRange()));
        }

        // Step 6: Spawn MalwareStrains at random edge positions
        for (int i = 0; i < config.getNumMalware(); i++)
        {
            spawnMalware();
        }
    }

    /**
     * Spawns a single MalwareStrain at a random edge of the grid using the
     * configured malware stats. Shared by initial setup and respawning.
     */
    private void spawnMalware()
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
        agents.add(new MalwareStrain(row, col, config.getMalwareDamage(), config.getDefaultScanRange()));
    }

    /** @return the data layer grid */
    public NetworkNode[][] getGrid() { return grid; }

    /** @return the list of active agents */
    public List<ActiveAgent> getAgents() { return agents; }

    /** @return the simulation configuration */
    public SimulationConfig getConfig() { return config; }

    /**
     * Advances the simulation by one tick. Every agent acts first (queuing combat),
     * the combat is applied all at once, killed malware is removed, and then the
     * surviving agents move. Acting before moving keeps the combat fair.
     */
    public void tick()
    {
        if (agents == null) return;

        // Once the run is decided, stop advancing it.
        if (outcome != Outcome.ONGOING) return;

        tickCount++;

        CombatResolver resolver = new CombatResolver();

        // Act phase: every active agent scans and queues combat (no mutation yet).
        for (ActiveAgent agent : agents)
        {
            if (!agent.isCorrupted())
            {
                agent.action(grid, agents, config, resolver);
            }
        }

        // Resolve all queued damage and repair at once.
        resolver.apply();

        SimulationConfig.DeathBehavior deathBehavior = config.getDeathBehavior();

        // Tick down pending respawns and bring back any malware whose delay elapsed.
        if (deathBehavior == SimulationConfig.DeathBehavior.RESPAWN)
        {
            List<Integer> stillWaiting = new ArrayList<>();
            for (int ticksLeft : respawnTimers)
            {
                if (ticksLeft <= 1)
                {
                    spawnMalware();
                }
                else
                {
                    stillWaiting.add(ticksLeft - 1);
                }
            }
            respawnTimers = stillWaiting;
        }

        // Remove killed malware so the threat count can drop. Under RESPAWN we also
        // queue a replacement to appear after the configured delay.
        if (deathBehavior == SimulationConfig.DeathBehavior.REMOVE
                || deathBehavior == SimulationConfig.DeathBehavior.RESPAWN)
        {
            List<ActiveAgent> survivors = new ArrayList<>();
            for (ActiveAgent agent : agents)
            {
                boolean killedMalware = (agent instanceof MalwareStrain) && agent.isCorrupted();
                if (killedMalware)
                {
                    if (deathBehavior == SimulationConfig.DeathBehavior.RESPAWN)
                    {
                        respawnTimers.add(config.getRespawnDelay());
                    }
                }
                else
                {
                    survivors.add(agent);
                }
            }
            agents = survivors;
        }

        // Move phase: surviving agents reposition toward their chosen targets.
        for (ActiveAgent agent : agents)
        {
            if (!agent.isCorrupted())
            {
                agent.move(this.rows, this.cols);
            }
        }

        checkOutcome();
    }

    public int getTickCount()
    {
        return this.tickCount;
    }
    /**
     * Evaluates the win/loss conditions after a tick and latches the result. Losses
     * are checked before wins so an overrun network never registers as a victory.
     * Defeat: the System Core is destroyed, the grid is corrupted past the configured
     * threshold, or every defender has fallen. Victory: a configured survival tick
     * limit is reached with the core intact, or all malware is cleared with none
     * pending respawn.
     */
    private void checkOutcome()
    {
        if (coreHealth != null && coreHealth.isCorrupted())
        {
            outcome = Outcome.DEFEAT;
            return;
        }
        if (getInfectionPercentage() >= config.getInfectionLossThreshold())
        {
            outcome = Outcome.DEFEAT;
            return;
        }
        if (hadDefenders && getDefenderCount() == 0)
        {
            outcome = Outcome.DEFEAT;
            return;
        }

        int maxTicks = config.getMaxTicks();
        if (maxTicks > 0 && tickCount >= maxTicks)
        {
            outcome = Outcome.VICTORY;
            return;
        }
        if (hadMalware && getMalwareCount() == 0 && respawnTimers.isEmpty())
        {
            outcome = Outcome.VICTORY;
        }
    }

    /** @return the current win/loss state of the simulation. */
    public Outcome getOutcome()
    {
        return outcome;
    }

    /**
     * Gets the total number of active malware threats remaining on the grid.
     * @return the count of active MalwareStrain agents
     */
    public int getMalwareCount() 
    {
        int count = 0;
        if (this.agents != null) 
        {
            for (ActiveAgent a : this.agents)
            {
                if (a instanceof models.MalwareStrain && !a.isCorrupted())
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Calculates the total percentage of the network grid that has been infected.
     * @return the double value representing the infection percentage (0.0 to 100.0)
     */
    public double getInfectionPercentage() 
    {
        int totalNodes = rows * cols;
        int infectedCount = 0;

        if (grid == null) return 0.0;

        for (int r = 0; r < rows; r++) 
        {
            for (int c = 0; c < cols; c++) 
            {
                NetworkNode node = grid[r][c];
                // Checks if the node exists and if its custom properties mark it as corrupted/infected
                if (node != null && node.isCorrupted()) 
                {
                    infectedCount++;
                }
            }
        }

        return ((double) infectedCount / totalNodes) * 100.0;
    }
    
    /**
     * Gets the total number of protective security sentinels currently patrolling.
     * @return the count of active AntivirusSentinel agents
     */
    public int getSentinelCount()
    {
        int count = 0;
        if (this.agents != null)
        {
            for (ActiveAgent a : this.agents)
            {
                if (a instanceof models.AntivirusSentinel && !a.isCorrupted())
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Gets the total number of living defenders on the grid: both AntivirusSentinels
     * and RepairBots that have not been corrupted. Downed defenders are excluded and
     * count again once a RepairBot revives them.
     * @return the count of active defending agents
     */
    public int getDefenderCount()
    {
        int count = 0;
        if (this.agents != null)
        {
            for (ActiveAgent a : this.agents)
            {
                boolean isDefender = a instanceof models.AntivirusSentinel
                        || a instanceof models.RepairBot;
                if (isDefender && !a.isCorrupted())
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Gets the total number of rows in the simulation grid matrix.
     * @return the row count boundary
     */
    public int getRows() 
    {
        return this.rows;
    }

    /**
     * Gets the total number of columns in the simulation grid matrix.
     * @return the column count boundary
     */
    public int getCols() 
    {
        return this.cols;
    }
}

