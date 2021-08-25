package sceneGraphDemo;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class WorldObject {

    // Basic parameters
    protected Component body;
    protected Vector3f position;
    protected Vector3f orientation;
    protected Matrix4f transform;
    protected Vector3f velocity;
    protected Cuboid hitbox;
}
