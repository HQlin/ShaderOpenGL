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
	private static final int POSITION_COMPINENT_COUNT = 4;// 坐标xyzw4个值
	private static final int BYTES_PER_FLOAT = 4;// float类型由4个byte构成
	private final FloatBuffer vertexData;// 本地系统存储点的数据
	private int program;// 着色器的链接程序
//	private static final String U_COLOR = "u_Color";
//	private int uColorLocation;
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;
	
	private static final String A_COLOR = "a_Color";
	private int aColorLocation;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int STRIDE = (POSITION_COMPINENT_COUNT+COLOR_COMPONENT_COUNT)*BYTES_PER_FLOAT;
	
	//正交投影来修复横竖屏幕显示比例
	private static final String U_MATRIX = "u_Matrix";
	private int uMatrixLocation;
	private final float[] projectionMatrix = new float[16];
	
	//模型矩阵
	private final float[] modelMatrix = new float[16];

	public MyRenderer(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		// 三角形的卷曲顺序：逆时针描点，指出物体前后区分（即前面物体会挡住后面物体）
		//默认屏幕坐标：左下角（-1，-1），右下角（1，-1），右上角（1,1），左上角（-1,1），中心点（0,0）
		float[] datas = {
				//Order of coordinates:X.Y,Z,W.R.G.B
				// table 三角形扇
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

		//投影矩阵传值给着色器
		glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
		// 绘制table
		//glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
		// 绘制分割线
		//glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_LINES, 6, 2);
		// 绘制蓝色的mallet
		//glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_POINTS, 8, 1);
		// 绘制红色的mallet
		//glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
		glDrawArrays(GL_POINTS, 9, 1);

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
		perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1f, 10f);
		//利用模型矩阵移动和旋转物体
		setIdentityM(modelMatrix, 0);
		translateM(modelMatrix, 0, 0f, 0f, -2.5f);
		rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
		//透视投影矩阵与模型矩阵相乘赋值给projectionMatrix
		final float[] temp = new float[16];
		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
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
		//uColorLocation = glGetUniformLocation(program, U_COLOR);
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		aColorLocation = glGetAttribLocation(program, A_COLOR);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		// 关联顶点属性
		vertexData.position(0);
//		glVertexAttribPointer(aPositionLocation, // 属性位置
//				POSITION_COMPINENT_COUNT, // 顶点参数个数
//				GL_FLOAT, // 顶点数据类型
//				false, // 整型数据才有意义
//				0, // 多余一个属性才有意义
//				vertexData);// 数据内存
		glVertexAttribPointer(aPositionLocation, // 属性坐标数据位置
				POSITION_COMPINENT_COUNT, // 顶点参数个数
				GL_FLOAT, // 顶点数据类型
				false, // 整型数据才有意义
				STRIDE, // 多余一个属性才有意义,此处有顶点坐标和颜色
				vertexData);// 数据内存
		vertexData.position(POSITION_COMPINENT_COUNT);
		glVertexAttribPointer(aColorLocation, // 属性颜色数据位置
				COLOR_COMPONENT_COUNT, // 顶点参数个数
				GL_FLOAT, // 顶点数据类型
				false, // 整型数据才有意义
				STRIDE, // 多余一个属性才有意义,此处有顶点坐标和颜色
				vertexData);// 数据内存
		//使能顶点属性坐标数组
		glEnableVertexAttribArray(aPositionLocation);
		//使能顶点属性颜色数组
		glEnableVertexAttribArray(aColorLocation);
		
	}

}
