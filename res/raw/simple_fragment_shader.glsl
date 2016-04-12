//片段着色器/res/raw/simple_fragment_shader.glsl
//precision为精度限定符：可以选择lowp、mediump、highp;顶点着色器默认为hitghp
precision mediump float;

uniform vec4 u_Color;

void main()
{
	gl_FragColor = u_Color;
}