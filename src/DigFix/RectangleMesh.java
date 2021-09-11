package DigFix;

// Class inheriting from Mesh class
// Defines a Rectangular mesh by overloading Mesh's 3D position, UV texture coordinates and normals

public class RectangleMesh extends Mesh {

    public RectangleMesh() {
        super();
        initialize();
    }

    @Override
    float[] initializeVertexPositions() {
        float[] vertPositions = new float[] {
                -1,  0, -1, -1,  0,  1,  1,  0, -1,
                -1,  0,  1,  1,  0,  1,  1,  0, -1
        };
        return vertPositions;
    }

    @Override
    int[] initializeVertexIndices() {

        int[] indices = new int[] {
                0,  1,  2,  3,  4,  5,  6
        };
        return indices;
    }

    @Override
    float[] initializeVertexNormals() {

        float[] vertNormals = new float[] {
                0,  1, 0, 0,  1,  0,  0,  1, 0,
                0,  1,  0,  0,  1,  0,  0,  1, 0
        };
        return vertNormals;
    }

    @Override
    float[] initializeTextureCoordinates() {
        float[] texCoors = new float[] {
                0,0, 	0,1, 	1,0,
                0,1,	1,1,	1,0,
        };
        return texCoors;
    }
}
