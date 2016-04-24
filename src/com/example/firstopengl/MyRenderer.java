package com.example.firstopengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
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
import com.example.firstopengl.util.Geometry;
import com.example.firstopengl.util.Geometry.Plane;
import com.example.firstopengl.util.Geometry.Point;
import com.example.firstopengl.util.Geometry.Ray;
import com.example.firstopengl.util.Geometry.Sphere;
import com.example.firstopengl.util.Geometry.Vector;
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
	
	//判断是否触控到mallet
	private boolean malletPressed = false;
	//蓝色mallet坐标
	private Point blueMalletPosition;
	//反转视图投影矩阵
	private final float[] invertedViewProjectionMatrix = new float[16];

	//边界
	private final float leftBound = -0.5f;
	private final float rightBound = 0.5f;
	private final float farBound = -0.8f;
	private final float nearBound = 0.8f;
	
	private Point previousBlueMalletPosition;
	private Point puckPosition;
	private Vector puckVector;
	
	
	public MyRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	// 绘制每一帧时，GLSurfaceView调用（suface创建后）
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		//puck坐标根据向量移动
		puckPosition = puckPosition.translate(puckVector);
		if(puckPosition.x < leftBound+puck.radius || puckPosition.x>rightBound-puck.radius){
			puckVector = new Vector(-puckVector.x, puckVector.y, puckVector.z);
			//为向量添加阻尼值
			puckVector = puckVector.scale(0.90f);
		}
		if(puckPosition.z < farBound+puck.radius || puckPosition.z>nearBound-puck.radius){
			puckVector = new Vector(puckVector.x, puckVector.y, -puckVector.z);
			//为向量添加阻尼值
			puckVector = puckVector.scale(0.90f);
		}
		puckPosition = new Point(
				clamp(puckPosition.x, leftBound+puck.radius, rightBound-puck.radius), 
				puckPosition.y, 
				clamp(puckPosition.z, farBound+puck.radius, nearBound-puck.radius));
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
		
		positionObjInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
		mallet.draw();
		
		//绘制puck
		positionObjInScene(puckPosition.x, puckPosition.y, puckPosition.z);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
		puck.bindData(colorProgram);
		puck.draw();
		
		//为向量添加阻尼值
		puckVector = puckVector.scale(0.99f);
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
		//采用反转视图投影矩阵
		invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
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
		
		texture = TextureHelper.loadTexture(context, R.drawable.bg);
		
		blueMalletPosition = new Point(0f, mallet.height/2f, 0.4f);
		puckPosition = new Point(0f, puck.height/2f, 0f);
		puckVector = new Vector(0f, 0f, 0f);
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
	
	/**
	 * 触控按下事件
	 * @param normalizedX
	 * @param normalizedY
	 */
	public void handleTouchPress(float normalizedX, float normalizedY) {
		// TODO Auto-generated method stub
		Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
		
		Sphere mbs = new Sphere(new Point(blueMalletPosition.x,blueMalletPosition.y,
				blueMalletPosition.z), mallet.height/2f);
		
		malletPressed = Geometry.intersects(mbs, ray);
		
		if(LoggerConfig.ON)
			System.out.println("malletPressed :" + malletPressed);
	}
	
	/**
	 * 归一化坐标转化成三维空间的射线
	 * @param normalizedX
	 * @param normalizedY
	 * @return
	 */
	private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){
		//定义归一化坐标为其次在投影近远平面上
		final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1}; 
		final float[] farPointNdc = {normalizedX, normalizedY, 1, 1}; 
		
		final float[] nearPointWorld = new float[4];
		final float[] farPointWorld = new float[4];
		//撤销透视除法
		multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
		multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
		
		divideByW(nearPointWorld);
		divideByW(farPointWorld);
		//得到射线在投影近远平面上的交点
		Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
		Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
		//得到射线
		return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
	}

	/**
	 * 消除透视除法的影响
	 * @param vector
	 */
	private void divideByW(float[] vector) {
		// TODO Auto-generated method stub
		vector[0] /= vector[3];
		vector[1] /= vector[3];
		vector[2] /= vector[3];
	}

	/**
	 * 触控拖动事件
	 * @param normalizedX
	 * @param normalizedY
	 */
	public void handleTouchDrag(float normalizedX, float normalizedY) {
		// TODO Auto-generated method stub
		if(malletPressed){
			previousBlueMalletPosition = blueMalletPosition;
			
			Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
			
			Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
			
			Point touchedPoint = Geometry.intersectionPoint(ray, plane);
			blueMalletPosition = new Point(
					clamp(touchedPoint.x, leftBound+mallet.radius, rightBound-mallet.radius),
					mallet.height/2f, 
					clamp(touchedPoint.z, 0f+mallet.radius, nearBound-mallet.radius));
			
			float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
			
			if(distance<(puck.radius+mallet.radius)){
				puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
			}
		}
	}
	
	/**
	 * 限制取值范围
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	private float clamp(float value, float min, float max){
		return Math.min(max, Math.max(value, min));
	}
}
