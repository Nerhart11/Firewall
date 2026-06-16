package simulation;

public class SimulationConfig {

    // Enums for toggle options

    // How malware damages the grid each tick:
    //   DIRECT   - only the cell the malware is standing on
    //   ADJACENT - the whole 3x3 block, full damage to each
    //   AURA     - the current cell at full damage, neighbors at half
    public enum InfectionMode { DIRECT, ADJACENT, AURA}

    // What happens to malware once it is killed (defenders always stay frozen
    // and repairable):
    //   REMOVE       - killed malware is taken off the grid for good
    //   RESPAWN      - killed malware is removed, then a replacement spawns at a
    //                  random edge after respawnDelay ticks
    //   MALWARE_ONLY - not yet implemented; currently a no-op, so killed malware
    //                  just stays frozen in place. Reserved for future use.
    public enum DeathBehavior {REMOVE, RESPAWN, MALWARE_ONLY}

    // Order a RepairBot picks what to fix first:
    //   CORE_AGENTS_GRID - System Core, then downed agents, then ordinary nodes
    public enum RepairPriority {CORE_AGENTS_GRID}

    // How malware decides where to move:
    //   SEEK_TARGET - hunt down the nearest healthy node and infect it
    //   RANDOM      - wander randomly, infecting whatever it happens to sit on
    public enum MalwareMovement { SEEK_TARGET, RANDOM }

    // Defaults for grid size
    private int rows = 20;
    private int cols = 20;

    // Defaults for toggle options
    private InfectionMode infectionMode = InfectionMode.DIRECT;
    private DeathBehavior deathBehavior = DeathBehavior.REMOVE;
    private RepairPriority repairPriority = RepairPriority.CORE_AGENTS_GRID;
    private MalwareMovement malwareMovement = MalwareMovement.SEEK_TARGET;

    // Defaults for # of Agents on the grid
    private int numSentinels = 5;
    private int numRepairBots = 3;
    private int numVaults = 10;
    private int numMalware = 4;

    // Default stats for Agents
    private int defaultScanRange = 3;
    private int malwareDamage = 20;
    private int sentinelDamage = 20;
    private int repairBotPower = 20;

    // Timing behavior
    private int respawnDelay = 10;
    private int tickDelay = 150;
    // Survival win: reach this many ticks with the core still alive. 0 disables it.
    private int maxTicks = 1000;

    // Loss trigger: defeat once this percentage of the grid is corrupted.
    private double infectionLossThreshold = 75.0;

    // Getters
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public InfectionMode getInfectionMode() { return infectionMode; }
    public DeathBehavior getDeathBehavior() { return deathBehavior; }
    public RepairPriority getRepairPriority() { return repairPriority; }
    public MalwareMovement getMalwareMovement() { return malwareMovement; }

    public int getNumSentinels() { return numSentinels; }
    public int getNumRepairBots() { return numRepairBots; }
    public int getNumVaults() { return numVaults; }
    public int getNumMalware() { return numMalware; }

    public int getDefaultScanRange() { return defaultScanRange; }
    public int getMalwareDamage() { return malwareDamage; }
    public int getSentinelDamage() { return sentinelDamage; }
    public int getRepairBotPower() { return repairBotPower; }

    public int getRespawnDelay() { return respawnDelay; }
    public int getTickDelay() { return tickDelay; }
    public int getMaxTicks() { return maxTicks; }
    public double getInfectionLossThreshold() { return infectionLossThreshold; }

    // Setters
    public void setRows(int rows) { this.rows = rows; }
    public void setCols(int cols) { this.cols = cols; }

    public void setInfectionMode(InfectionMode mode) { this.infectionMode = mode; }
    public void setDeathBehavior(DeathBehavior behavior) { this.deathBehavior = behavior; }
    public void setRepairPriority(RepairPriority priority) { this.repairPriority = priority; }
    public void setMalwareMovement(MalwareMovement movement) { this.malwareMovement = movement; }

    public void setNumSentinels(int n) { this.numSentinels = n; }
    public void setNumRepairBots(int n) { this.numRepairBots = n; }
    public void setNumVaults(int n) { this.numVaults = n; }
    public void setNumMalware(int n) { this.numMalware = n; }

    public void setDefaultScanRange(int r) { this.defaultScanRange = r; }
    public void setMalwareDamage(int d) { this.malwareDamage = d; }
    public void setSentinelDamage(int d) { this.sentinelDamage = d; }
    public void setRepairBotPower(int p) { this.repairBotPower = p; }

    public void setRespawnDelay(int delay) { this.respawnDelay = delay; }
    public void setTickDelay(int delay) { this.tickDelay = delay; }
    public void setMaxTicks(int max) { this.maxTicks = max; }
    public void setInfectionLossThreshold(double percent) { this.infectionLossThreshold = percent; }
}
