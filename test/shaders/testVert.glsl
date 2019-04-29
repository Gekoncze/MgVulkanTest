#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inUv;
layout(location = 2) in vec4 inColor;

layout(location = 0) out vec2 outUv;
layout(location = 1) out vec4 outColor;

layout(binding = 0) uniform UniformBufferObject {
    mat4 matrix;
} matrix;

void main() {
    gl_Position = matrix.matrix * vec4(inPosition, 1.0);
    outUv = inUv;
    outColor = inColor;
}