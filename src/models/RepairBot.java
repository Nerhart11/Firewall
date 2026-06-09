package models;

import base.ActiveAgent;
import base.NetworkNode;
import simulation.CombatResolver;
import simulation.SimulationConfig;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Specialized agent that scans for corrupted nodes and restores them.
 */
public class RepairBot extends ActiveAgent {

    private static final Color BASE_COLOR = new Color(170, 221, 255);
    private static final Color DEAD_COLOR = new Color(35, 45, 55);

    private int repairPower;

    /**
     * @param row grid row position
     * @param col grid column position
     * @param repairPower amount of repair progress applied per tick
     * @param scanRange how far this bot can detect corrupted nodes
     */
    public RepairBot(int row, int col, int repairPower, int scanRange) {
        super(row, col, 100, 5, scanRange);
        this.repairPower = repairPower;
        this.currentTarget = null;
    }

    public int getRepairPower() { return repairPower; }
    public void setRepairPower(int repairPower) { this.repairPower = repairPower; }

    /**
     * Act phase: pick a target that needs repair (damaged or corrupted) by priority
     * (Core &gt; Agents &gt; Grid), nearest within the first non-empty category, and
     * queue repair if adjacent.
     */
    @Override
    public void action(NetworkNode[][] grid, List<ActiveAgent> agents,
                       SimulationConfig config, CombatResolver resolver) {
        currentTarget = selectTarget(grid, agents);
        if (currentTarget != null && stepDistanceTo(currentTarget) <= 1) {
            currentTarget.setBeingRepaired(true);
            resolver.queueRepair(currentTarget, repairPower);
        }
    }

    /**
     * Chooses a target needing repair following the Core &gt; Agents &gt; Grid
     * priority. The first category with a candidate in range wins; ties broken by
     * distance.
     */
    private NetworkNode selectTarget(NetworkNode[][] grid, List<ActiveAgent> agents) {
        List<NetworkNode> cores = new ArrayList<>();
        List<NetworkNode> dataCells = new ArrayList<>();

        int minRow = Math.max(0, getRow() - getScanRange());
        int maxRow = Math.min(grid.length - 1, getRow() + getScanRange());
        for (int r = minRow; r <= maxRow; r++) {
            int minCol = Math.max(0, getCol() - getScanRange());
            int maxCol = Math.min(grid[r].length - 1, getCol() + getScanRange());
            for (int c = minCol; c <= maxCol; c++) {
                NetworkNode node = grid[r][c];
                if (node == null || !node.needsRepair()) {
                    continue;
                }
                if (node instanceof SystemCore) {
                    cores.add(node);
                } else {
                    dataCells.add(node);
                }
            }
        }

        NetworkNode core = findNearestInRange(cores);
        if (core != null) {
            return core;
        }

        List<NetworkNode> downedAgents = new ArrayList<>();
        for (ActiveAgent agent : agents) {
            if (agent != this && !(agent instanceof MalwareStrain) && agent.needsRepair()) {
                downedAgents.add(agent);
            }
        }
        NetworkNode agent = findNearestInRange(downedAgents);
        if (agent != null) {
            return agent;
        }

        return findNearestInRange(dataCells);
    }

    /**
     * Move phase: head toward a target that still needs repair, else roam randomly.
     * Once a target is back to full health the bot reverts to roaming.
     */
    @Override
    public void move(int maxRows, int maxCols) {
        if (currentTarget == null || !currentTarget.needsRepair()) {
            randomWalk(maxRows, maxCols);
        } else {
            moveToward(currentTarget.getRow(), currentTarget.getCol(), maxRows, maxCols);
        }
    }

    @Override
    public Color getColor() {
        return getHealthColor(BASE_COLOR, DEAD_COLOR);
    }

    @Override
    public String getTypeName() {
        return "Repair Bot";
    }

    @Override
    public String toString() {
        return "R";
    }
}
