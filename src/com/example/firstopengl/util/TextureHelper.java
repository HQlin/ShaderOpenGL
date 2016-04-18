package com.example.firstopengl.util;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

import com.example.firstopengl.LoggerConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TextureHelper {
	private static final String TAG = "TestureHelper";
	/**
	 * 加载纹理
	 * @param context    
	 * @param resourceId  资源id
	 * @return            纹理数据
	 */
	public static int loadTexture(Context context, int resourceId){		
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);
		
		if(textureObjectIds[0] == 0){
			if(LoggerConfig.ON){
				Log.w(TAG, "不能创建纹理对象");
			}
			return 0;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;//图像原始数据
		
		final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		
		if(bitmap==null){
			if(LoggerConfig.ON){
				Log.w(TAG, "资源不能被加载");
			}
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		//绑定纹理，才可进行对纹理操作
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
		//opengl纹理过滤模式 http://blog.csdn.net/pizi0475/article/details/49740879
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);	
		//加载位图数据到纹理
		texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		//生成MIP贴图
		glGenerateMipmap(GL_TEXTURE_2D);
		//解除纹理绑定
		glBindTexture(GL_TEXTURE_2D, 0);
		
		return textureObjectIds[0];
	}
}
