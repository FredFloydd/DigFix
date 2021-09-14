package DigFix;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class Ray {

    private Vector3f origin;
    private Vector3f direction;
    private Vector3f currentPosition;
    private final float EPSILON = 0.00001f;

    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = new Vector3f(origin);
        this.direction = new Vector3f(direction.normalize());
        this.currentPosition = new Vector3f(origin);
    }

    public void getNextFaceIntersection() {
        Vector3f testVector = new Vector3f();
        testVector.x = (float) Math.floor(currentPosition.x);
        testVector.y = (float) Math.floor(currentPosition.y);
        testVector.z = (float) Math.floor(currentPosition.z);
        if (direction.x > 0) {
            testVector.x++;
        }
        if (direction.y > 0) {
            testVector.y++;
        }
        if (direction.z > 0) {
            testVector.z++;
        }
        float lambdaX = (testVector.x - currentPosition.x) / (EPSILON + direction.x);
        float lambdaY = (testVector.y - currentPosition.y) / (EPSILON + direction.y);
        float lambdaZ = (testVector.z - currentPosition.z) / (EPSILON + direction.z);
        currentPosition.add(new Vector3f(direction).mul(Math.min(lambdaX, Math.min(lambdaY, lambdaZ))));
    }

    public Vector3i getNextBlockIntersection() {
        getNextFaceIntersection();
        currentPosition.add(new Vector3f(direction).mul(currentPosition.minComponent() + EPSILON));
        Vector3i blockIndex = new Vector3i();
        blockIndex.x += Math.floor(currentPosition.x);
        blockIndex.y += Math.floor(currentPosition.y);
        blockIndex.z += Math.floor(currentPosition.z);
        return blockIndex;
    }

    public Vector3f getCurrentPosition() {
        return currentPosition;
    }
}
