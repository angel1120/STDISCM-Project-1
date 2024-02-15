import java.util.List;

class Particle {
    private float x, y;
    private float angle;
    private float velocity;

    public Particle(float x, float y, float angle, float velocity) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.velocity = velocity;
    }

    public void update(int width, int height, List<Wall> walls) {
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

        // Bounce off the walls
        for (Wall wall : walls) {
            float x1 = wall.getX1();
            float y1 = wall.getY1();
            float x2 = wall.getX2();
            float y2 = wall.getY2();

            // Check if the particle intersects with the wall segment
            if (isIntersecting(x, y, x + dx, y + dy, x1, y1, x2, y2)) {
                float wallAngle = (float) Math.atan2(y2 - y1, x2 - x1);

                float angleIncidence = (float) Math.atan2(dy, dx);
                float angleDiff = angleIncidence - wallAngle;
                float angleReflection = angleIncidence - 2 * angleDiff;

                angle = (float) Math.toDegrees(angleReflection);

                break;
            }
        }
    }

    private boolean isIntersecting(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float orientation1 = orientation(x1, y1, x2, y2, x3, y3);
        float orientation2 = orientation(x1, y1, x2, y2, x4, y4);
        float orientation3 = orientation(x3, y3, x4, y4, x1, y1);
        float orientation4 = orientation(x3, y3, x4, y4, x2, y2);

        return (orientation1 != orientation2 && orientation3 != orientation4);
    }

    private float orientation(float x1, float y1, float x2, float y2, float x3, float y3) {
        float val = (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
        if (val == 0)
            return 0;  // Collinear
        return (val > 0) ? 1 : 2; // Clockwise or counterclockwise
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

}