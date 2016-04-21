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

	//投影矩阵，即三维空间在屏幕上的显示方式
	private final float[] projectionMatrix = new float[16];	
	//模型矩阵,即旋转、缩放、移动操作
	private final float[] modelMatrix = new float[16];	
	//视图矩阵，即摄像头相关设置
	private final float[] viewMatrix = new float[16];
	//视图投影矩阵，即 视图矩阵*投影矩阵
	private final float[] viewProjectionMatrix = new float[16];
	//模型视图投影矩阵，该矩阵最后传递给着色器，即 模型矩阵*视图矩阵*投影矩阵 
	private final float[] modelViewProjectionMatrix = new float[16];
	
	//着色器程序
	private TextureShaderProgram textureProgram;
	private ColorShaderProgram colorProgram;
	//纹理
	private int texture;
	
	//objs
	private Table table;
	private Puck puck;
	private Mallet mallet;

	public MyRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	// 绘制每一帧时，GLSurfaceView调用（suface创建后）
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		// 清除屏幕颜色
		glClear(GL_COLOR_BUFFER_BIT);
		
		//绘制table
		positionTableInScene();
		textureProgram.useProgram();
		textureProgram.setUniforms(modelViewProjectionMatrix, texture);
		table.bindData(textureProgram);
		table.draw();
		
		//绘制mallet
		positionObjInScene(0f, mallet.height/2f, -0.4f);
		colorProgram.useProgram();
		colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
		mallet.bindData(colorProgram);
		mallet.draw();
		
		positionObjInScene(0f, mallet.height/2f, 0.4f);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
		mallet.draw();
		
		//绘制puck
		positionObjInScene(0f, mallet.height/2f, 0f);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
		puck.bindData(colorProgram);
		puck.draw();
	}

	// suface视图大小改变时，GLSurfaceView调用（suface创建后）
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		// 设置视图大小
		glViewport(0, 0, width, height);
//		//创建正交矩阵
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
		//创建透视投影矩阵
		perspectiveM(projectionMatrix, 0, 
				45, 						//摄像头焦距，即观察视野大小
				(float)width/(float)height, //屏幕的宽高比
				1f, 10f);					//摄像头到近远平面的距离
		//创建视图矩阵，即摄像头相关设置
		setLookAtM(viewMatrix, 0, 
				0f, 1.2f, 2.2f, //摄像头位置
				0f, 0f, 0f,		//观察点
				0f, 1f, 0f);    //摄像头正上方
		//视图投影矩阵  = 投影矩阵 * 视图矩阵
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
	}

	// suface创建时，GLSurfaceView调用
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		// 清空屏幕的颜色
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		mallet = new Mallet(0.08f, 0.15f, 32);
		puck = new Puck(0.06f, 0.02f, 32);
		table = new Table();
		
		textureProgram = new TextureShaderProgram(context);
		colorProgram = new ColorShaderProgram(context);
		
		texture = TextureHelper.loadTexture(context, R.drawable.ic_launcher);
	}

	/**
	 * table坐标作模型矩阵变换，即绕x轴旋转-90度       
	 * 模型视图投影矩阵 = 模型矩阵*视图矩阵*投影矩阵
	 */
	private void positionTableInScene(){
		setIdentityM(modelMatrix, 0);
		rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
		multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0,
				modelMatrix, 0);
	}
	
	/**
	 * 物体坐标作模型矩阵变换，即在前者modelMatrix矩阵上再作移动操作来确定下面物体绘制的位置
	 * 模型视图投影矩阵 = 模型矩阵*视图矩阵*投影矩阵
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
