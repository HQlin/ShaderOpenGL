//片段着色器/res/raw/simple_fragment_shader.glsl
//precision为精度限定符：可以选择lowp、mediump、highp;顶点着色器默认为hitghp
precision mediump float;
//glUniform4f方法设置u_Color的值
uniform vec4 u_Color;
//各定点混合颜色
varying vec4 v_Color;

void main()
{
	//gl_FragColor = u_Color;
	gl_FragColor = v_Color;
}