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
	
	//�ж��Ƿ񴥿ص�mallet
	private boolean malletPressed = false;
	//��ɫmallet����
	private Point blueMalletPosition;
	//��ת��ͼͶӰ����
	private final float[] invertedViewProjectionMatrix = new float[16];

	//�߽�
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

	// ����ÿһ֡ʱ��GLSurfaceView���ã�suface������
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		//puck������������ƶ�
		puckPosition = puckPosition.translate(puckVector);
		if(puckPosition.x < leftBound+puck.radius || puckPosition.x>rightBound-puck.radius){
			puckVector = new Vector(-puckVector.x, puckVector.y, puckVector.z);
			//Ϊ�����������ֵ
			puckVector = puckVector.scale(0.90f);
		}
		if(puckPosition.z < farBound+puck.radius || puckPosition.z>nearBound-puck.radius){
			puckVector = new Vector(puckVector.x, puckVector.y, -puckVector.z);
			//Ϊ�����������ֵ
			puckVector = puckVector.scale(0.90f);
		}
		puckPosition = new Point(
				clamp(puckPosition.x, leftBound+puck.radius, rightBound-puck.radius), 
				puckPosition.y, 
				clamp(puckPosition.z, farBound+puck.radius, nearBound-puck.radius));
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
		
		positionObjInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
		mallet.draw();
		
		//����puck
		positionObjInScene(puckPosition.x, puckPosition.y, puckPosition.z);
		colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
		puck.bindData(colorProgram);
		puck.draw();
		
		//Ϊ�����������ֵ
		puckVector = puckVector.scale(0.99f);
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
		//���÷�ת��ͼͶӰ����
		invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
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
		
		texture = TextureHelper.loadTexture(context, R.drawable.bg);
		
		blueMalletPosition = new Point(0f, mallet.height/2f, 0.4f);
		puckPosition = new Point(0f, puck.height/2f, 0f);
		puckVector = new Vector(0f, 0f, 0f);
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
	
	/**
	 * ���ذ����¼�
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
	 * ��һ������ת������ά�ռ������
	 * @param normalizedX
	 * @param normalizedY
	 * @return
	 */
	private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){
		//�����һ������Ϊ�����ͶӰ��Զƽ����
		final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1}; 
		final float[] farPointNdc = {normalizedX, normalizedY, 1, 1}; 
		
		final float[] nearPointWorld = new float[4];
		final float[] farPointWorld = new float[4];
		//����͸�ӳ���
		multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
		multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
		
		divideByW(nearPointWorld);
		divideByW(farPointWorld);
		//�õ�������ͶӰ��Զƽ���ϵĽ���
		Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
		Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
		//�õ�����
		return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
	}

	/**
	 * ����͸�ӳ�����Ӱ��
	 * @param vector
	 */
	private void divideByW(float[] vector) {
		// TODO Auto-generated method stub
		vector[0] /= vector[3];
		vector[1] /= vector[3];
		vector[2] /= vector[3];
	}

	/**
	 * �����϶��¼�
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
	 * ����ȡֵ��Χ
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	private float clamp(float value, float min, float max){
		return Math.min(max, Math.max(value, min));
	}
}
