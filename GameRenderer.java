package p;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

// Draws the maze, characters, HUD, and game status screens.
final class GameRenderer {
    private static final Color WALL_PIXEL_DARK = new Color(163, 126, 216);
    private static final Color WALL_PIXEL_HIGHLIGHT = new Color(235, 218, 255);
    private static final int WALL_PIXEL_SIZE = 6;

    private final Model2 game;

    // Keep access to the game data that needs to be drawn.
    GameRenderer(Model2 game) {
        this.game = game;
    }

    // Scale the game to fit the window, then draw the current game state.
    void paint(Graphics source) {
        Graphics2D graphics = (Graphics2D) source.create();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

        double widthScale = (double) game.getWidth() / game.panelSize.width;
        double heightScale = (double) game.getHeight() / game.panelSize.height;
        double scale = Math.min(widthScale, heightScale);
        int scaledWidth = (int) (game.panelSize.width * scale);
        int scaledHeight = (int) (game.panelSize.height * scale);
        graphics.translate((game.getWidth() - scaledWidth) / 2,
                (game.getHeight() - scaledHeight) / 2);
        graphics.scale(scale, scale);

        graphics.setColor(GameConstants.BACKGROUND_COLOR);
        graphics.fillRect(0, 0, game.panelSize.width, game.panelSize.height);
        drawMaze(graphics, MazeLayouts.getLayout(game.state.level));

        if (game.state.showLevelBanner) {
            drawLevelBanner(graphics);
        }
        drawScore(graphics);

        if (game.state.inGame) {
            drawPacman(graphics);
            drawGhosts(graphics);
        } else if (game.state.gameWon) {
            drawWinScreen(graphics);
        } else if (game.state.lives == 0) {
            drawGameOver(graphics);
        } else {
            drawIntroScreen(graphics);
        }

        Toolkit.getDefaultToolkit().sync();
        graphics.dispose();

        Graphics2D frameGraphics = (Graphics2D) source.create();
        frameGraphics.setColor(GameConstants.LIGHT_PURPLE);
        frameGraphics.setStroke(new BasicStroke(5));
        frameGraphics.drawRect(2, 2, game.getWidth() - 5, game.getHeight() - 5);
        frameGraphics.dispose();
    }

    private void drawGhost(Graphics2D graphics, int x, int y) {
        graphics.drawImage(game.assets.ghost, x, y, game);
    }

    private void drawGhosts(Graphics2D graphics) {
        for (int ghostIndex = 0; ghostIndex < game.characters.ghostCount; ghostIndex++) {
            int x = game.characters.ghostX[ghostIndex] + 1;
            int y = game.characters.ghostY[ghostIndex] + GameConstants.MAZE_TOP_OFFSET + 1;
            drawGhost(graphics, x, y);
        }
    }

    private void drawIntroScreen(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.drawString("Press SPACE to Start The Game",
                GameConstants.SCREEN_SIZE / 6, GameConstants.MAZE_TOP_OFFSET + 150);
    }

    // Draw the top and bottom HUD, including score, lives, and elapsed time.
    private void drawScore(Graphics2D graphics) {
        int bottomHudY = GameConstants.MAZE_TOP_OFFSET + GameConstants.SCREEN_SIZE;
        graphics.setColor(GameConstants.BACKGROUND_COLOR);
        graphics.fillRect(0, 0, GameConstants.SCREEN_SIZE, GameConstants.BLOCK_SIZE);
        graphics.fillRect(0, bottomHudY, GameConstants.SCREEN_SIZE, GameConstants.BLOCK_SIZE);
        graphics.setColor(GameConstants.LIGHT_PURPLE);
        graphics.drawRect(2, 1, GameConstants.SCREEN_SIZE - 5, GameConstants.BLOCK_SIZE - 3);
        graphics.drawRect(2, bottomHudY + 1,
                GameConstants.SCREEN_SIZE - 5, GameConstants.BLOCK_SIZE - 3);
        graphics.setFont(game.smallFont);
        graphics.setColor(Color.BLACK);
        graphics.drawString("level: " + game.state.level, 10, 17);

        String scoreText = "Score: " + game.state.score;
        int scoreX = GameConstants.SCREEN_SIZE - 10
                - graphics.getFontMetrics().stringWidth(scoreText);
        graphics.drawString(scoreText, scoreX, 17);
        for (int lifeIndex = 0; lifeIndex < game.state.lives; lifeIndex++) {
            graphics.drawImage(game.assets.heart, lifeIndex * 28 + 8, bottomHudY + 2, game);
        }
        graphics.drawString("Time: " + game.getElapsedTime(), 245, bottomHudY + 18);
    }

    private void drawLevelBanner(Graphics2D graphics) {
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
        graphics.setColor(new Color(255, 255, 255, 220));
        FontMetrics metrics = graphics.getFontMetrics();
        String levelText = "LEVEL " + game.state.level;
        String difficultyText = game.state.levelDifficulties[game.state.level - 1].toUpperCase();
        int levelX = (GameConstants.SCREEN_SIZE - metrics.stringWidth(levelText)) / 2;
        int difficultyX = (GameConstants.SCREEN_SIZE - metrics.stringWidth(difficultyText)) / 2;
        int levelY = GameConstants.MAZE_TOP_OFFSET + GameConstants.SCREEN_SIZE / 2
                - metrics.getHeight() / 2;
        graphics.drawString(levelText, levelX, levelY);
        graphics.drawString(difficultyText, difficultyX, levelY + metrics.getHeight());
    }

    void drawPacman(Graphics2D graphics) {
        int directionX = game.characters.pacmanDirectionX;
        int directionY = game.characters.pacmanDirectionY;
        if (directionX == 0) {
            directionX = game.characters.requestedDirectionX;
        }
        if (directionY == 0) {
            directionY = game.characters.requestedDirectionY;
        }

        int x = game.characters.pacmanX + 1;
        int y = game.characters.pacmanY + GameConstants.MAZE_TOP_OFFSET + 1;
        if (directionX == -1) {
            graphics.drawImage(game.assets.left, x, y, game);
        } else if (directionX == 1) {
            graphics.drawImage(game.assets.right, x, y, game);
        } else if (directionY == -1) {
            graphics.drawImage(game.assets.up, x, y, game);
        } else {
            graphics.drawImage(game.assets.down, x, y, game);
        }
    }

    // Visit each maze cell and draw its walls and collectible.
    private void drawMaze(Graphics2D graphics, short[] mazeLayout) {
        graphics.setColor(GameConstants.LIGHT_PURPLE);
        graphics.setStroke(new BasicStroke(5));
        int mazeCellIndex = 0;
        for (int mazeY = 0; mazeY < GameConstants.SCREEN_SIZE;
             mazeY += GameConstants.BLOCK_SIZE) {
            for (int mazeX = 0; mazeX < GameConstants.SCREEN_SIZE;
                 mazeX += GameConstants.BLOCK_SIZE) {
                drawMazeTile(graphics, mazeLayout, mazeCellIndex, mazeX,
                        mazeY + GameConstants.MAZE_TOP_OFFSET);
                mazeCellIndex++;
            }
        }
    }

    private void drawMazeTile(Graphics2D graphics, short[] mazeLayout, int index, int x, int y) {
        if (mazeLayout[index] == 0) {
            graphics.fillRect(x, y, GameConstants.BLOCK_SIZE, GameConstants.BLOCK_SIZE);
        }
        short cell = game.state.screenData[index];
        drawMazeWalls(graphics, cell, x, y);
        if ((cell & 16) != 0) {
            graphics.drawImage(game.assets.strawberry, x + 3, y + 3, game);
        }
    }

    // Wall bits in a cell decide which of its four sides should be drawn.
    private void drawMazeWalls(Graphics2D graphics, short cell, int x, int y) {
        if ((cell & 1) != 0) {
            drawPixelWall(graphics, x, y, false);
        }
        if ((cell & 2) != 0) {
            drawPixelWall(graphics, x, y, true);
        }
        if ((cell & 4) != 0) {
            drawPixelWall(graphics, x + GameConstants.BLOCK_SIZE - WALL_PIXEL_SIZE, y, false);
        }
        if ((cell & 8) != 0) {
            drawPixelWall(graphics, x, y + GameConstants.BLOCK_SIZE - WALL_PIXEL_SIZE, true);
        }
    }

    private void drawPixelWall(Graphics2D graphics, int x, int y, boolean horizontal) {
        for (int offset = 0; offset < GameConstants.BLOCK_SIZE; offset += WALL_PIXEL_SIZE) {
            graphics.setColor((offset / WALL_PIXEL_SIZE) % 2 == 0
                    ? GameConstants.LIGHT_PURPLE : WALL_PIXEL_DARK);
            if (horizontal) {
                graphics.fillRect(x + offset, y, WALL_PIXEL_SIZE, WALL_PIXEL_SIZE);
            } else {
                graphics.fillRect(x, y + offset, WALL_PIXEL_SIZE, WALL_PIXEL_SIZE);
            }
            graphics.setColor(WALL_PIXEL_HIGHLIGHT);
            if (horizontal) {
                graphics.fillRect(x + offset, y, 2, 2);
            } else {
                graphics.fillRect(x, y + offset, 2, 2);
            }
        }
    }

    private void drawGameOver(Graphics2D graphics) {
        Failed.draw(graphics, game);
    }

    private void drawWinScreen(Graphics2D graphics) {
        Congratulation.draw(graphics, game);
    }
}
