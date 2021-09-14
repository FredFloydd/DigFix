package DigFix;

import org.joml.*;

import java.lang.Math;

public class Player extends WorldObject {

    // Camera to render player view
    public Camera camera;

    // Player's body is a CubeRobot
    CubeRobot body;

    // Variables to handle player motion and world interaction
    private final float DIG_DISTANCE = 5f;
    private final float WALK_SPEED = 5f;
    public Vector4i walkingDirections;

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

    public void updatePosition(float deltaTime, long currentTime, Chunk chunk) {
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
        Vector3f rayPos = new Vector3f(camera.position);
        Vector3f rayDir = new Vector3f(camera.orientation).normalize();
        boolean atBlock = false;
        int count = 0;
        while (!atBlock) {
            rayPos.add(new Vector3f(rayDir).mul(0.01f));
            if (!chunk.checkValidIndex(Math.round(rayPos.x), Math.round(rayPos.y), Math.round(rayPos.z))) {
                break;
            }
            if (chunk.getBlockType(Math.round(rayPos.x), Math.round(rayPos.y), Math.round(rayPos.z)) == 1 ) {
                chunk.setBlockType(Math.round(rayPos.x), Math.round(rayPos.y), Math.round(rayPos.z), 0);
                atBlock = true;
            }
            if (count == 100 * DIG_DISTANCE) {
                atBlock = true;
            }
            count++;
        }
    }

    @Override
    public Matrix4f getTransform() {
        return body.getTransform();
    }
}
