package sceneGraphDemo;

import org.joml.*;

public class Player extends WorldObject {

    // Camera to render player view
    public Camera camera;

    // Player's body is a CubeRobot
    CubeRobot body;

    // Variables to handle player motion
    private float walk_speed;             // Speed is capped to this, regardless of direction
    public Vector4i walking_directions;  // Walk direction, index 0 is forwards, 1 right, 2 backwards, 3 left

    // Define up direction for the camera
    private Vector3f up;

    public Player(CubeRobot body_, Vector3f position_, Vector3f orientation_, float aspect_ratio, float fov_y, Vector3f up_) {
        body = body_;
        position = position_;
        orientation = orientation_;
        up = up_;
        transform = new Matrix4f().lookAlong(orientation, up).translate(position);
        camera = new Camera(aspect_ratio, fov_y, (new Vector3f(0f, 4.4f, -0.9f).add(position)), new Vector3f(0f, 0f, -1f), up);
        body.move(position);
        walking_directions = new Vector4i(0);
        walk_speed = 2f;
    }

    public void updatePosition(float delta_time) {
        Vector3f move_vector = new Vector3f();
        int forwards = walking_directions.z - walking_directions.x;
        Vector3f forwards_vector = new Vector3f(orientation.x, 0f, orientation.z).normalize();
        int sideways = walking_directions.w - walking_directions.y;
        Vector3f sideways_vector = new Vector3f();
        forwards_vector.cross(up, sideways_vector);
        sideways_vector.normalize();
        move_vector = move_vector.add(forwards_vector.mul(forwards)).add(sideways_vector.mul(sideways));
        if (move_vector.x != 0 || move_vector.z != 0) {
            move_vector = move_vector.normalize().mul(walk_speed * delta_time);
            position.add(move_vector);
            camera.move(move_vector);
            body.move(move_vector.mul(-1));
        }
    }
}
