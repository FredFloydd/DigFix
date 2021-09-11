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
    protected final double epsilon = 0.1;

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
    public void rotate(double delta_phi, double delta_theta){
        phi += delta_phi;
        if (phi > 2 * Math.PI) { phi -= 2 * Math.PI; }
        if (phi < 0) { phi += 2 * Math.PI; }
        theta = Math.max(epsilon, Math.min(Math.PI - epsilon, theta + delta_theta));
        updateOrientation();
    }

    // Sets the polars to the given values
    public void setPolars(double phi_, double theta_) {
        phi = phi_;
        if (phi > 2 * Math.PI) {phi -= 2 * Math.PI;}
        if (phi < 0) {phi += 2 * Math.PI;}
        theta = Math.max(0.2, Math.min(Math.PI - 0.2, theta_));
        updateOrientation();
    }

    // Moves the object by the given vector in world coordinates
    public void move(Vector3f movement_vector){
        position.x -= movement_vector.x;
        position.y += movement_vector.y;
        position.z -= movement_vector.z;
    }

    public void setPosition(Vector3f new_position){
        position = new_position;
    }

    public void setOrientation(Vector3f new_direction){
        orientation = new_direction;
        updatePolars();
    }

    public Vector3f getOrientation(){
        return orientation;
    }

    public Vector3f getPosition() { return position; }

    public abstract Matrix4f getTransform();

}
