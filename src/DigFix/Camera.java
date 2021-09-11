package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends WorldObject {

    // Field of view and aspect ratios
    private final float fov_y; // Vertical field-of-view of camera in radians
    private float aspect_ratio; // Aspect ratio of camera

    // Near and far clipping distances
    private final float near_distance = 0.01f;
    private final float far_distance = 1000f;

    public Camera(double aspect_ratio, float fov_y, Vector3f position, Vector3f orientation, Vector3f up) {
        this.aspect_ratio = (float) aspect_ratio;
        setPosition(position);
        setOrientation(orientation);
        this.up = up;
        this.fov_y = fov_y;
    }

    @Override
    public Matrix4f getTransform() {
        Vector3f eye = new Vector3f().add(position);
        Vector3f center = new Vector3f().add(position).add(orientation);
        return new Matrix4f().lookAt(eye, center, up);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective(fov_y, aspect_ratio, near_distance, far_distance);
    }

    public void setAspectRatio(float aspect_ratio) { this.aspect_ratio = aspect_ratio; }

    public float getAspectRatio() {
        return aspect_ratio;
    }

    public float getFov_y() { return  fov_y; }

    public float getNearDistance() { return near_distance; }

    public float getFarDistance() { return far_distance; }

}