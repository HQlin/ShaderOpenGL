package com.example.firstopengl.objs;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

import com.example.firstopengl.data.Constants;
import com.example.firstopengl.data.VertexArray;
import com.example.firstopengl.programs.TextureShaderProgram;

public class Table {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT
			+TEXTURE_COORDINATES_COMPONENT_COUNT)*Constants.BYTES_PER_FLOAT;
	
	private static final float[] VERTEX_DATA = {
			//Order of coordinates: X, Y, S, T
			
			//Triangle Fan
			 0.0f,  0.0f, 0.5f, 0.5f,
			-0.5f, -0.8f, 0.0f, 1.0f,
			 0.5f, -0.8f, 1.0f, 1.0f,
			 0.5f,  0.8f, 1.0f, 0.0f,
			-0.5f,  0.8f, 0.0f, 0.0f,
			-0.5f, -0.8f, 0.0f, 1.0f
	};
	
	private final VertexArray vertexArray;
	
	public Table() {
		// TODO Auto-generated constructor stub
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	public void bindData(TextureShaderProgram textureProgram){
		vertexArray.setVertexAttribPointer(
				0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT,
				STRIDE);
		
		vertexArray.setVertexAttribPointer(
				POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoodinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, 
				STRIDE);
	}
	
	public void draw(){
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
}
