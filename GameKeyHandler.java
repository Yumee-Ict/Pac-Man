package p;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Converts keyboard presses into game movement and actions.
final class GameKeyHandler extends KeyAdapter {
    private final Model2 game;

    // Keep a reference to the game so key presses can update it.
    GameKeyHandler(Model2 game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.VK_N) {
            game.nextLevel();
        } else if (keyCode == KeyEvent.VK_S) {
            game.subtractGhost();
        } else if (keyCode == KeyEvent.VK_A) {
            game.addGhost();
        }

        if (game.isInGame()) {
            if (keyCode == KeyEvent.VK_LEFT) {
                game.requestDirection(-1, 0);
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                game.requestDirection(1, 0);
            } else if (keyCode == KeyEvent.VK_UP) {
                game.requestDirection(0, -1);
            } else if (keyCode == KeyEvent.VK_DOWN) {
                game.requestDirection(0, 1);
            } else if (keyCode == KeyEvent.VK_ESCAPE && game.isTimerRunning()) {
                game.pauseGame();
            }
        } else if (keyCode == KeyEvent.VK_SPACE) {
            game.startGame();
        }
    }
}
