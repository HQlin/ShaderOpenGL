package com.example.firstopengl.objs;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

import java.util.ArrayList;
import java.util.List;

import com.example.firstopengl.util.Geometry.Circle;
import com.example.firstopengl.util.Geometry.Cylinder;
import com.example.firstopengl.util.Geometry.Point;

import android.util.FloatMath;

public class ObjsBuilder {
	private static final int FLOATS_PER_VERTEX = 3;
	private final float[] vertexData;
	private int offset = 0;
	private final List<DrawCmd> drawList = new ArrayList<ObjsBuilder.DrawCmd>();
	
	public ObjsBuilder(int sizeInVertices) {
		// TODO Auto-generated constructor stub
		vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
	}
	
	/**
	 * 绘制平面圆（中心点+周边点+起始重合点）
	 * @param numPoints
	 * @return
	 */
	private static int sizeOfCircleInVertices(int numPoints){
		return 1+(numPoints+1);
	}
	
	/**
	 * 绘制圆柱侧面（周边点+起始重合点）
	 * @param numPoints
	 * @return
	 */
	private static int sizeOfOpenCylinderInVertices(int numPoints){
		return (numPoints+1)*2;
	}
	
	/**
	 * 使用圆柱创建冰球
	 * @param puck
	 * @param numPoints
	 * @return
	 */
	static GeneratedData createPuck(Cylinder puck, int numPoints){
		int size = sizeOfCircleInVertices(numPoints) 
				+ sizeOfOpenCylinderInVertices(numPoints);
		
		ObjsBuilder builder = new ObjsBuilder(size);
		
		Circle puckTop = new Circle(
				puck.center.translateY(puck.height/2f),
				puck.radius);
		
		builder.appendCircle(puckTop, numPoints);
		builder.appendOpenCylinder(puck, numPoints);
		
		return builder.build();
	}
	
	/**
	 * 使用2个不同打圆柱体创建木槌
	 * @param center
	 * @param radius
	 * @param height
	 * @param numPoints
	 * @return
	 */
	static GeneratedData createMallet(Point center, float radius, float height, int numPoints){
		int size = sizeOfCircleInVertices(numPoints)*2
				+ sizeOfOpenCylinderInVertices(numPoints)*2;
		
		ObjsBuilder builder = new ObjsBuilder(size);
		
		float baseHeight = height*0.25f;
		
		Circle baseCircle = new Circle(center.translateY(-baseHeight), radius);
		Cylinder baseCylinder = new Cylinder(baseCircle.center.translateY(-baseHeight/2f),
				radius, baseHeight);
		
		builder.appendCircle(baseCircle, numPoints);
		builder.appendOpenCylinder(baseCylinder, numPoints);
		
		float handleHeight = height*0.75f;
		float handleRadius = radius/3f;
		
		Circle handleCircle = new Circle(center.translateY(height*0.5f), handleRadius);
		Cylinder handleCylinder = new Cylinder(handleCircle.center.translateY(-handleHeight/2f), handleRadius, handleHeight);
		
		builder.appendCircle(handleCircle, numPoints);
		builder.appendOpenCylinder(handleCylinder, numPoints);
		
		return builder.build();
	}

	private GeneratedData build() {
		// TODO Auto-generated method stub
		return new GeneratedData(vertexData, drawList);
	}

	static class GeneratedData {
		// TODO Auto-generated method stub
		final float[] vertexData;
		final List<DrawCmd> drawList;
		
		GeneratedData(float[] vertexData, List<DrawCmd> drawList) {
			// TODO Auto-generated constructor stub
			this.vertexData = vertexData;
			this.drawList = drawList;
		}
	}

	/**
	 * 使用三角形带绘制圆柱侧面
	 * @param cylinder
	 * @param numPoints
	 */
	private void appendOpenCylinder(Cylinder cylinder, int numPoints) {
		// TODO Auto-generated method stub
		final int startVertex = offset / FLOATS_PER_VERTEX;
		final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
		final float yStart = cylinder.center.y - (cylinder.height/2f);
		final float yEnd = cylinder.center.y + (cylinder.height/2f);
		
		for(int i = 0;i<=numPoints; i++){
			float angleInRadians = 
					((float)i/(float)numPoints)
					*((float)Math.PI*2f);
			float xPosition = 
					cylinder.center.x
					+ cylinder.radius*FloatMath.cos(angleInRadians);
			float zPosition = 
					cylinder.center.z
					+ cylinder.radius*FloatMath.sin(angleInRadians);
			
			vertexData[offset++] = xPosition;
			vertexData[offset++] = yStart;
			vertexData[offset++] = zPosition;
			
			vertexData[offset++] = xPosition;
			vertexData[offset++] = yEnd;
			vertexData[offset++] = zPosition;
			
			drawList.add(new DrawCmd() {
				
				@Override
				public void draw() {
					// TODO Auto-generated method stub
					glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
				}
			});
		}
	}

	/**
	 * 使用三角形扇绘制平面圆
	 * @param circle
	 * @param numPoints
	 */
	private void appendCircle(Circle circle, int numPoints) {
		// TODO Auto-generated method stub
		final int startVertex = offset / FLOATS_PER_VERTEX;
		final int numVertices = sizeOfCircleInVertices(numPoints);
		//平面圆中心
		vertexData[offset++] = circle.center.x;
		vertexData[offset++] = circle.center.y;
		vertexData[offset++] = circle.center.z;
		
		//使用单位圆计算平面圆的周边点
		for(int i = 0; i<=numPoints; i++){
			float angleInRadians =
					((float)i/(float) numPoints)
					* ((float) Math.PI*2f);
			
			vertexData[offset++] = circle.center.x
					+ circle.radius * FloatMath.cos(angleInRadians);
			vertexData[offset++] = circle.center.y;
			vertexData[offset++] = circle.center.z
					+ circle.radius * FloatMath.sin(angleInRadians);
		}
		
		drawList.add(new DrawCmd() {
			
			@Override
			public void draw() {
				// TODO Auto-generated method stub
				glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
			}
		});
	}
	
	static interface DrawCmd{
		void draw();
	}
	
	
}
