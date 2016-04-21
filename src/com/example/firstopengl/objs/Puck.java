package com.example.firstopengl.objs;

import java.util.List;

import com.example.firstopengl.data.VertexArray;
import com.example.firstopengl.objs.ObjsBuilder.DrawCmd;
import com.example.firstopengl.objs.ObjsBuilder.GeneratedData;
import com.example.firstopengl.programs.ColorShaderProgram;
import com.example.firstopengl.util.Geometry.Cylinder;
import com.example.firstopengl.util.Geometry.Point;


public class Puck {
	private static final int POSITION_COMPONENT_COUNT = 3;
	
	public final float radius, height;
	
	private final VertexArray vertexArray;
	private final List<DrawCmd> drawList;
	
	public Puck(float radius, float height, int numPointsAroundPuck) {
		// TODO Auto-generated constructor stub
		GeneratedData generatedData = ObjsBuilder.createPuck(
				new Cylinder(new Point(0f, 0f, 0f), radius, height),
				numPointsAroundPuck);
		this.radius = radius;
		this.height = height;
		
		vertexArray = new VertexArray(generatedData.vertexData);
		drawList = generatedData.drawList;
	}
	
	public void bindData(ColorShaderProgram colorProgram){
		vertexArray.setVertexAttribPointer(0, 
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, 0);
	}
	
	public void draw(){
		for(DrawCmd cmd : drawList){
			cmd.draw();
		}
	}
}
