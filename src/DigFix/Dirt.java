package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Dirt extends Block {

    public Dirt(Vector3i position_, Vector3i orientation_) {
        texture_path = "resources/dirt_texture.png";
        appearance = new Cuboid(new Vector3f(1f), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(position_.x, position_.y, position_.z), new Matrix4f(), texture_path);
        position = position_;
        orientation = orientation_;
    }

}
