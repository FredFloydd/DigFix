package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class WorldObject {

    // Component to draw object
    protected Component body;

    // Vector position, orientation and up direction of object
    protected Vector3f position;
    protected Vector3f orientation;
    protected Vector3f up;

    // Polar coordinates of the object's orientation
    protected double phi;
    protected double theta;
    protected final double EPSILON = 0.1;

    // Updates the orientation, given polar coordinates
    private void updateOrientation() {
        orientation.x = (float) (Math.sin(phi) * Math.sin(theta));
        orientation.y = (float) Math.cos(theta);
        orientation.z = (float) -(Math.cos(phi) * Math.sin(theta));
    }

    // Updates the polars, given an orientation
    private void updatePolars() {
        phi = Math.atan(-orientation.x / orientation.z);
        theta = Math.atan(Math.sqrt(orientation.z * orientation.z + orientation.x * orientation.x) / orientation.y);
        if (theta < 0) { theta += Math.PI; }
    }

    // Rotates the object by the given polar angles
    public void rotate(double deltaPhi, double deltaTheta){
        phi += deltaPhi;
        if (phi > 2 * Math.PI) { phi -= 2 * Math.PI; }
        if (phi < 0) { phi += 2 * Math.PI; }
        theta = Math.max(EPSILON, Math.min(Math.PI - EPSILON, theta + deltaTheta));
        updateOrientation();
    }

    // Sets the polars to the given values
    public void setPolars(double phi, double theta) {
        this.phi = phi;
        if (this.phi > 2 * Math.PI) {this.phi -= 2 * Math.PI;}
        if (this.phi < 0) {this.phi += 2 * Math.PI;}
        this.theta = Math.max(EPSILON, Math.min(Math.PI - EPSILON, theta));
        updateOrientation();
    }

    // Moves the object by the given vector in world coordinates
    public void move(Vector3f movementVector) {
        position.x -= movementVector.x;
        position.y += movementVector.y;
        position.z -= movementVector.z;
    }

    public void setPosition(Vector3f newPosition) {
        position = newPosition;
    }

    public void setOrientation(Vector3f newDirection) {
        orientation = newDirection;
        updatePolars();
    }

    public Vector3f getOrientation() {
        return orientation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public abstract Matrix4f getTransform();
}
