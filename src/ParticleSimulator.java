import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ParticleSimulator extends JPanel implements ActionListener {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final int PARTICLE_RADIUS = 5;
    private static final int FPS_UPDATE_INTERVAL = 1000; // Update fps every second

    private List<Particle> particles;
    private List<Wall> walls;
    private int particleCount;
    private int fps;
    private int frameCount;
    private long lastFpsUpdateTime;

    public ParticleSimulator() {
        particles = new ArrayList<>();
        walls = new ArrayList<>();
        particleCount = 0;
        fps = 0;
        frameCount = 0;
        lastFpsUpdateTime = System.currentTimeMillis();

        // Adjust frame rate to 60 fps
        Timer timer = new Timer(1000 / 60, this);
        timer.start();

        Timer fpsTimer = new Timer(FPS_UPDATE_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();
                fps = (int) (frameCount * 1000 / (currentTime - lastFpsUpdateTime));
                lastFpsUpdateTime = currentTime;
                frameCount = 0;
            }
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

        // Particle
        for (Particle particle : particles) {
            g.setColor(Color.BLACK);
            g.fillOval((int) particle.getX(), (int) particle.getY(), 2 * PARTICLE_RADIUS, 2 * PARTICLE_RADIUS);
        }

        // Wall
        for (Wall wall : walls) {
            g.setColor(Color.BLUE);
            g.drawLine((int) wall.getX1(), (int) wall.getY1(), (int) wall.getX2(), (int) wall.getY2());
        }

        // Particle count and fps
        g.setColor(Color.BLACK);
        g.drawString("Particles: " + particleCount, 10, 20);
        g.drawString("FPS: " + fps, 10, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Particle particle : particles) {
            particle.update(WINDOW_WIDTH, WINDOW_HEIGHT);
        }
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

        JLabel xLabel = new JLabel("X Position:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(xLabel, gbc);

        JTextField xField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(xField, gbc);

        JLabel yLabel = new JLabel("Y Position:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(yLabel, gbc);

        JTextField yField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(yField, gbc);

        JLabel angleLabel = new JLabel("Initial Angle (degrees): ");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(angleLabel, gbc);

        JTextField angleField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(angleField, gbc);

        JLabel velocityLabel = new JLabel("Initial Velocity (pixels/second): ");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(velocityLabel, gbc);

        JTextField velocityField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(velocityField, gbc);

        JButton addParticleButton = new JButton("Add Particle");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(addParticleButton, gbc);

        addParticleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    float x = Float.parseFloat(xField.getText());
                    float y = Float.parseFloat(yField.getText());
                    float angle = Float.parseFloat(angleField.getText());
                    float velocity = Float.parseFloat(velocityField.getText());
                    particles.add(new Particle(x, y, angle, velocity));
                    particleCount++;
                    // test big numbers
//                    while (particleCount!=100000) {
//                        Random rand = new Random();
//                        int temp = rand.nextInt(10);
//                        particles.add(new Particle(x, y, angle, temp));
//                        particleCount++;
//                    }
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
        gbc.gridy = 7;
        panel.add(addWallLabel, gbc);

        JLabel x1Label = new JLabel("X1:");
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(x1Label, gbc);

        JTextField x1Field = new JTextField(10);
        gbc.gridx = 1;
        panel.add(x1Field, gbc);

        JLabel y1Label = new JLabel("Y1:");
        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(y1Label, gbc);

        JTextField y1Field = new JTextField(10);
        gbc.gridx = 1;
        panel.add(y1Field, gbc);

        JLabel x2Label = new JLabel("X2:");
        gbc.gridx = 0;
        gbc.gridy = 10;
        panel.add(x2Label, gbc);

        JTextField x2Field = new JTextField(10);
        gbc.gridx = 1;
        panel.add(x2Field, gbc);

        JLabel y2Label = new JLabel("Y2:");
        gbc.gridx = 0;
        gbc.gridy = 11;
        panel.add(y2Label, gbc);

        JTextField y2Field = new JTextField(10);
        gbc.gridx = 1;
        panel.add(y2Field, gbc);

        JButton addWallButton = new JButton("Add Wall");
        gbc.gridx = 0;
        gbc.gridy = 12;
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ParticleSimulator();
            }
        });
    }
}