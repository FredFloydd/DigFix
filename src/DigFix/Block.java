package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

public abstract class Block extends WorldObject {

    // Cuboid for rendering
    protected Cuboid appearance;
    protected String texture_path;

    // Vector position and rotation
    protected  Vector3i position;
    protected Vector3i orientation;

    @Override
    public Matrix4f getTransform() {
        return new Matrix4f().translate((Vector3fc) position).lookAlong((Vector3fc) orientation, up);
    }

    public Cuboid getAppearance() { return appearance; }
}
