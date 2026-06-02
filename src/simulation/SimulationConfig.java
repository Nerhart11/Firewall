package simulation;

public class SimulationConfig {

    // Enums for toggle options
    public enum InfectionMode { DIRECT, ADJACENT, AURA}
    public enum DeathBehavior {REMOVE, RESPAWN, MALWARE_ONLY}
    public enum RepairPriority {CORE_AGENTS_GRID}

    // Defaults for grid size
    private int rows = 20;
    private int cols = 20;

    // Defaults for toggle options
    private InfectionMode infectionMode = InfectionMode.DIRECT;
    private DeathBehavior deathBehavior = DeathBehavior.REMOVE;
    private RepairPriority repairPriority = RepairPriority.CORE_AGENTS_GRID;

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
    private int maxTicks = 0;

    // Getters
    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public InfectionMode getInfectionMode() { return infectionMode; }
    public DeathBehavior getDeathBehavior() { return deathBehavior; }
    public RepairPriority getRepairPriority() { return repairPriority; }

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

    // Setters
    public void setRows(int rows) { this.rows = rows; }
    public void setCols(int cols) { this.cols = cols; }

    public void setInfectionMode(InfectionMode mode) { this.infectionMode = mode; }
    public void setDeathBehavior(DeathBehavior behavior) { this.deathBehavior = behavior; }
    public void setRepairPriority(RepairPriority priority) { this.repairPriority = priority; }

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
}
