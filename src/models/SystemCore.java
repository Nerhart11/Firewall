package models;

import base.DataCell;

import java.awt.*;

/**
 * Critical target node. The core occupies four grid cells but shares a single
 * large health pool through {@link CoreHealth}: damage to any one cell drains the
 * whole core, and the core is only considered destroyed once that shared pool is
 * exhausted. All four cells therefore corrupt and recover together.
 */
public class SystemCore extends DataCell {

    private static final Color BASE_COLOR = new Color(255, 215, 0);
    private static final Color DEAD_COLOR = new Color(65, 50, 0);

    /** Combined health for the whole core -- larger than anything else in the sim. */
    public static final int CORE_MAX_HP = 2000;
    public static final int CORE_REPAIR_THRESHOLD = 1200;

    private final CoreHealth health;

    /**
     * @param row grid row position
     * @param col grid column position
     * @param health the shared pool every cell of this core delegates to
     */
    public SystemCore(int row, int col, CoreHealth health) {
        super(row, col, health.getMaxHP(), CORE_REPAIR_THRESHOLD);
        this.health = health;
    }

    /** @return the shared health pool backing this core cell. */
    public CoreHealth getHealth() { return health; }

    // All health-bearing behavior is delegated to the shared pool so the four
    // core cells act as one unit.

    @Override
    public void infect(int damage) { health.infect(damage); }

    @Override
    public boolean repair(int repairSpeed) { return health.repair(repairSpeed); }

    @Override
    public boolean isCorrupted() { return health.isCorrupted(); }

    @Override
    public boolean needsRepair() { return health.needsRepair(); }

    @Override
    public boolean isBeingRepaired() { return health.isBeingRepaired(); }

    @Override
    public void setBeingRepaired(boolean status) { health.setBeingRepaired(status); }

    @Override
    public Color getColor() {
        int hp = health.getCurrentHP();
        int max = health.getMaxHP();
        int r = DEAD_COLOR.getRed() + (BASE_COLOR.getRed() - DEAD_COLOR.getRed()) * hp / max;
        int g = DEAD_COLOR.getGreen() + (BASE_COLOR.getGreen() - DEAD_COLOR.getGreen()) * hp / max;
        int b = DEAD_COLOR.getBlue() + (BASE_COLOR.getBlue() - DEAD_COLOR.getBlue()) * hp / max;
        return new Color(r, g, b);
    }

    @Override
    public String getTypeName() {
        return "System Core";
    }

    @Override
    public String toString() {
        return "C";
    }

    /**
     * A single health pool shared by every cell of one System Core. Mirrors the
     * damage/repair rules of a normal node but exists once for the whole core, so
     * the four cells share one total health rather than four separate bars.
     */
    public static class CoreHealth {

        private final int maxHP;
        private final int repairThreshold;
        private int currentHP;
        private int repairProgress;
        private boolean corrupted;
        private boolean beingRepaired;

        public CoreHealth(int maxHP, int repairThreshold) {
            this.maxHP = maxHP;
            this.repairThreshold = repairThreshold;
            this.currentHP = maxHP;
        }

        /** Applies damage to the shared pool, corrupting the core if it hits zero. */
        void infect(int damage) {
            currentHP -= damage;
            if (currentHP <= 0) {
                currentHP = 0;
                corrupted = true;
                repairProgress = 0;
            }
        }

        /**
         * Progresses repair on the shared pool. A corrupted core must accumulate
         * repairThreshold worth of progress before it revives; a merely damaged core
         * has its health topped back up.
         * @return true if the core just reached full health
         */
        boolean repair(int repairSpeed) {
            if (corrupted) {
                repairProgress += repairSpeed;
                if (repairProgress >= repairThreshold) {
                    corrupted = false;
                    currentHP = maxHP;
                    repairProgress = 0;
                    return true;
                }
                return false;
            }
            currentHP += repairSpeed;
            if (currentHP >= maxHP) {
                currentHP = maxHP;
                return true;
            }
            return false;
        }

        public boolean isCorrupted() { return corrupted; }
        boolean needsRepair() { return corrupted || currentHP < maxHP; }
        boolean isBeingRepaired() { return beingRepaired; }
        void setBeingRepaired(boolean status) { this.beingRepaired = status; }

        int getCurrentHP() { return currentHP; }
        int getMaxHP() { return maxHP; }
    }
}
