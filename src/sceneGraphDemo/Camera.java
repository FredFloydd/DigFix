package sceneGraphDemo;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Stores the position of the camera in spherical coordinates. The camera is always directed towards point 0,0,0.
 *
 * The class facilitates computation of the view and projection matrices for a camera that is rotating around an object
 * located at the origin.
 */
public class Camera {

    // Vector position, view direction and up direction of camera
    private Vector3f position;
    private Vector3f view_Direction;
    private Vector3f up;

    // Matrix describing camera position and orientation
    private Matrix4f transform;

    // Field of view and aspect ratios
    private float fov_y; // Vertical field-of-view of camera in radians
    private float aspect_ratio; // Aspect ratio of camera

    public Camera(double aspect_ratio, float fov_y, Vector3f initial_position, Vector3f initial_direction, Vector3f up_) {
        this.aspect_ratio = (float) aspect_ratio;
        this.position = initial_position;
        this.view_Direction = initial_direction;
        this.up = up_;
        this.transform = new Matrix4f().lookAlong(initial_direction, up).translate(initial_position.mul(-1));
        this.fov_y = fov_y;
    }

    public void rotate(float angle, Vector3f axis){
        Matrix4f rotation = new Matrix4f().rotate(angle, axis);
        transform = transform.mulLocal(rotation);
        view_Direction = view_Direction.mul(new Matrix3f(rotation));
    }

    public void move(Vector3f movement_vector){
        Matrix4f translation = new Matrix4f().translate(movement_vector);
        transform = transform.mulLocal(translation);
        position = position.add(movement_vector);
    }

    public void set_position(Vector3f new_position){
        transform.translate(new_position);
        transform.translate(position.mul(-1));
        position = new_position;
    }

    public void set_view_direction(Vector3f new_direction){
        transform.translate(position);
        transform.lookAlong(new_direction, up);
    }

    public Matrix4f getViewMatrix() {
        return transform;
    }

    public Vector3f getViewDirection(){
        return view_Direction;
    }

    public Vector3f getCameraPosition() {
        return position;
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f().perspective(fov_y, aspect_ratio, 0.01f, 100f);
    }

    public float getAspectRatio() { return aspect_ratio; }
    public void setAspectRatio(float aspect_ratio) { this.aspect_ratio = aspect_ratio; }
}