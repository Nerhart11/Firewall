package models;

import base.ActiveAgent;
import base.NetworkNode;
import simulation.CombatResolver;
import simulation.SimulationConfig;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Defensive agent that scans the grid for malware and attacks it.
 */
public class AntivirusSentinel extends ActiveAgent
{

    private static final Color BASE_COLOR = new Color(0, 170, 255);
    private static final Color DEAD_COLOR = new Color(0, 35, 55);

    private int damage;

    public AntivirusSentinel(int row, int col, int damage, int scanRange)
    {
        super(row, col, 150, 120, scanRange);
        this.damage = damage;
    }

    /**
     * Act phase: target the nearest live malware in range and queue damage if adjacent.
     */
    @Override
    public void action(NetworkNode[][] grid, List<ActiveAgent> agents,
                       SimulationConfig config, CombatResolver resolver)
    {
        List<NetworkNode> malware = new ArrayList<>();
        for (ActiveAgent agent : agents) {
            if (agent instanceof MalwareStrain && !agent.isCorrupted()) {
                malware.add(agent);
            }
        }

        currentTarget = findNearestInRange(malware);
        if (currentTarget != null && stepDistanceTo(currentTarget) <= 1) {
            resolver.queueDamage(currentTarget, damage);
        }
    }

    /**
     * Move phase: pursue a live malware target, else patrol randomly.
     * A killed target is corrupted, so the sentinel reverts to patrolling.
     */
    @Override
    public void move(int maxRows, int maxCols)
    {
        if (currentTarget == null || currentTarget.isCorrupted()) {
            randomWalk(maxRows, maxCols);
        } else {
            moveToward(currentTarget.getRow(), currentTarget.getCol(), maxRows, maxCols);
        }
    }

    @Override
    public Color getColor()
    {
        return getHealthColor(BASE_COLOR, DEAD_COLOR);
    }

    @Override
    public String getTypeName()
    {
        return "Antivirus Sentinel";
    }

    @Override
    public String toString()
    {
        return "S";
    }


}
