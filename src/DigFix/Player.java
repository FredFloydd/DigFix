package DigFix;

import org.joml.*;

import java.lang.Math;

public class Player extends WorldObject {

    // Camera to render player view
    public Camera camera;

    // Player's body is a CubeRobot
    CubeRobot body;

    // Variables to handle player motion
    private final float WALK_SPEED = 5f; // Speed is capped to this, regardless of direction
    public Vector4i walkingDirections;  // Walk direction, index 0 is forwards, 1 right, 2 backwards, 3 left

    // Define up direction for the camera
    private final Vector3f up;

    public Player(Vector3f position, Vector3f orientation, float aspectRatio, float FOV_Y, Vector3f up) {
        this.body = new CubeRobot(position, new Vector3f(orientation.x, orientation.y, orientation.z));
        this.up = up;
        this.camera = new Camera(aspectRatio, FOV_Y, new Vector3f(position.x, position.y + body.viewHeight,  position.z), orientation, up);
        this.walkingDirections = new Vector4i(0);
        setPosition(position);
        setOrientation(orientation);
        body.setOrientation(new Vector3f(orientation.x, orientation.y, orientation.z));
    }

    public void updatePosition(float deltaTime, long currentTime) {
        // Move player based on keyboard input
        Vector3f moveVector = new Vector3f();
        int forwards = walkingDirections.z - walkingDirections.x;
        Vector3f forwardsVector = new Vector3f(orientation.x, 0f, orientation.z).normalize();
        int sideways = walkingDirections.w - walkingDirections.y;
        Vector3f sidewaysVector = new Vector3f();
        forwardsVector.cross(up, sidewaysVector);
        sidewaysVector.normalize();
        moveVector.add(forwardsVector.mul(forwards)).add(sidewaysVector.mul(sideways));

        // If move vector isn't null, move and animate the player
        if (moveVector.x != 0 || moveVector.z != 0) {
            moveVector.normalize().mul(WALK_SPEED * deltaTime);
            move(moveVector);
            camera.move(moveVector);
            if (!body.walking) {
                body.startWalking = currentTime;
            }
            body.walking = true;
        }
        else {
            body.walking = false;
        }
    }

    // Changes player orientation, with positive horizontal rotating clockwise, positive vertical rotating upwards
    public void updatePlayerOrientation(float deltaPhi, float deltaTheta) {
        body.rotate(-deltaPhi, 0);
        camera.rotate(deltaPhi, deltaTheta);
        rotate(deltaPhi, deltaTheta);
    }

    public void breakBlock(Chunk chunk) {
        Vector3f checkPosition = new Vector3f(camera.position);
        Vector3f checkDirection = new Vector3f(camera.orientation).normalize();
        boolean atBlock = false;
        while (!atBlock) {
            checkPosition.add(new Vector3f(checkDirection).mul(0.1f));
            if (Math.abs(checkPosition.y) + 1 > chunk.VERTICAL / 2 || Math.abs(checkPosition.x) + 1 > chunk.HORIZONTAL / 2 || Math.abs(checkPosition.z) + 1 > chunk.HORIZONTAL / 2 ) {
                break;
            }
            if (chunk.blockArray[Math.round(checkPosition.y) + chunk.VERTICAL / 2][Math.round(checkPosition.x) + chunk.HORIZONTAL / 2][Math.round(checkPosition.z) + chunk.HORIZONTAL / 2] == 1 ) {
                chunk.blockArray[Math.round(checkPosition.y) + chunk.VERTICAL / 2][Math.round(checkPosition.x) + chunk.HORIZONTAL / 2][Math.round(checkPosition.z) + chunk.HORIZONTAL / 2] = 0;
                atBlock = true;
            }
        }
    }

    @Override
    public Matrix4f getTransform() {
        return body.getTransform();
    }
}
