package com.example.firstopengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

public class MyRenderer implements Renderer {
	private final Context context;
	private static final int POSITION_COMPINENT_COUNT = 4;// ����xyzw4��ֵ
	private static final int BYTES_PER_FLOAT = 4;// float������4��byte����
	private final FloatBuffer vertexData;// ����ϵͳ�洢�������
	private int program;// ��ɫ�������ӳ���
//	private static final String U_COLOR = "u_Color";
//	private int uColorLocation;
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;
	
	private static final String A_COLOR = "a_Color";
	private int aColorLocation;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPINENT_COUNT+COLOR_COMPONENT_COUNT)*BYTES_PER_FLOAT;
	
	//����ͶӰ���޸�������Ļ��ʾ����
	private static final String U_MATRIX = "u_Matrix";
	private int uMatrixLocation;
	private final float[] projectionMatrix = new float[16];
	
	//ģ�;���
	private final float[] modelMatrix = new float[16];

	public MyRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		// �����εľ���˳����ʱ����㣬ָ������ǰ�����֣���ǰ������ᵲס�������壩
		//Ĭ����Ļ���꣺���½ǣ�-1��-1�������½ǣ�1��-1�������Ͻǣ�1,1�������Ͻǣ�-1,1�������ĵ㣨0,0��
		float[] datas = {
				//Order of coordinates:X.Y,Z,W.R.G.B
				// table ��������
				 0.0f,  0.0f, 0.0f, 1.5f, 1.0f, 1.0f, 1.0f,

				-0.5f, -0.8f, 0.0f, 1.0f, 0.5f, 0.5f, 0.5f,
				 0.5f, -0.8f, 0.0f, 1.0f, 0.5f, 0.5f, 0.5f,
				 0.5f,  0.8f, 0.0f, 2.0f, 0.5f, 0.5f, 0.5f,
				-0.5f,  0.8f, 0.0f, 2.0f, 0.5f, 0.5f, 0.5f,
				-0.5f, -0.8f, 0.0f, 1.0f, 0.5f, 0.5f, 0.5f,

				// line
				-0.5f, 0.0f, 0.0f, 1.5f, 1.0f, 0.0f, 0.0f,
				 0.5f, 0.0f, 0.0f, 1.5f, 1.0f, 0.0f, 0.0f,

				// mallets
				0.0f, -0.4f, 0.0f, 1.25f, 0.0f, 0.0f, 1.0f,
				0.0f,  0.4f, 0.0f, 1.75f, 1.0f, 0.0f, 0.0f
				};

		//���ڴ��java�Ѹ��Ƶ����ضѣ���Ϊopengl���ڱ���ϵͳ������
		//���뱾��ϵͳ�ڴ�
		vertexData = ByteBuffer.allocateDirect(datas.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		//��ֵ
		vertexData.put(datas);
	}

	// ����ÿһ֡ʱ��GLSurfaceView���ã�suface������
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		// �����Ļ��ɫ
		glClear(GL_COLOR_BUFFER_BIT);

		//ͶӰ����ֵ����ɫ��
		glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
		// ����table
		//glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
		// ���Ʒָ���
		//glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_LINES, 6, 2);
		// ������ɫ��mallet
		//glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 8, 1);
		// ���ƺ�ɫ��mallet
		//glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_POINTS, 9, 1);

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
		// ������ɫ����Ƭ����ɫ������
		String vertexShaderSoure = TRReader.readTFfromRes(context, R.raw.simple_vertex_shader);
		String fragmentShaderSoure = TRReader.readTFfromRes(context, R.raw.simple_fragment_shader);
		// ��ȡ�Զ�Ӧ��ɫ��
		int vertexShader = ShaderHelper.conpileVertexShader(vertexShaderSoure);
		int fragmentShader = ShaderHelper.conpileFragmentShader(fragmentShaderSoure);
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		if (LoggerConfig.ON) {
			ShaderHelper.validateProgram(program);
		}
		//ʹ���Զ���ĳ���
		glUseProgram(program);
		// ���uniform��attributeλ��
		//uColorLocation = glGetUniformLocation(program, U_COLOR);
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		aColorLocation = glGetAttribLocation(program, A_COLOR);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		// ������������
		vertexData.position(0);
//		glVertexAttribPointer(aPositionLocation, // ����λ��
//				POSITION_COMPINENT_COUNT, // �����������
//				GL_FLOAT, // ������������
//				false, // �������ݲ�������
//				0, // ����һ�����Բ�������
//				vertexData);// �����ڴ�
		glVertexAttribPointer(aPositionLocation, // ������������λ��
				POSITION_COMPINENT_COUNT, // �����������
				GL_FLOAT, // ������������
				false, // �������ݲ�������
				STRIDE, // ����һ�����Բ�������,�˴��ж����������ɫ
				vertexData);// �����ڴ�
		vertexData.position(POSITION_COMPINENT_COUNT);
		glVertexAttribPointer(aColorLocation, // ������ɫ����λ��
				COLOR_COMPONENT_COUNT, // �����������
				GL_FLOAT, // ������������
				false, // �������ݲ�������
				STRIDE, // ����һ�����Բ�������,�˴��ж����������ɫ
				vertexData);// �����ڴ�
		//ʹ�ܶ���������������
		glEnableVertexAttribArray(aPositionLocation);
		//ʹ�ܶ���������ɫ����
		glEnableVertexAttribArray(aColorLocation);
		
	}

}
