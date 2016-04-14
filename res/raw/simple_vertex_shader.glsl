//顶点着色器/res/raw/simple_vertex_shader.glsl
//glUniformMatrix4fv方法设置u_Matrix的值
uniform mat4 u_Matrix;

attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

void main()
{
	v_Color = a_Color;
	
	gl_Position = u_Matrix * a_Position;
	gl_PointSize = 10.0;
}