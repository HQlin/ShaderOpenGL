package com.example.firstopengl.programs;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.example.firstopengl.R;

import android.content.Context;

public class ColorShaderProgram extends ShaderProgram{
	//Uniform locations
	private final int uMatrixLocation;
	private final int uColorLocation;
	
	//Attribute locations
	private final int aPositionLocation;
	private final int aColorLocation;
	
	public ColorShaderProgram(Context context) {
		// TODO Auto-generated constructor stub
		super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);
		
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aColorLocation = glGetAttribLocation(program, A_COLOR);
	}
	/**
	 * 设置着色器uniform变量
	 * @param matrix
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setUniforms(float[] matrix, float r, float g, float b){
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		glUniform4f(uColorLocation, r, g, b, 1f);
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
