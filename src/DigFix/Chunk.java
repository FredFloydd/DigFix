package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Chunk {

    public int[][][] block_array;
    private Cuboid cuboid;
    public final int horizontal = 32;
    public final int vertical = 128;
    private Vector2i chunk_origin;

    public Chunk(Vector2i chunk_index) {
        chunk_origin = chunk_index.mul(horizontal);
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
        for (int y = - vertical / 2; y < vertical / 2; y++) {
            for (int x = -horizontal / 2; x < horizontal / 2; x++) {
                for (int z = -horizontal / 2; z < horizontal / 2; z++) {
                    if (isBlock(x, y, z) && isInFoV(camera, x, y, z) && isVisible(camera, x, y, z)) {
                        cuboid.render(camera1, new Matrix4f().translate(x - chunk_origin.x, y, z - chunk_origin.y));
                    }
                }
            }
        }
    }

    private boolean isBlock(int x, int y, int z) {
        return block_array[y + vertical / 2][x + horizontal / 2][z + horizontal / 2] != 0;
    }

    private boolean isInFoV(Camera camera, int x, int y, int z) {
        Vector3f camera_position = camera.getPosition();
        Vector3f look_direction = camera.getOrientation().normalize();
        Vector3f block_position = new Vector3f(chunk_origin.x + x, y, chunk_origin.y + z);
        Vector3f direction_to_block = (new Vector3f(block_position).sub(camera_position)).normalize();
        return direction_to_block.dot(look_direction) > camera.getFov_y() * camera.getAspectRatio() / 2.4f;
    }

    private boolean isVisible(Camera camera, int x, int y, int z) {
        Vector3f block_origin = new Vector3f(chunk_origin.x + x, y, chunk_origin.y + z);
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
            boolean blocked = false;
            while (!blocked) {
                position.add(new Vector3f(direction).mul(0.1f));
                if (Math.abs(position.y) + 1 > vertical / 2 || Math.abs(position.x) + 1 > horizontal / 2 || Math.abs(position.z) + 1 > horizontal / 2 ) {
                    break;
                }
                if (block_array[Math.round(position.y) + vertical / 2][Math.round(position.x) + horizontal / 2][Math.round(position.z) + horizontal / 2] == 1 ) {
                blocked = true;
                }
                if (new Vector3f(camera.getPosition()).sub(position).dot(direction) < 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
