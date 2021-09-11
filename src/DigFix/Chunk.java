package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Chunk {

    public int[][][] blockArray;
    private Cuboid cuboid;
    public final int HORIZONTAL = 32;
    public final int VERTICAL = 128;
    private Vector2i chunkOrigin;

    public Chunk(Vector2i chunkIndex) {
        chunkOrigin = chunkIndex.mul(HORIZONTAL);
        cuboid = new Cuboid(new Vector3f(1,1,1), new Vector3f(), new Vector3f(), new Matrix4f(), "resources/dirt_texture.png");
        blockArray = new int[VERTICAL][HORIZONTAL][HORIZONTAL];
        for (int y = -VERTICAL / 2; y < 0; y++) {
            for (int x = -HORIZONTAL / 2; x < HORIZONTAL / 2; x++) {
                for (int z = -HORIZONTAL / 2; z < HORIZONTAL / 2; z++) {
                    blockArray[y + VERTICAL / 2][x + HORIZONTAL / 2][z + HORIZONTAL / 2] = 1;
                }
            }
        }
    }

    public void renderChunk(Camera camera1, Camera camera) {
        for (int y = -VERTICAL / 2; y < VERTICAL / 2; y++) {
            for (int x = -HORIZONTAL / 2; x < HORIZONTAL / 2; x++) {
                for (int z = -HORIZONTAL / 2; z < HORIZONTAL / 2; z++) {
                    if (isBlock(x, y, z) && isInFoV(camera, x, y, z) && isVisible(camera, x, y, z)) {
                        cuboid.render(camera1, new Matrix4f().translate(x - chunkOrigin.x, y, z - chunkOrigin.y));
                    }
                }
            }
        }
    }

    private boolean isBlock(int x, int y, int z) {
        return blockArray[y + VERTICAL / 2][x + HORIZONTAL / 2][z + HORIZONTAL / 2] != 0;
    }

    private boolean isInFoV(Camera camera, int x, int y, int z) {
        Vector3f cameraPosition = camera.getPosition();
        Vector3f lookDirection = camera.getOrientation().normalize();
        Vector3f blockPosition = new Vector3f(chunkOrigin.x + x, y, chunkOrigin.y + z);
        Vector3f directionToBlock = (new Vector3f(blockPosition).sub(cameraPosition)).normalize();
        return directionToBlock.dot(lookDirection) > camera.getFOV_Y() * camera.getAspectRatio() / 2.4f;
    }

    private boolean isVisible(Camera camera, int x, int y, int z) {
        Vector3f blockOrigin = new Vector3f(chunkOrigin.x + x, y, chunkOrigin.y + z);
        Vector3f[] vertices = new Vector3f[] {
                new Vector3f(blockOrigin).add(0f, 0f, 0f),
                new Vector3f(blockOrigin).add(0f, 0f, 1f),
                new Vector3f(blockOrigin).add(0f, 1f, 0f),
                new Vector3f(blockOrigin).add(0f, 1f, 1f),
                new Vector3f(blockOrigin).add(1f, 0f, 0f),
                new Vector3f(blockOrigin).add(1f, 0f, 1f),
                new Vector3f(blockOrigin).add(1f, 1f, 0f),
                new Vector3f(blockOrigin).add(1f, 1f, 1f)
        };
        for (int i = 0; i < vertices.length; i++) {
            Vector3f position = vertices[i];
            Vector3f direction = (new Vector3f(camera.getPosition()).sub(position)).normalize();
            boolean blocked = false;
            while (!blocked) {
                position.add(new Vector3f(direction).mul(0.1f));
                if (Math.abs(position.y) + 1 > VERTICAL / 2 || Math.abs(position.x) + 1 > HORIZONTAL / 2 || Math.abs(position.z) + 1 > HORIZONTAL / 2 ) {
                    break;
                }
                if (blockArray[Math.round(position.y) + VERTICAL / 2][Math.round(position.x) + HORIZONTAL / 2][Math.round(position.z) + HORIZONTAL / 2] == 1 ) {
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
