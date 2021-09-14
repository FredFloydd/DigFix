package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Chunk {

    public int[][][] blockArray;
    private Cuboid cuboid;
    public final int HORIZONTAL = 32;
    public final int VERTICAL = 128;
    private Vector2i chunkOrigin;

    public Chunk(Vector2i chunkIndex) {
        chunkOrigin = chunkIndex.mul(HORIZONTAL);
        cuboid = new Cuboid(new Vector3f(1,1,1), new Vector3f(), new Vector3f(), new Matrix4f(), "resources/dirt_texture.png");
        blockArray = new int[VERTICAL + 1][HORIZONTAL + 1][HORIZONTAL + 1];
        for (int y = -VERTICAL / 2; y < 0; y++) {
            for (int x = -HORIZONTAL / 2; x < HORIZONTAL / 2; x++) {
                for (int z = -HORIZONTAL / 2; z < HORIZONTAL / 2; z++) {
                    setBlockType(x, y, z, 1);
                }
            }
        }
    }

    public int getBlockType(int x, int y, int z) {
        int xIndex = x + HORIZONTAL / 2;
        int yIndex = y + VERTICAL / 2;
        int zIndex = z + HORIZONTAL / 2;
        return blockArray[yIndex][xIndex][zIndex];
    }

    public void setBlockType(int x, int y, int z, int value) {
        int xIndex = x + HORIZONTAL / 2;
        int yIndex = y + VERTICAL / 2;
        int zIndex = z + HORIZONTAL / 2;
        blockArray[yIndex][xIndex][zIndex] = value;
    }

    public boolean checkValidIndex(int x, int y, int z) {
        return Math.abs(x) < HORIZONTAL / 2 && Math.abs(y) < VERTICAL / 2 && Math.abs(z) < HORIZONTAL / 2;
    }

    public void renderChunk(Camera camera) {
        for (int y = -VERTICAL / 2; y < VERTICAL / 2; y++) {
            for (int x = -HORIZONTAL / 2; x < HORIZONTAL / 2; x++) {
                for (int z = -HORIZONTAL / 2; z < HORIZONTAL / 2; z++) {
                    if (toRender(camera, x, y, z)) {
                        cuboid.render(camera, new Matrix4f().translate(x + chunkOrigin.x, y, z + chunkOrigin.y));
                    }
                }
            }
        }
    }

    private boolean toRender(Camera camera, int x, int y, int z) {
        return isBlock(x, y, z) && isInFoV(camera, x, y, z) && isVisible(camera, x, y, z);
    }

    private boolean isBlock(int x, int y, int z) {
        return getBlockType(x, y, z) != 0;
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
            Vector3f directionToCamera = (new Vector3f(camera.position).sub(vertices[i])).normalize();
            Ray rayToCamera = new Ray(vertices[i], directionToCamera);
            boolean blocked = false;
            while (!blocked) {
                Vector3i blockIndex = rayToCamera.getNextBlockIntersection();
                Vector3f rayPosition = rayToCamera.getCurrentPosition();
                if (getBlockType(blockIndex.x, blockIndex.y, blockIndex.z) != 0) {
                    blocked = true;
                } else if (directionToCamera.dot(new Vector3f(rayPosition).sub(camera.getPosition())) < 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
