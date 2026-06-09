package simulation;

import base.NetworkNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Collects the damage and repair queued by agents during the act phase, then
 * applies it all at once. Adding up amounts per target before applying keeps
 * combat from being biased due to load order.
 */
public class CombatResolver {

    private final Map<NetworkNode, Integer> pendingDamage = new HashMap<>();
    private final Map<NetworkNode, Integer> pendingRepair = new HashMap<>();

    /** Adds damage against a target for this tick. */
    public void queueDamage(NetworkNode target, int amount) {
        if (pendingDamage.containsKey(target)) {
            pendingDamage.put(target, pendingDamage.get(target) + amount);
        } else {
            pendingDamage.put(target, amount);
        }
    }

    /** Adds repair against a target for this tick. */
    public void queueRepair(NetworkNode target, int amount) {
        if (pendingRepair.containsKey(target)) {
            pendingRepair.put(target, pendingRepair.get(target) + amount);
        } else {
            pendingRepair.put(target, amount);
        }
    }

    /**
     * Applies all queued combat: damage first, then repair. Clears the
     * being-repaired flag on any node that gets fully restored.
     */
    public void apply() {
        for (Map.Entry<NetworkNode, Integer> entry : pendingDamage.entrySet()) {
            entry.getKey().infect(entry.getValue());
        }
        for (Map.Entry<NetworkNode, Integer> entry : pendingRepair.entrySet()) {
            boolean fullyRestored = entry.getKey().repair(entry.getValue());
            if (fullyRestored) {
                entry.getKey().setBeingRepaired(false);
            }
        }
        pendingDamage.clear();
        pendingRepair.clear();
    }
}
