package com.example.firstopengl;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	private GLSurfaceView glSurfaceView;
	private boolean rendererSet;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//1.创建GLSurfaceView实例
		glSurfaceView = new GLSurfaceView(this);
		//2.检查系统是否支持opengl es2.0
		final ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo ci = am.getDeviceConfigurationInfo();
		final boolean supportsEs2 = ci.reqGlEsVersion >= 0x20000;
		//3.为opengl es2.0配置渲染表面
		if(supportsEs2){
			glSurfaceView.setEGLContextClientVersion(2);
			glSurfaceView.setRenderer(new MyRenderer(this));
			rendererSet = true;
		} else {
			Toast.makeText(this, "设备不支持opengl es 2.0", Toast.LENGTH_SHORT).show();
			return;
		}		
		//4.显示GLSurfaceView
		setContentView(glSurfaceView);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(rendererSet){
			glSurfaceView.onResume();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(rendererSet){
			glSurfaceView.onPause();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
