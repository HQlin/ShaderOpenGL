package com.example.firstopengl.programs;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.example.firstopengl.R;

import android.content.Context;

public class ColorShaderProgram extends ShaderProgram{
	//Uniform locations
	private final int uMatrixLocation;
	
	//Attribute locations
	private final int aPositionLocation;
	private final int aColorLocation;
	
	public ColorShaderProgram(Context context) {
		// TODO Auto-generated constructor stub
		super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);
		
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aColorLocation = glGetAttribLocation(program, A_COLOR);
	}
	/**
	 * 设置着色器uniform变量
	 * @param matrix 
	 */
	public void setUniforms(float[] matrix){
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
	}
	public int getPositionAttributeLocation() {
		// TODO Auto-generated method stub
		return aPositionLocation;
	}

	public int getColorAttributeLocation() {
		// TODO Auto-generated method stub
		return aColorLocation;
	}

}
