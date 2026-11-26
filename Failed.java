package p;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

// Holds the message shown when the player runs out of lives.
final class Failed {
    static final String GAME_OVER_MESSAGE = "GAME OVER";

    static void draw(Graphics2D graphics, Model2 game) {
        graphics.setColor(Color.RED);
        graphics.setFont(game.smallFont);
        drawCenteredText(graphics, GAME_OVER_MESSAGE,
                GameConstants.MAZE_TOP_OFFSET + GameConstants.SCREEN_SIZE / 2 - 18);
        drawCenteredText(graphics, "Score = " + game.state.score,
                GameConstants.MAZE_TOP_OFFSET + GameConstants.SCREEN_SIZE / 2 + 4);
        drawCenteredText(graphics, "press SPACE to play again",
                GameConstants.MAZE_TOP_OFFSET + GameConstants.SCREEN_SIZE / 2 + 26);
    }

    private static void drawCenteredText(Graphics2D graphics, String text, int baselineY) {
        FontMetrics metrics = graphics.getFontMetrics();
        int textX = (GameConstants.SCREEN_SIZE - metrics.stringWidth(text)) / 2;
        graphics.drawString(text, textX, baselineY);
    }

    private Failed() {
    }
}
