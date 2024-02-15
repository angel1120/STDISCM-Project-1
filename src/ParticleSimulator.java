import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticleSimulator extends JPanel implements ActionListener {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final int PARTICLE_RADIUS = 2;
    private static final int FPS_UPDATE_INTERVAL = 500; // Update fps every 0.5 seconds
    private static final int NUM_THREADS = 8; // Number of worker threads for load balancing

    private List<Particle> particles;
    private List<Wall> walls;
    private int particleCount;
    private int fps;
    private int frameCount;
    private long lastFpsUpdateTime;
    private ExecutorService executor;

    public ParticleSimulator() {
        particles = new ArrayList<>();
        walls = new ArrayList<>();
        particleCount = 0;
        fps = 0;
        frameCount = 0;
        lastFpsUpdateTime = System.currentTimeMillis();
        executor = Executors.newFixedThreadPool(NUM_THREADS);

        // Adjust frame rate to 60 fps
        Timer timer = new Timer(1000 / 60, this);
        timer.start();

        Timer fpsTimer = new Timer(FPS_UPDATE_INTERVAL, e -> {
            long currentTime = System.currentTimeMillis();
            fps = (int) (frameCount * 1000 / (currentTime - lastFpsUpdateTime));
            lastFpsUpdateTime = currentTime;
            frameCount = 0;
        });
        fpsTimer.start();

        JFrame mainFrame = new JFrame("Particle Simulator");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(WINDOW_WIDTH+30, WINDOW_HEIGHT+50);
        mainFrame.setResizable(false);
        mainFrame.add(this);
        mainFrame.setVisible(true);

        addFeatures();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        // Particle
        for (Particle particle : particles) {
            g.fillOval((int) particle.getX(), (int) particle.getY(), 2 * PARTICLE_RADIUS, 2 * PARTICLE_RADIUS);
        }

        // Wall
        for (Wall wall : walls) {
            g.drawLine((int) wall.getX1(), (int) wall.getY1(), (int) wall.getX2(), (int) wall.getY2());
        }

        // Particle count and fps
        g.drawString("Particles: " + particleCount, 10, 20);
        g.drawString("FPS: " + fps, 10, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        executor.execute(() -> {
            for (Particle particle : particles) {
                particle.update(WINDOW_WIDTH, WINDOW_HEIGHT, walls);
            }
        });
        frameCount++;
        repaint();
    }

    private void addFeatures() {
        JFrame addFeaturesFrame = new JFrame("Add Particle/Wall");
        addFeaturesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFeaturesFrame.setSize(500, 500);
        addFeaturesFrame.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        JLabel addParticleLabel = new JLabel("Particle");
        addParticleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(addParticleLabel, gbc);

        JLabel instructions = new JLabel("Please choose the input batch type");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(instructions, gbc);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(10), gbc);

    // Uniformly spaced particles between start and end points:
        final boolean[] isPoints = {false};

        JButton startEndPoints = new JButton("Input start and end points");
        startEndPoints.setPreferredSize(new Dimension(210, startEndPoints.getPreferredSize().height));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(startEndPoints, gbc);

        JLabel pointsLabel = new JLabel("<html>Input start point (X1, Y1)<br>and end point (X2, Y2)</html>");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(pointsLabel, gbc);
        pointsLabel.setVisible(false);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(5), gbc);

        JLabel xStartLabel = new JLabel("X1 Position:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(xStartLabel, gbc);
        xStartLabel.setVisible(false);

        JTextField xStartField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(xStartField, gbc);
        xStartField.setVisible(false);

        JLabel yStartLabel = new JLabel("Y1 Position:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(yStartLabel, gbc);
        yStartLabel.setVisible(false);

        JTextField yStartField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(yStartField, gbc);
        yStartField.setVisible(false);

        JLabel xEndLabel = new JLabel("X2 Position:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(xEndLabel, gbc);
        xEndLabel.setVisible(false);

        JTextField xEndField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(xEndField, gbc);
        xEndField.setVisible(false);

        JLabel yEndLabel = new JLabel("Y2 Position:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(yEndLabel, gbc);
        yEndLabel.setVisible(false);

        JTextField yEndField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(yEndField, gbc);
        yEndField.setVisible(false);

    // Uniformly spaced particles between start and end angles Θ
        final boolean[] isAngles = {false};

        JButton startEndAngles = new JButton("Input start and end angles");
        startEndAngles.setPreferredSize(new Dimension(210, startEndPoints.getPreferredSize().height));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(startEndAngles, gbc);

        JLabel anglesLabel = new JLabel("<html>Input start angle and end angle</html>");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(anglesLabel, gbc);
        anglesLabel.setVisible(false);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(5), gbc);

        JLabel angleStartLabel = new JLabel("Start Angle (degrees): ");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(angleStartLabel, gbc);
        angleStartLabel.setVisible(false);

        JTextField angleStartField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(angleStartField, gbc);
        angleStartField.setVisible(false);

        JLabel angleEndLabel = new JLabel("End Angle (degrees): ");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(angleEndLabel, gbc);
        angleEndLabel.setVisible(false);

        JTextField angleEndField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(angleEndField, gbc);
        angleEndField.setVisible(false);

    // Uniform difference in velocity between start and end velocities
        final boolean[] isVelocity = {false};

        JButton startEndVelocity = new JButton("Input start and end velocities");
        startEndVelocity.setPreferredSize(new Dimension(210, startEndPoints.getPreferredSize().height));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(startEndVelocity, gbc);

        JLabel velocityLabel = new JLabel("<html>Input start velocity and end velocity</html>");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(velocityLabel, gbc);
        velocityLabel.setVisible(false);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(5), gbc);

        JLabel velocityStartLabel = new JLabel("Start Velocity (pixels/second): ");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(velocityStartLabel, gbc);
        velocityStartLabel.setVisible(false);

        JTextField velocityStartField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(velocityStartField, gbc);
        velocityStartField.setVisible(false);

        JLabel velocityEndLabel = new JLabel("End Velocity (pixels/second): ");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(velocityEndLabel, gbc);
        velocityEndLabel.setVisible(false);

        JTextField velocityEndField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(velocityEndField, gbc);
        velocityEndField.setVisible(false);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(10), gbc);

    // Particle count for uniform distribution
        JLabel countLabel = new JLabel("Number of Particles:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(countLabel, gbc);

        JTextField countField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(countField, gbc);

    // Batch Listeners
        startEndPoints.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPoints[0] = true;
                isAngles[0] = false;
                isVelocity[0] = false;

                startEndPoints.setVisible(false);
                pointsLabel.setVisible(true);
                xStartField.setVisible(true);
                xStartLabel.setVisible(true);
                yStartField.setVisible(true);
                yStartLabel.setVisible(true);
                xEndLabel.setVisible(true);
                xEndField.setVisible(true);
                yEndLabel.setVisible(true);
                yEndField.setVisible(true);

                startEndAngles.setVisible(true);
                anglesLabel.setVisible(false);
                angleStartLabel.setVisible(false);
                angleStartField.setVisible(false);
                angleEndLabel.setVisible(false);
                angleEndField.setVisible(false);
                angleStartField.setText("");
                angleEndField.setText("");

                startEndVelocity.setVisible(true);
                velocityLabel.setVisible(false);
                velocityStartLabel.setVisible(false);
                velocityStartField.setVisible(false);
                velocityEndLabel.setVisible(false);
                velocityEndField.setVisible(false);
                velocityStartField.setText("");
                velocityEndField.setText("");
            }
        });

        startEndAngles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPoints[0] = false;
                isAngles[0] = true;
                isVelocity[0] = false;

                startEndPoints.setVisible(true);
                pointsLabel.setVisible(false);
                xStartField.setVisible(false);
                xStartLabel.setVisible(false);
                yStartField.setVisible(false);
                yStartLabel.setVisible(false);
                xEndLabel.setVisible(false);
                xEndField.setVisible(false);
                yEndLabel.setVisible(false);
                yEndField.setVisible(false);
                xStartField.setText("");
                yStartField.setText("");
                xEndField.setText("");
                yEndField.setText("");

                startEndAngles.setVisible(false);
                anglesLabel.setVisible(true);
                angleStartLabel.setVisible(true);
                angleStartField.setVisible(true);
                angleEndLabel.setVisible(true);
                angleEndField.setVisible(true);

                startEndVelocity.setVisible(true);
                velocityLabel.setVisible(false);
                velocityStartLabel.setVisible(false);
                velocityStartField.setVisible(false);
                velocityEndLabel.setVisible(false);
                velocityEndField.setVisible(false);
                velocityStartField.setText("");
                velocityEndField.setText("");
            }
        });

        startEndVelocity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPoints[0] = false;
                isAngles[0] = false;
                isVelocity[0] = true;

                startEndPoints.setVisible(true);
                pointsLabel.setVisible(false);
                xStartField.setVisible(false);
                xStartLabel.setVisible(false);
                yStartField.setVisible(false);
                yStartLabel.setVisible(false);
                xEndLabel.setVisible(false);
                xEndField.setVisible(false);
                yEndLabel.setVisible(false);
                yEndField.setVisible(false);
                xStartField.setText("");
                yStartField.setText("");
                xEndField.setText("");
                yEndField.setText("");

                startEndAngles.setVisible(true);
                anglesLabel.setVisible(false);
                angleStartLabel.setVisible(false);
                angleStartField.setVisible(false);
                angleEndLabel.setVisible(false);
                angleEndField.setVisible(false);
                angleStartField.setText("");
                angleEndField.setText("");

                startEndVelocity.setVisible(false);
                velocityLabel.setVisible(true);
                velocityStartLabel.setVisible(true);
                velocityStartField.setVisible(true);
                velocityEndLabel.setVisible(true);
                velocityEndField.setVisible(true);
            }
        });

    // Add Particles
        JButton addParticleButton = new JButton("Add Particle");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(addParticleButton, gbc);

        addParticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    float x, y, angle, velocity;

                    int n = Integer.parseInt(countField.getText());
                    if (isPoints[0]) {
                        velocity = 5;
                        float x1 = Float.parseFloat(xStartField.getText());
                        float y1 = Float.parseFloat(yStartField.getText());
                        float x2 = Float.parseFloat(xEndField.getText());
                        float y2 = Float.parseFloat(yEndField.getText());

                        float dx = x2 - x1;
                        float dy = y2 - y1;

                        float distX = dx / (n - 1);
                        float distY = dy / (n - 1);

                        angle = (float) Math.atan2(dy, dx);
                        angle = (float) Math.toDegrees(angle);
                        angle = (angle + 360) % 360;

                        for (int i = 0; i < n; i++) {
                            if (i == 0)
                                particles.add(new Particle(x1, y1, angle, velocity));
                            particles.add(new Particle(x1 += distX, y1 += distY, angle, velocity));
                            particleCount++;
                        }
                    } else if (isAngles[0]) {
                        x = 640;
                        y = 360;
                        velocity = 5;
                        float angle1 = Float.parseFloat(angleStartField.getText());
                        float angle2 = Float.parseFloat(angleEndField.getText());

                        float angleDifference = angle2 - angle1;
                        float angleDist = angleDifference / (n - 1);

                        for (int i = 0; i < n; i++) {
                            if (i == 0)
                                particles.add(new Particle(x, y, angle1, velocity));
                            particles.add(new Particle(x, y, angle1 += angleDist, velocity));
                            particleCount++;
                        }
                    } else if (isVelocity[0]) {
                        x = 0;
                        y = 0;
                        angle = 45;
                        float velocity1 = Float.parseFloat(velocityStartField.getText());
                        float velocity2 = Float.parseFloat(velocityEndField.getText());

                        float velocityDifference = velocity2 - velocity1;
                        float velocityInc = velocityDifference / (n - 1);

                        for (int i = 0; i < n; i++) {
                            particles.add(new Particle(x, y, angle, velocity1 + i*velocityInc));
                            particleCount++;
                        }
                    } else {    // default
                        x = 640;
                        y = 360;
                        angle = 90;
                        velocity = 5;

                        particles.add(new Particle(x, y, angle, velocity));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter numeric values.");
                }
            }
        });

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(20), gbc);

        JLabel addWallLabel = new JLabel("Wall");
        addWallLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(addWallLabel, gbc);

        JLabel x1Label = new JLabel("X1:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(x1Label, gbc);

        JTextField x1Field = new JTextField(10);
        gbc.gridx = 1;
        panel.add(x1Field, gbc);

        JLabel y1Label = new JLabel("Y1:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(y1Label, gbc);

        JTextField y1Field = new JTextField(10);
        gbc.gridx = 1;
        panel.add(y1Field, gbc);

        JLabel x2Label = new JLabel("X2:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(x2Label, gbc);

        JTextField x2Field = new JTextField(10);
        gbc.gridx = 1;
        panel.add(x2Field, gbc);

        JLabel y2Label = new JLabel("Y2:");
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(y2Label, gbc);

        JTextField y2Field = new JTextField(10);
        gbc.gridx = 1;
        panel.add(y2Field, gbc);

        JButton addWallButton = new JButton("Add Wall");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(addWallButton, gbc);

        addWallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    float x1 = Float.parseFloat(x1Field.getText());
                    float y1 = Float.parseFloat(y1Field.getText());
                    float x2 = Float.parseFloat(x2Field.getText());
                    float y2 = Float.parseFloat(y2Field.getText());
                    walls.add(new Wall(x1, y1, x2, y2));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter numeric values.");
                }
            }
        });

        addFeaturesFrame.add(panel);
        addFeaturesFrame.setVisible(true);
        addFeaturesFrame.setLocationRelativeTo(null); // Center the frame on screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParticleSimulator::new);
    }
}