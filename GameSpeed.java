package p;

// Calculates movement speeds for Pac-Man and ghosts at each level.
final class GameSpeed {
    private static final double[] GHOST_SPEEDS = {4.0, 4.5, 5.0, 5.5, 6.0};

    private GameSpeed() {
    }

    // Use the nearest supported level speed if the level is outside the table.
    static double getGhostSpeed(int level) {
        int levelIndex = level - 1;
        if (levelIndex < 0) {
            levelIndex = 0;
        } else if (levelIndex >= GHOST_SPEEDS.length) {
            levelIndex = GHOST_SPEEDS.length - 1;
        }
        return GHOST_SPEEDS[levelIndex];
    }

    // Choose a Pac-Man speed that lands exactly on maze-cell boundaries.
    static int getPacmanSpeed(int level) {
        int pacmanSpeed = (int) Math.ceil(getGhostSpeed(level)) + 1;
        while (GameConstants.BLOCK_SIZE % pacmanSpeed != 0) {
            pacmanSpeed++;
        }
        return pacmanSpeed;
    }
}
