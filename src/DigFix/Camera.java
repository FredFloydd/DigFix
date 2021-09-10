package DigFix;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends WorldObject {

    // Field of view and aspect ratios
    private final float fov_y; // Vertical field-of-view of camera in radians
    private float aspect_ratio; // Aspect ratio of camera

    public Camera(double aspect_ratio_, float fov_y_, Vector3f initial_position, Vector3f initial_direction, Vector3f up_) {
        aspect_ratio = (float) aspect_ratio_;
        setPosition(initial_position);
        setOrientation(initial_direction);
        up = up_;
        fov_y = fov_y_;
    }

    @Override
    public Matrix4f getTransform() {
        Vector3f eye = new Vector3f().add(position);
        Vector3f center = new Vector3f().add(position).add(orientation);
        return new Matrix4f().lookAt(eye, center, up);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective(fov_y, aspect_ratio, 0.01f, 1000f);
    }

    public float getAspectRatio() {
        return aspect_ratio;
    }

    public float getFov_y() { return  fov_y; }

    public void setAspectRatio(float aspect_ratio_) {
        aspect_ratio = aspect_ratio_;
    }
}