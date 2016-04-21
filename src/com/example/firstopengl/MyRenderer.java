package com.example.firstopengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.firstopengl.objs.Mallet;
import com.example.firstopengl.objs.Puck;
import com.example.firstopengl.objs.Table;
import com.example.firstopengl.programs.ColorShaderProgram;
import com.example.firstopengl.programs.TextureShaderProgram;
import com.example.firstopengl.util.TextureHelper;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

public class MyRenderer implements Renderer {
	private final Context context;

	//ͶӰ���󣬼���ά�ռ�����Ļ�ϵ���ʾ��ʽ
	private final float[] projectionMatrix = new float[16];	
	//ģ�;���,����ת�����š��ƶ�����
	private final float[] modelMatrix = new float[16];	
	//��ͼ���󣬼�����ͷ�������
	private final float[] viewMatrix = new float[16];
	//��ͼͶӰ���󣬼� ��ͼ����*ͶӰ����
	private final float[] viewProjectionMatrix = new float[16];
	//ģ����ͼͶӰ���󣬸þ�����󴫵ݸ���ɫ������ ģ�;���*��ͼ����*ͶӰ���� 
	private final float[] modelViewProjectionMatrix = new float[16];
	
	//��ɫ������
	private TextureShaderProgram textureProgram;
	private ColorShaderProgram colorProgram;
	//����
	private int texture;
	
	//objs
	private Table table;
	private Puck puck;
	private Mallet mallet;

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
		positionTableInScene();
		textureProgram.useProgram();
		textureProgram.setUniforms(modelViewProjectionMatrix, texture);
		table.bindData(textureProgram);
		table.draw();
		
		//����mallet
		positionObjInScene(0f, mallet.height/2f, -0.4f);
		colorProgram.useProgram();
		colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
		mallet.bindData(colorProgram);
		mallet.draw();
		
		positionObjInScene(0f, mallet.height/2f, 0.4f);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
		mallet.draw();
		
		//����puck
		positionObjInScene(0f, mallet.height/2f, 0f);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
		puck.bindData(colorProgram);
		puck.draw();
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
		perspectiveM(projectionMatrix, 0, 
				45, 						//����ͷ���࣬���۲���Ұ��С
				(float)width/(float)height, //��Ļ�Ŀ�߱�
				1f, 10f);					//����ͷ����Զƽ��ľ���
		//������ͼ���󣬼�����ͷ�������
		setLookAtM(viewMatrix, 0, 
				0f, 1.2f, 2.2f, //����ͷλ��
				0f, 0f, 0f,		//�۲��
				0f, 1f, 0f);    //����ͷ���Ϸ�
		//��ͼͶӰ����  = ͶӰ���� * ��ͼ����
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
	}

	// suface����ʱ��GLSurfaceView����
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		// �����Ļ����ɫ
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		mallet = new Mallet(0.08f, 0.15f, 32);
		puck = new Puck(0.06f, 0.02f, 32);
		table = new Table();
		
		textureProgram = new TextureShaderProgram(context);
		colorProgram = new ColorShaderProgram(context);
		
		texture = TextureHelper.loadTexture(context, R.drawable.ic_launcher);
	}

	/**
	 * table������ģ�;���任������x����ת-90��       
	 * ģ����ͼͶӰ���� = ģ�;���*��ͼ����*ͶӰ����
	 */
	private void positionTableInScene(){
		setIdentityM(modelMatrix, 0);
		rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
		multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0,
				modelMatrix, 0);
	}
	
	/**
	 * ����������ģ�;���任������ǰ��modelMatrix�����������ƶ�������ȷ������������Ƶ�λ��
	 * ģ����ͼͶӰ���� = ģ�;���*��ͼ����*ͶӰ����
	 * @param x
	 * @param y
	 * @param z
	 */
	private void positionObjInScene(float x, float y, float z){
		setIdentityM(modelMatrix, 0);
		translateM(modelMatrix, 0, x, y, z);
		multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0,
				modelMatrix, 0);
	}
}
