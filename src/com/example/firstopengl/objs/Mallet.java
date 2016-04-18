package com.example.firstopengl.objs;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

import com.example.firstopengl.data.Constants;
import com.example.firstopengl.data.VertexArray;
import com.example.firstopengl.programs.ColorShaderProgram;

public class Mallet {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT
			+COLOR_COMPONENT_COUNT)*Constants.BYTES_PER_FLOAT;
	
	private static final float[] VERTEX_DATA = {
			//Order of coordinates: X, Y, R, G, B
			0.0f, -0.4f, 0.0f, 0.0f, 1.0f,
			0.0f,  0.4f, 1.0f, 0.0f, 0.0f
	};
	
	private final VertexArray vertexArray;
	
	public Mallet() {
		// TODO Auto-generated constructor stub
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public void bindData(ColorShaderProgram colorProgram){
		vertexArray.setVertexAttribPointer(
				0,
				colorProgram.getPositionAttributeLocation(), 
				POSITION_COMPONENT_COUNT, 
				STRIDE);
		vertexArray.setVertexAttribPointer(
				POSITION_COMPONENT_COUNT,
				colorProgram.getColorAttributeLocation(),
				COLOR_COMPONENT_COUNT, 
				STRIDE);
	}
	
	public void draw(){
		glDrawArrays(GL_POINTS, 0, 2);
	}
}
