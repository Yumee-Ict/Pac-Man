package p;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

// Draws the completion screen after the player wins.
public final class Congratulation {
    static void draw(Graphics2D graphics, Model2 game) {
        graphics.setColor(new Color(0, 0, 0, 210));
        graphics.fillRect(0, GameConstants.MAZE_TOP_OFFSET,
                GameConstants.SCREEN_SIZE, GameConstants.SCREEN_SIZE);

        graphics.setColor(Color.YELLOW);
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        drawCenteredText(graphics, "CONGRATULATIONS!", GameConstants.MAZE_TOP_OFFSET + 135);

        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        drawCenteredText(graphics, "YOU WON!", GameConstants.MAZE_TOP_OFFSET + 170);
        drawCenteredText(graphics, "Score: " + game.state.score,
                GameConstants.MAZE_TOP_OFFSET + 210);
        drawCenteredText(graphics, "Time: " + game.getElapsedTime(),
                GameConstants.MAZE_TOP_OFFSET + 240);

        graphics.setFont(game.smallFont);
        drawCenteredText(graphics, "Press SPACE to play again",
                GameConstants.MAZE_TOP_OFFSET + 280);
    }

    private static void drawCenteredText(Graphics2D graphics, String text, int baselineY) {
        FontMetrics metrics = graphics.getFontMetrics();
        int textX = (GameConstants.SCREEN_SIZE - metrics.stringWidth(text)) / 2;
        graphics.drawString(text, textX, baselineY);
    }

    private Congratulation() {
    }
}
