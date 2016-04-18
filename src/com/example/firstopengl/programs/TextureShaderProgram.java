package com.example.firstopengl.programs;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.example.firstopengl.R;

import android.content.Context;

public class TextureShaderProgram extends ShaderProgram{
	//Uniform locations
	private final int uMatrixLocation;
	private final int uTextureUnitLocation;
	
	//Attribute locations
	private final int aPositionLocation;
	private final int aTextureCoordinatesLocation;
	
	public TextureShaderProgram(Context context) {
		// TODO Auto-generated constructor stub
		super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
		
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
		
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
	}
	
	/**
	 * 设置着色器uniform变量
	 * @param matrix
	 * @param textureId
	 */
	public void setUniforms(float[] matrix, int textureId){
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		//激活纹理单元0
		glActiveTexture(GL_TEXTURE0);
		//绑定纹理单元
		glBindTexture(GL_TEXTURE_2D, textureId);
		//传递纹理单元0
		glUniform1i(uTextureUnitLocation, 0);
	}
	
	public int getPositionAttributeLocation(){
		return aPositionLocation;
	}


	public int getTextureCoodinatesAttributeLocation() {
		return aTextureCoordinatesLocation;
	}

}
