import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FlappyBirdGame extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final int GRAVITY = 1;
    private static final int JUMP_POWER = 10;
    private static final int PIPE_GAP = 200;
    private static final int PIPE_DELAY = 120;

    private Bird bird;
    private PipeManager pipeManager;
    private int score;
    private boolean gameover;

    private Timer gameTimer;

    public FlappyBirdGame() {
        setTitle("Flappy Bird");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        bird = new Bird(100, HEIGHT / 2);
        pipeManager = new PipeManager();
        score = 0;
        gameover = false;

        gameTimer = new Timer(20, new GameUpdateListener());
        gameTimer.start();

        addKeyListener(new BirdKeyListener());
        setFocusable(true);
    }

    public void update() {
        if (gameover) {
            return;
        }

        bird.update();

        if (bird.getY() + bird.getHeight() >= HEIGHT || pipeManager.checkCollision(bird)) {
            gameover = true;
        }

        pipeManager.update();

        if (pipeManager.isPipePassed(bird.getX())) {
            score++;
        }
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        bird.paint(g);
        pipeManager.paint(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Score: " + score, 10, 30);

        if (gameover) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics metrics = g.getFontMetrics();
            String gameoverText = "Game Over";
            int textX = (WIDTH - metrics.stringWidth(gameoverText)) / 2;
            int textY = (HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(gameoverText, textX, textY);
        }
    }

    private class Bird {
        private int x;
        private int y;
        private int velocity;

        private static final int WIDTH = 50;
        private static final int HEIGHT = 50;

        public Bird(int x, int y) {
            this.x = x;
            this.y = y;
            velocity = 0;
        }

        public void update() {
            if (gameover) {
                return;
            }

            velocity += GRAVITY;
            y += velocity;
        }

        public void paint(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, WIDTH, HEIGHT);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getHeight() {
            return HEIGHT;
        }
    }

    private class Pipe {
        private int x;
        private int height;
        private static final int WIDTH = 100;
        private static final int PIPE_VELOCITY = 3;

        public Pipe(int x, int height) {
            this.x = x;
            this.height = height;
        }

        public void update() {
            if (gameover) {
                return;
            }

            x -= PIPE_VELOCITY;
        }

        public void paint(Graphics g) {
            g.setColor(Color.GREEN);
            g.fillRect(x, 0, WIDTH, height);
            g.fillRect(x, height + PIPE_GAP, WIDTH, HEIGHT - height - PIPE_GAP);
        }

        public int getX() {
            return x;
        }

        public int getHeight() {
            return height;
        }
    }

    private class PipeManager {
        private java.util.List<Pipe> pipes;

        public PipeManager() {
            pipes = new java.util.ArrayList<>();
        }

        public void update() {
            if (gameover) {
                return;
            }

            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                pipe.update();

                if (pipe.getX() + Pipe.WIDTH < 0) {
                    pipes.remove(i);
                    i--;
                }
            }

            if (pipes.size() == 0 || WIDTH - pipes.get(pipes.size() - 1).getX() >= PIPE_DELAY) {
                int pipeHeight = random.nextInt(HEIGHT - PIPE_GAP - 100);
                pipes.add(new Pipe(WIDTH, pipeHeight));
            }
        }

        public void paint(Graphics g) {
            for (Pipe pipe : pipes) {
                pipe.paint(g);
            }
        }

        public boolean checkCollision(Bird bird) {
            for (Pipe pipe : pipes) {
                if (bird.getX() + bird.getWidth() > pipe.getX() && bird.getX() < pipe.getX() + Pipe.WIDTH) {
                    if (bird.getY() < pipe.getHeight() || bird.getY() + bird.getHeight() > pipe.getHeight() + PIPE_GAP) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isPipePassed(int x) {
            for (Pipe pipe : pipes) {
                if (x == pipe.getX() + Pipe.WIDTH) {
                    return true;
                }
            }
            return false;
        }
    }

    private class GameUpdateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }
    }

    private class BirdKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                bird.velocity = -JUMP_POWER;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FlappyBirdGame game = new FlappyBirdGame();
                game.setVisible(true);
            }
        });
    }
}
