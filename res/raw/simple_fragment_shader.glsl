//Ƭ����ɫ��/res/raw/simple_fragment_shader.glsl
//precisionΪ�����޶���������ѡ��lowp��mediump��highp;������ɫ��Ĭ��Ϊhitghp
precision mediump float;

uniform vec4 u_Color;

void main()
{
	gl_FragColor = u_Color;
}