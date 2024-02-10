import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSimulator extends JPanel implements ActionListener {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final int PARTICLE_RADIUS = 5;
    private static final int FPS_UPDATE_INTERVAL = 1000; // Update fps every second

    private List<Particle> particles;
    private int particleCount;
    private int fps;
    private int frameCount;
    private long lastFpsUpdateTime;

    public ParticleSimulator() {
        particles = new ArrayList<>();
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

        addParticle();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Particle
        for (Particle particle : particles) {
            g.setColor(Color.BLACK);
            g.fillOval((int) particle.getX(), (int) particle.getY(), 2 * PARTICLE_RADIUS, 2 * PARTICLE_RADIUS);
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

    private void addParticle() {
        JFrame addParticleFrame = new JFrame("Add Particle");
        addParticleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addParticleFrame.setSize(500, 150);
        addParticleFrame.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        JLabel xLabel = new JLabel("X Position:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(xLabel, gbc);

        JTextField xField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(xField, gbc);

        JLabel yLabel = new JLabel("Y Position:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(yLabel, gbc);

        JTextField yField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(yField, gbc);

        JLabel angleLabel = new JLabel("Initial Angle (degrees): ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(angleLabel, gbc);

        JTextField angleField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(angleField, gbc);

        JLabel velocityLabel = new JLabel("Initial Velocity (pixels/second):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(velocityLabel, gbc);

        JTextField velocityField = new JTextField(10);
        gbc.gridx = 1;
        panel.add(velocityField, gbc);

        JButton addButton = new JButton("Add Particle");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        addButton.addActionListener(new ActionListener() {
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

        addParticleFrame.add(panel);
        addParticleFrame.setVisible(true);
        addParticleFrame.setLocationRelativeTo(null); // Center the frame on screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ParticleSimulator();
            }
        });
    }
}