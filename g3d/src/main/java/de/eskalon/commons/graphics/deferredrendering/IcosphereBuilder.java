package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;

public class IcosphereBuilder {

	/**
	 * Objects created by this class should always be rendered with this
	 * primitive type (GL_TRIANGLES).
	 */
	public static final int PRIMITIVE_TYPE = GL30.GL_TRIANGLES;

	/**
	 * Maximum possible number of subdivisions. Limited by the internal
	 * structure of {@link com.badlogic.gdx.graphics.Mesh Mesh} that uses short
	 * values for the index array.
	 */
	public static final int MAX_SUBDIVISIONS = 5;

	/**
	 * Creates a shader program for basic rendering.
	 * 
	 * @return the shader program
	 */
	public static ShaderProgram getProgram() {
		return ShaderProgramFactory.fromFile(
				Gdx.files.internal("resources/shaders/basic.vert"),
				Gdx.files.internal("resources/shaders/basic.frag"));
	}

	/**
	 * Generates an icosphere.
	 * 
	 * @param circumRadius
	 *            Circumradius of the icosphere.
	 * @param subdivisions
	 *            Number of subdivisions. The maximum number of subdivions is
	 *            limited to 5 due to internal structure of
	 *            {@link com.badlogic.gdx.graphics.Mesh Mesh} that uses short
	 *            values for the index array.
	 * @return The subdivided icosphere.
	 */
	public static Mesh createIcosphere(float circumRadius, int subdivisions) {
		Preconditions.checkArgument(subdivisions <= MAX_SUBDIVISIONS,
				"exceeded maximum subdivion count");
		Mesh output = IcosphereBuilder.createIcosahedron(circumRadius);
		for (int i = 0; i < subdivisions; i++) {
			output = IcosphereBuilder.subdivide(output);
		}
		return output;
	}

	/**
	 * Creates an icosahedron.
	 * 
	 * @param circumRadius
	 *            Circumradius of the icosahedron.
	 * @return A Mesh of an icosahedron.
	 */
	private static Mesh createIcosahedron(float circumRadius) {
		// golden ratio phi
		double phi = (1.0 + Math.sqrt(5)) / 2.0;
		float du = (float) (1.0 / Math.sqrt(phi * phi + 1.0) * circumRadius);
		float dv = (float) (phi * du);

		// @formatter:off
		Mesh mesh = new Mesh(true, 12, 60, VertexAttribute.Position());
		mesh.setVertices(new float[] {
				 0f, dv, du,
				 0f, dv,-du,
				 0f,-dv, du,
				 0f,-dv,-du,
				
				 du, 0f, dv,
				-du, 0f, dv,
				 du, 0f,-dv,
				-du, 0f,-dv,
				
				 dv, du, 0f,
				 dv,-du, 0f,
				-dv, du, 0f,
				-dv,-du, 0f
		});
		
		mesh.setIndices(new short[] {
				0,  1,  8,
				0,  4,  5,
				0,  5, 10,
				0,  8,  4,
				0, 10,  1,
				1,  6,  8,
				1,  7,  6,
				1, 10,  7,
				2,  3, 11,
				2,  4,  9,
				2,  5,  4,
				2,  9,  3,
				2, 11,  5,
				3,  6,  7,
				3,  7, 11,
				3,  9,  6,
				4,  8,  9,
				5, 11, 10,
				6,  9,  8,
				7, 10, 11
		});
		// @formatter:on
		return mesh;
	}

	/**
	 * Subdivides an icosphere. Every face is subdivided into 4 faces. Vertices
	 * are shifted to assert the circumradius and structure of the icosphere.
	 * 
	 * @param icosphere
	 *            Mesh that gets subdivided.
	 * @return The once subdivided input mesh.
	 */
	private static Mesh subdivide(Mesh icosphere) {
		// the vertices array contains 3 values per vertex which need to be
		// skipped accordingly when accessing it via an index
		final int valuesPerVert = 3;

		VertexLookup lookUp;
		float[] oldVerts = new float[icosphere.getNumVertices()
				* valuesPerVert];
		short[] oldIndices = new short[icosphere.getNumIndices()];
		icosphere.getVertices(oldVerts);
		icosphere.getIndices(oldIndices);
		float[] newVerts;
		short[] newIndices;

		// calculate circumradius to match the one of the input mesh
		float circumRadius = new Vector3(oldVerts[0], oldVerts[1], oldVerts[2])
				.len();

		// TODO: document thought process behind the 42
		// calculate new number of vertices
		int numNewVerts = 42;
		for (int i = 1; numNewVerts <= icosphere.getNumVertices(); i++) {
			numNewVerts += ((int) Math.pow(4, i) * 30);
		}

		// calculate new number of faces
		int numNewIndices = icosphere.getNumIndices() * 4;

		// allocate memory
		newVerts = new float[numNewVerts * valuesPerVert];
		newIndices = new short[numNewIndices];
		lookUp = new VertexLookup(
				(numNewVerts - icosphere.getNumVertices()) * 3);

		// copy old vertices
		for (int i = 0; i < oldVerts.length; i++) {
			newVerts[i] = oldVerts[i];
		}

		// these pointers keep track of the next value to fill in the newVerts
		// and newIndices arrays
		int vertPointer = oldVerts.length;
		int indexPointer = 0;

		// this offset skips over the first 3 values in "tmpIndices"
		final int offset = 3;

		// for each face, therefore skipping 3 indices per face
		for (int i = 0; i < oldIndices.length; i += 3) {
			// this array contains the indices of the necessary vertices to
			// subdivide one triangle
			short[] tmpIndices = new short[6];
			tmpIndices[0] = (short) (oldIndices[i]);
			tmpIndices[1] = (short) (oldIndices[i + 1]);
			tmpIndices[2] = (short) (oldIndices[i + 2]);

			short nextIndex = (short) (vertPointer / valuesPerVert);

			for (int j = 3; j < tmpIndices.length; j++) {
				tmpIndices[j] = lookUp.lookUp(tmpIndices[j % offset],
						tmpIndices[(j + 1) % offset]);
				if (tmpIndices[j] < 0) {
					Vector3 v0 = new Vector3(
							oldVerts[tmpIndices[j % offset] * valuesPerVert],
							oldVerts[tmpIndices[j % offset] * valuesPerVert
									+ 1],
							oldVerts[tmpIndices[j % offset] * valuesPerVert
									+ 2]);
					Vector3 v1 = new Vector3(
							oldVerts[tmpIndices[(j + 1) % offset]
									* valuesPerVert],
							oldVerts[tmpIndices[(j + 1) % offset]
									* valuesPerVert + 1],
							oldVerts[tmpIndices[(j + 1) % offset]
									* valuesPerVert + 2]);

					// calculate new vertex position
					Vector3 v2 = v0.add(v1).scl(0.5f).nor().scl(circumRadius);

					// add new vertex to the vertices array
					newVerts[vertPointer++] = v2.x;
					newVerts[vertPointer++] = v2.y;
					newVerts[vertPointer++] = v2.z;

					// register the new vertex in the look up table
					lookUp.add(tmpIndices[j % offset],
							tmpIndices[(j + 1) % offset], nextIndex);
					tmpIndices[j] = nextIndex++;
				}
			}

			// add new faces to the indices array

			// v0 v3 v5
			newIndices[indexPointer++] = tmpIndices[0];
			newIndices[indexPointer++] = tmpIndices[3];
			newIndices[indexPointer++] = tmpIndices[5];

			// v3 v1 v4
			newIndices[indexPointer++] = tmpIndices[3];
			newIndices[indexPointer++] = tmpIndices[1];
			newIndices[indexPointer++] = tmpIndices[4];

			// v4 v2 v5
			newIndices[indexPointer++] = tmpIndices[4];
			newIndices[indexPointer++] = tmpIndices[2];
			newIndices[indexPointer++] = tmpIndices[5];

			// v3 v4 v5
			newIndices[indexPointer++] = tmpIndices[3];
			newIndices[indexPointer++] = tmpIndices[4];
			newIndices[indexPointer++] = tmpIndices[5];
		}

		// create mesh object an fill it with the calculated data
		Mesh output = new Mesh(true, numNewVerts, numNewIndices,
				VertexAttribute.Position());
		output.setVertices(newVerts);
		output.setIndices(newIndices);
		return output;
	}

	/**
	 * This class prevents the {@link IcosphereBuilder} from creating duplicate
	 * vertices.
	 * 
	 * @author Sarroxxie
	 *
	 */
	private static class VertexLookup {
		private short[] table;
		private int pointer;

		public VertexLookup(int length) {
			table = new short[length];
			pointer = 0;
		}

		/**
		 * Looks up if there exists a vertex in the middle of the two input
		 * vertices.
		 * 
		 * @param vertex1
		 *            Index to the first vertex.
		 * @param vertex2
		 *            Index to the second vertex.
		 * @return The index to the vertex between the two input vertices if it
		 *         is already in the list. Returns -1 else.
		 */
		public short lookUp(short vertex1, short vertex2) {
			for (int i = 0; i < pointer; i += 3) {
				if ((vertex1 == table[i] && vertex2 == table[i + 1])
						|| (vertex2 == table[i] && vertex1 == table[i + 1])) {
					return table[i + 2];
				}
			}
			return -1;
		}

		/**
		 * Registers a new vertex to the look up table.
		 * 
		 * @param vertex1
		 *            Index to the first vertex.
		 * @param vertex2
		 *            Index to the second vertex.
		 * @param vertex3
		 *            Index to the vertex that lies in the middle of vertex1 and
		 *            vertex2.
		 */
		public void add(short vertex1, short vertex2, short vertex3) {
			this.table[pointer++] = vertex1;
			this.table[pointer++] = vertex2;
			this.table[pointer++] = vertex3;
		}
	}

}
