class Particle {
    private float x, y;
    private float angle; // Angle in degrees
    private float velocity;

    public Particle(float x, float y, float angle, float velocity) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.velocity = velocity;
    }

    public void update(int width, int height) {
        // Update position based on velocity and angle
        float dx = (float) (velocity * Math.cos(Math.toRadians(angle)));
        float dy = (float) (velocity * Math.sin(Math.toRadians(angle)));
        x += dx;
        y += dy;

        // Bounce off the canvas
        if (x < 0 || x > width)
            angle = 180 - angle;
        if (y < 0 || y > height)
            angle = -angle;

        // bounce off the walls
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}