package base;

/**
 * Abstract class for nodes that can move independently across the grid.
 */
public abstract class ActiveAgent extends NetworkNode {

    private final int scanRange;

    /**
     * @param row grid row position
     * @param col grid column position
     * @param maxHP maximum health before corruption
     * @param repairThreshold total repair progress needed to restore
     * @param scanRange how far this agent can detect targets
     */
    public ActiveAgent(int row, int col, int maxHP, int repairThreshold, int scanRange) {
        super(row, col, maxHP, repairThreshold);
        this.scanRange = scanRange;
    }

    /** Moves this agent to a new position on the grid. */
    public void move(){

    }

    /** @return the scan/detection range of this agent */
    public int getScanRange() {
        return scanRange;
    }
}
