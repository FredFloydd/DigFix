package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.CallbackI;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Chunk {

    public int[][][] block_array;
    private Cuboid cuboid;
    public final int horizontal = 50;
    public final int vertical = 128;
    private Vector2i position;

    public Chunk(Vector2i chunk_position) {
        position = chunk_position.mul(horizontal);
        cuboid = new Cuboid(new Vector3f(1,1,1), new Vector3f(), new Vector3f(), new Matrix4f(), "resources/dirt_texture.png");
        block_array = new int[vertical][horizontal][horizontal];
        for (int y = -vertical / 2; y < 0; y++) {
            for (int x = -horizontal / 2; x < horizontal / 2; x++) {
                for (int z = -horizontal / 2; z < horizontal / 2; z++) {
                    block_array[y + vertical / 2][x + horizontal / 2][z + horizontal / 2] = 1;
                }
            }
        }
    }

    public void renderChunk(Camera camera1, Camera camera) {
        Vector3f cameraPosition = camera.getPosition();
        Vector3f lookDirection = camera.getOrientation().normalize();
        for (int y = - vertical / 2; y < 0; y++) {
            for (int x = -horizontal / 2; x < horizontal / 2; x++) {
                for (int z = -horizontal / 2; z < horizontal / 2; z++) {
                    Vector3f block_position = new Vector3f(position.x + x, y, position.y + z);
                    Vector3f direction_to_block = new Vector3f(block_position).sub(cameraPosition);
                    direction_to_block.normalize();
                    if (isBlock(x, y, z) && isInFoV(camera, x, y, z) && isVisible(camera, x, y, z)) {
                        cuboid.render(camera1, new Matrix4f().translate(x - position.x, y, z - position.y));
                    }
                }
            }
        }
    }

    private boolean isBlock(int x, int y, int z) {
        return block_array[y + vertical / 2][x + horizontal / 2][z + horizontal / 2] != 0;
    }

    private boolean isInFoV(Camera camera, int x, int y, int z) {
        Vector3f cameraPosition = camera.getPosition();
        Vector3f lookDirection = camera.getOrientation().normalize();
        Vector3f block_position = new Vector3f(position.x + x, y, position.y + z);
        Vector3f direction_to_block = new Vector3f(block_position).sub(cameraPosition);
        direction_to_block.normalize();
        return direction_to_block.dot(lookDirection) > camera.getFov_y() * camera.getAspectRatio() / 2.4;
    }

    private boolean isVisible(Camera camera, int x, int y, int z) {
        Vector3f block_origin = new Vector3f(position.x + x, y, position.y + z);
        Vector3f[] vertices = new Vector3f[] {
                new Vector3f(block_origin).add(0f, 0f, 0f),
                new Vector3f(block_origin).add(0f, 0f, 1f),
                new Vector3f(block_origin).add(0f, 1f, 0f),
                new Vector3f(block_origin).add(0f, 1f, 1f),
                new Vector3f(block_origin).add(1f, 0f, 0f),
                new Vector3f(block_origin).add(1f, 0f, 1f),
                new Vector3f(block_origin).add(1f, 1f, 0f),
                new Vector3f(block_origin).add(1f, 1f, 1f)
        };
        for (int i = 0; i < vertices.length; i++) {
            Vector3f position = vertices[i];
            Vector3f direction = (new Vector3f(camera.getPosition()).sub(position)).normalize();
            position.add(new Vector3f(direction).mul(1.5f));
            boolean atCamera = false;
            while (!atCamera) {
                position.add(new Vector3f(direction).mul(0.1f));
                if (Math.abs(position.y) + 1 > vertical / 2 || Math.abs(position.x) + 1 > horizontal / 2 || Math.abs(position.z) + 1 > horizontal / 2 ) {
                    break;
                }
                if (block_array[Math.round(position.y) + vertical / 2][Math.round(position.x) + horizontal / 2][Math.round(position.z) + horizontal / 2] == 1 ) {
                atCamera = true;
                }
                if (new Vector3f(camera.getPosition()).sub(position).dot(direction) < 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
