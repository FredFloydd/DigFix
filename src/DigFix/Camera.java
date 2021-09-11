package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera extends WorldObject {

    // Field of view and aspect ratios
    private final float FOV_Y; // Vertical field-of-view of camera in radians
    private float aspectRatio; // Aspect ratio of camera

    // Near and far clipping distances
    private final float NEAR_DISTANCE = 0.01f;
    private final float FAR_DISTANCE = 1000f;

    public Camera(double aspectRatio, float FOV_Y, Vector3f position, Vector3f orientation, Vector3f up) {
        this.aspectRatio = (float) aspectRatio;
        setPosition(position);
        setOrientation(orientation);
        this.up = up;
        this.FOV_Y = FOV_Y;
    }

    @Override
    public Matrix4f getTransform() {
        Vector3f eye = new Vector3f().add(position);
        Vector3f center = new Vector3f().add(position).add(orientation);
        return new Matrix4f().lookAt(eye, center, up);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective(FOV_Y, aspectRatio, NEAR_DISTANCE, FAR_DISTANCE);
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public float getFOV_Y() {
        return FOV_Y;
    }

    public float getNearDistance() {
        return NEAR_DISTANCE;
    }

    public float getFarDistance() {
        return FAR_DISTANCE;
    }

}