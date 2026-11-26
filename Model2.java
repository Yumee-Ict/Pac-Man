package p;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

// Coordinates game components and owns the Swing update timer.
public class Model2 extends JPanel implements ActionListener {
    private static final int TIMER_DELAY_MILLIS = 40;

    public final Dimension panelSize;
    public final Font smallFont = new Font(Font.MONOSPACED, Font.BOLD, 14);

    private final Timer timer;
    private final transient SoundManager soundManager = new SoundManager();
    private final transient CharacterController characterController;
    private final transient LevelManager levelManager;
    private final transient LeaderboardDialog leaderboardDialog;

    public transient GameState state;
    public transient CharacterPositions characters;
    public transient GameAssets assets;
    public transient GameRenderer renderer;

    public Model2() {
        state = new GameState();
        characters = new CharacterPositions();
        assets = new GameAssets();
        panelSize = new Dimension(400,
                GameConstants.SCREEN_SIZE + 2 * GameConstants.BLOCK_SIZE);
        setPreferredSize(panelSize);
        setBackground(GameConstants.BACKGROUND_COLOR);
        setFocusable(true);
        addKeyListener(new GameKeyHandler(this));

        renderer = new GameRenderer(this);
        characterController = new CharacterController(this);
        levelManager = new LevelManager(this);
        leaderboardDialog = new LeaderboardDialog(this);

        timer = new Timer(TIMER_DELAY_MILLIS, this);
        timer.start();
        levelManager.startNewGame();
    }

    private void updateGame() {
        if (state.dying) {
            death();
            return;
        }

        characterController.movePacman();
        characterController.moveGhosts();
        checkMaze();
    }

    private void checkMaze() {
        for (short cell : state.screenData) {
            if ((cell & 48) != 0) {
                return;
            }
        }

        if (state.level == state.levelDifficulties.length) {
            state.gameWon = true;
            state.inGame = false;
            state.gameCompletedAtNanos = System.nanoTime();
            state.showLevelBanner = false;
            soundManager.playCongratulations();
            SwingUtilities.invokeLater(leaderboardDialog::promptForEntry);
            return;
        }

        startLevelBanner();
        levelManager.loadLevel(state.level + 1);
    }

    private void death() {
        state.lives--;
        if (state.lives == 0) {
            state.inGame = false;
            state.dying = false;
            state.gameCompletedAtNanos = System.nanoTime();
            soundManager.playFailed();
        } else {
            levelManager.resetPositions();
        }
    }

    public String getElapsedTime() {
        if (state.gameStartedAtNanos == 0) {
            return "00:00";
        }

        long endTime;
        if (state.gameCompletedAtNanos != 0) {
            endTime = state.gameCompletedAtNanos;
        } else if (state.inGame) {
            endTime = System.nanoTime();
        } else {
            endTime = state.gameStartedAtNanos;
        }
        long elapsedSeconds = (endTime - state.gameStartedAtNanos) / 1_000_000_000L;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        String minuteText = minutes < 10 ? "0" + minutes : Long.toString(minutes);
        String secondText = seconds < 10 ? "0" + seconds : Long.toString(seconds);
        return minuteText + ":" + secondText;
    }

    public void showLeaderboard() {
        leaderboardDialog.showLeaderboard();
    }

    private void startLevelBanner() {
        state.showLevelBanner = true;
        state.levelBannerTimer = 1000;
    }

    public void subtractGhost() {
        if (characters.ghostCount > 0) {
            characters.ghostCount--;
        }
    }

    public void addGhost() {
        if (characters.ghostCount < GameConstants.MAX_GHOSTS) {
            characters.ghostCount++;
        }
        levelManager.resetPositions();
    }

    void nextLevel() {
        if (state.level >= state.levelDifficulties.length) {
            return;
        }
        levelManager.loadLevel(state.level + 1);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        renderer.paint(graphics);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (state.showLevelBanner) {
            state.levelBannerTimer -= TIMER_DELAY_MILLIS;
            if (state.levelBannerTimer <= 0) {
                state.showLevelBanner = false;
                state.levelBannerTimer = 0;
            }
        }
        if (state.inGame) {
            updateGame();
        }
        repaint();
    }

    public boolean isInGame() {
        return state.inGame;
    }

    public void requestDirection(int directionX, int directionY) {
        characters.requestedDirectionX = directionX;
        characters.requestedDirectionY = directionY;
    }

    public boolean isTimerRunning() {
        return timer.isRunning();
    }

    public void pauseGame() {
        state.inGame = false;
    }

    public void startGame() {
        state.inGame = true;
        levelManager.startNewGame();
        state.gameStartedAtNanos = System.nanoTime();
        startLevelBanner();
        soundManager.stopEndSounds();
        soundManager.startGameMusic();
    }
}
