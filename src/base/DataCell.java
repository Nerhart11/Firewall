package base;

/**
 * Abstract class for static nodes on the grid that can be targeted by malware.
 */
public abstract class DataCell extends NetworkNode{

    /**
     * @param row grid row position
     * @param col grid column position
     * @param maxHP maximum health before corruption
     * @param repairThreshold total repair progress needed to restore
     */
    public DataCell(int row, int col, int maxHP, int repairThreshold) {
        super(row, col, maxHP, repairThreshold);
    }
}
