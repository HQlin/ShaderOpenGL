//Ƭ����ɫ��/res/raw/simple_fragment_shader.glsl
//precisionΪ�����޶���������ѡ��lowp��mediump��highp;������ɫ��Ĭ��Ϊhitghp
precision mediump float;
//glUniform4f��������u_Color��ֵ
uniform vec4 u_Color;
//����������ɫ
varying vec4 v_Color;

void main()
{
	//gl_FragColor = u_Color;
	gl_FragColor = v_Color;
}