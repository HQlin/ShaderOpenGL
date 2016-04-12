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
	private static final int POSITION_COMPINENT_COUNT = 2;// 坐标xy2个值
	private static final int BYTES_PER_FLOAT = 4;// float类型由4个byte构成
	private final FloatBuffer vertexData;// 本地系统存储点的数据
	private int program;// 着色器的链接程序
	private static final String U_COLOR = "u_Color";
	private int uColorLocation;
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;

	public MyRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		// 三角形的卷曲顺序：逆时针描点，指出物体前后区分（即前面物体会挡住后面物体）
		float[] datas = {
				// table
				-0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,

				-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,

				// line
				-0.5f, 0.0f, 0.5f, 0.0f,

				// mallets
				0.0f, -0.25f, 0.0f, 0.25f };

		//吧内存从java堆复制到本地堆，因为opengl是在本地系统操作的
		//申请本地系统内存
		vertexData = ByteBuffer.allocateDirect(datas.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		//赋值
		vertexData.put(datas);
	}

	// 绘制每一帧时，GLSurfaceView调用（suface创建后）
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		// 清除屏幕颜色
		glClear(GL_COLOR_BUFFER_BIT);

		// 绘制table
		glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		// 绘制分割线
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_LINES, 6, 2);
		// 绘制蓝色的mallet
		glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 8, 1);
		// 绘制红色的mallet
		glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_POINTS, 9, 1);

	}

	// suface视图大小改变时，GLSurfaceView调用（suface创建后）
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		// 设置视图大小
		glViewport(0, 0, width, height);
	}

	// suface创建时，GLSurfaceView调用
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		// 清空屏幕的颜色
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// 顶点着色器与片段着色器代码
		String vertexShaderSoure = TRReader.readTFfromRes(context, R.raw.simple_vertex_shader);
		String fragmentShaderSoure = TRReader.readTFfromRes(context, R.raw.simple_fragment_shader);
		// 获取对对应着色器
		int vertexShader = ShaderHelper.conpileVertexShader(vertexShaderSoure);
		int fragmentShader = ShaderHelper.conpileFragmentShader(fragmentShaderSoure);
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		if (LoggerConfig.ON) {
			ShaderHelper.validateProgram(program);
		}
		//使用自定义的程序
		glUseProgram(program);
		// 获得uniform与attribute位置
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		// 关联顶点属性
		vertexData.position(0);
		glVertexAttribPointer(aPositionLocation, // 属性位置
				POSITION_COMPINENT_COUNT, // 顶点参数个数
				GL_FLOAT, // 顶点数据类型
				false, // 整型数据才有意义
				0, // 多余一个属性才有意义
				vertexData);// 数据内存
		// 使能顶点数组
		glEnableVertexAttribArray(aPositionLocation);
		
	}

}
