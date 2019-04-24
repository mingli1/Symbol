varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;

void main() {
    vec4 color = texture2D(u_sampler2D, v_texCoord0);
    color.rgb = vec3(1.) - color.rgb;
    gl_FragColor = color * v_color;
}