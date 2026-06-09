package base;

import simulation.CombatResolver;
import simulation.SimulationConfig;

import java.util.List;
import java.util.Random;

/**
 * Abstract class for nodes that can move independently across the grid.
 * Each tick an agent first acts (scans and queues combat), then moves.
 */
public abstract class ActiveAgent extends NetworkNode {

    private final int scanRange;

    /** Target picked during the act phase, used by the move phase. May be null. */
    protected NetworkNode currentTarget;

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

    /**
     * Act phase: scan from the current position, set currentTarget, and queue any
     * damage or repair through the resolver. Does not move or change HP directly.
     */
    public abstract void action(NetworkNode[][] grid, List<ActiveAgent> agents,
                                SimulationConfig config, CombatResolver resolver);

    /**
     * Move phase: step toward currentTarget if it is still valid, otherwise wander.
     */
    public abstract void move(int maxRows, int maxCols);

    /** @return the scan/detection range of this agent */
    public int getScanRange() {
        return scanRange;
    }

    /**
     * @return the number of 8-direction moves to reach the other node
     *         (max of the row and column distance).
     */
    public int stepDistanceTo(NetworkNode other) {
        int rowDist = Math.abs(this.row - other.getRow());
        int colDist = Math.abs(this.col - other.getCol());
        if (rowDist > colDist) {
            return rowDist;
        } else {
            return colDist;
        }
    }

    /** @return true if the other node is within this agent's scan range */
    public boolean isInScanRange(NetworkNode other) {
        return stepDistanceTo(other) <= scanRange;
    }

    /**
     * Finds the closest candidate that is within scan range.
     * @return the nearest in-range candidate, or null if none qualify
     */
    protected NetworkNode findNearestInRange(List<NetworkNode> candidates) {
        NetworkNode nearest = null;
        int bestDistance = Integer.MAX_VALUE;
        for (NetworkNode candidate : candidates) {
            if (candidate == this || !isInScanRange(candidate)) {
                continue;
            }
            int distance = stepDistanceTo(candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    /**
     * Steps one cell toward the given coordinates, staying inside the grid.
     * Each axis moves on its own so the agent can slide along edges.
     */
    protected void moveToward(int targetRow, int targetCol, int maxRows, int maxCols) {
        int dRow = 0;
        if (targetRow > this.row) {
            dRow = 1;
        } else if (targetRow < this.row) {
            dRow = -1;
        }

        int dCol = 0;
        if (targetCol > this.col) {
            dCol = 1;
        } else if (targetCol < this.col) {
            dCol = -1;
        }

        int newRow = this.row + dRow;
        int newCol = this.col + dCol;
        if (newRow >= 0 && newRow < maxRows) {
            this.row = newRow;
        }
        if (newCol >= 0 && newCol < maxCols) {
            this.col = newCol;
        }
    }

    /**
     * Rolls a randomized damage amount around the given base (a spread of half the
     * base). This keeps combat from being perfectly deterministic, so two evenly
     * matched fighters won't always trade fatal blows on the same tick.
     *
     * @param baseDamage the agent's normal damage
     * @return a damage value in the range [base - base/2, base + base/2]
     */
    protected int rollDamage(int baseDamage) {
        Random rand = new Random();
        int variance = baseDamage / 2;
        int min = baseDamage - variance;
        return min + rand.nextInt(2 * variance + 1);
    }

    /**
     * Takes a single random step (-1, 0, or +1 per axis), staying inside the grid.
     */
    protected void randomWalk(int maxRows, int maxCols) {
        Random rand = new Random();
        int newRow = this.row + rand.nextInt(3) - 1;
        int newCol = this.col + rand.nextInt(3) - 1;
        if (newRow >= 0 && newRow < maxRows) {
            this.row = newRow;
        }
        if (newCol >= 0 && newCol < maxCols) {
            this.col = newCol;
        }
    }
}
