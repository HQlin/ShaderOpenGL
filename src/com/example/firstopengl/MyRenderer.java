package com.example.firstopengl;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttrib1f;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

@SuppressWarnings("unused")
public class MyRenderer implements Renderer {
	private final Context context;
	private static final int POSITION_COMPINENT_COUNT = 2;// ����xy2��ֵ
	private static final int BYTES_PER_FLOAT = 4;// float������4��byte����
	private final FloatBuffer vertexData;// ����ϵͳ�洢�������
	private int program;// ��ɫ�������ӳ���
	private static final String U_COLOR = "u_Color";
	private int uColorLocation;
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;

	public MyRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		// �����εľ���˳����ʱ����㣬ָ������ǰ�����֣���ǰ������ᵲס�������壩
		float[] datas = {
				// table
				-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,

				-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,

				// line
				-0.5f, 0.0f, 0.5f, 0.0f,

				// mallets
				0.0f, -0.25f, 0.0f, 0.25f };

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

		// ����table
		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		// ���Ʒָ���
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_LINES, 6, 2);
		// ������ɫ��mallet
		glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 8, 1);
		// ���ƺ�ɫ��mallet
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_POINTS, 9, 1);

	}

	// suface��ͼ��С�ı�ʱ��GLSurfaceView���ã�suface������
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		// ������ͼ��С
		glViewport(0, 0, width, height);
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
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		// ������������
		vertexData.position(0);
		glVertexAttribPointer(aPositionLocation, // ����λ��
				POSITION_COMPINENT_COUNT, // �����������
				GL_FLOAT, // ������������
				false, // �������ݲ�������
				0, // ����һ�����Բ�������
				vertexData);// �����ڴ�
		// ʹ�ܶ�������
		glEnableVertexAttribArray(aPositionLocation);
		
	}

}
