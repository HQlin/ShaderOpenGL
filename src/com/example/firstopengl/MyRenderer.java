package com.example.firstopengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.firstopengl.objs.Mallet;
import com.example.firstopengl.objs.Table;
import com.example.firstopengl.programs.ColorShaderProgram;
import com.example.firstopengl.programs.TextureShaderProgram;
import com.example.firstopengl.util.TextureHelper;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

public class MyRenderer implements Renderer {
	private final Context context;

	//ͶӰ����
	private final float[] projectionMatrix = new float[16];	
	//ģ�;���
	private final float[] modelMatrix = new float[16];
	//objs
	private Table table;
	private Mallet mallet;
	//��ɫ������
	private TextureShaderProgram textureProgram;
	private ColorShaderProgram colorProgram;
	//����
	private int texture;

	public MyRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	// ����ÿһ֡ʱ��GLSurfaceView���ã�suface������
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		// �����Ļ��ɫ
		glClear(GL_COLOR_BUFFER_BIT);

		//����table
		textureProgram.useProgram();
		textureProgram.setUniforms(projectionMatrix, texture);
		table.bindData(textureProgram);
		table.draw();
		
		//����mallet
		colorProgram.useProgram();
		colorProgram.setUniforms(projectionMatrix);
		mallet.bindData(colorProgram);
		mallet.draw();
	}

	// suface��ͼ��С�ı�ʱ��GLSurfaceView���ã�suface������
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		// ������ͼ��С
		glViewport(0, 0, width, height);
//		//������������
//		final float aspectRatio = width>height?
//				(float)width/(float)height :
//				(float)height/(float)width;
//				
//		if(width>height){
//			//land
//			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1.0f, 1.0f, -1.0f, 1.0f);
//		} else {
//			//prot or square
//			orthoM(projectionMatrix, 0, -1.0f, 1.0f, -aspectRatio, aspectRatio,-1.0f, 1.0f);
//		}
//		System.out.println("aspectRatio: " + aspectRatio);
		//����͸��ͶӰ����
		perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1f, 10f);
		//����ģ�;����ƶ�����ת����
		setIdentityM(modelMatrix, 0);
		translateM(modelMatrix, 0, 0f, 0f, -2.5f);
		rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
		//͸��ͶӰ������ģ�;�����˸�ֵ��projectionMatrix
		final float[] temp = new float[16];
		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
	}

	// suface����ʱ��GLSurfaceView����
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		// �����Ļ����ɫ
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		table = new Table();
		mallet = new Mallet();
		
		textureProgram = new TextureShaderProgram(context);
		colorProgram = new ColorShaderProgram(context);
		
		texture = TextureHelper.loadTexture(context, R.drawable.ic_launcher);
	}

}
