//¶¥µã×ÅÉ«Æ÷/res/raw/simple_vertex_shader.glsl
attribute vec4 a_Position;

void main()
{
	gl_Position = a_Position;
	gl_PointSize = 10.0;
}