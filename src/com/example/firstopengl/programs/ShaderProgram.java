package com.example.firstopengl.programs;

import static android.opengl.GLES20.glUseProgram;

import com.example.firstopengl.util.ShaderHelper;
import com.example.firstopengl.util.TRReader;

import android.content.Context;

public class ShaderProgram {
	//Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	
	//Attribute constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	
	protected final int program;
	protected ShaderProgram(Context context, int vertexShaderResId,
			int fragmentShaderResId){
		program = ShaderHelper.buildProgram(
				TRReader.readTFfromRes(context, vertexShaderResId),
				TRReader.readTFfromRes(context, fragmentShaderResId));
	}
	
	public void useProgram(){
		//使用自定义的程序
		glUseProgram(program);
	}
}
