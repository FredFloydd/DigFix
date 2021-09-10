package DigFix;

import org.joml.*;

import java.lang.Math;

public class Player extends WorldObject {

    // Camera to render player view
    public Camera camera;

    // Player's body is a CubeRobot
    CubeRobot body;

    // Variables to handle player motion
    private final float walk_speed = 5f; // Speed is capped to this, regardless of direction
    public Vector4i walking_directions;  // Walk direction, index 0 is forwards, 1 right, 2 backwards, 3 left

    // Define up direction for the camera
    private final Vector3f up;

    public Player(Vector3f position_, Vector3f orientation_, float aspect_ratio, float fov_y, Vector3f up_) {
        body = new CubeRobot(position_, new Vector3f(orientation_.x, orientation_.y, orientation_.z));
        up = up_;
        camera = new Camera(aspect_ratio, fov_y, new Vector3f(position_.x, position_.y + body.viewHeight,  position_.z), orientation_, up);
        setPosition(position_);
        setOrientation(orientation_);
        body.setOrientation(new Vector3f(orientation_.x, orientation_.y, orientation_.z));
        walking_directions = new Vector4i(0);
    }

    public void updatePosition(float delta_time, long currentTime) {
        // Move player based on keyboard input
        Vector3f move_vector = new Vector3f();
        int forwards = walking_directions.z - walking_directions.x;
        Vector3f forwards_vector = new Vector3f(orientation.x, 0f, orientation.z).normalize();
        int sideways = walking_directions.w - walking_directions.y;
        Vector3f sideways_vector = new Vector3f();
        forwards_vector.cross(up, sideways_vector);
        sideways_vector.normalize();
        move_vector = move_vector.add(forwards_vector.mul(forwards)).add(sideways_vector.mul(sideways));

        // If move vector isn't null, move and animate the player
        if (move_vector.x != 0 || move_vector.z != 0) {
            move_vector = move_vector.normalize().mul(walk_speed * delta_time);
            move(move_vector);
            camera.move(move_vector);
            if (!body.walking) {
                body.start_walking = currentTime;
            }
            body.walking = true;
        }
        else {
            body.walking = false;
        }
    }

    // Changes player orientation, with positive horizontal rotating clockwise, positive vertical rotating upwards
    public void updatePlayerOrientation(float delta_phi, float delta_theta) {
        body.rotate(-delta_phi, 0);
        camera.rotate(delta_phi, delta_theta);
        rotate(delta_phi, delta_theta);
    }

    public void breakBlock(Chunk chunk) {
        Vector3f check_position = new Vector3f(camera.position);
        Vector3f check_direction = new Vector3f(camera.orientation).normalize();
        boolean atBlock = false;
        while (!atBlock) {
            check_position.add(new Vector3f(check_direction).mul(0.1f));
            if (Math.abs(check_position.y) + 1 > chunk.vertical / 2 || Math.abs(check_position.x) + 1 > chunk.horizontal / 2 || Math.abs(check_position.z) + 1 > chunk.horizontal / 2 ) {
                break;
            }
            if (chunk.block_array[Math.round(check_position.y) + chunk.vertical / 2][Math.round(check_position.x) + chunk.horizontal / 2][Math.round(check_position.z) + chunk.horizontal / 2] == 1 ) {
                chunk.block_array[Math.round(check_position.y) + chunk.vertical / 2][Math.round(check_position.x) + chunk.horizontal / 2][Math.round(check_position.z) + chunk.horizontal / 2] = 0;
                atBlock = true;
            }
        }
    }

    @Override
    public Matrix4f getTransform() {
        return body.getTransform();
    }
}
