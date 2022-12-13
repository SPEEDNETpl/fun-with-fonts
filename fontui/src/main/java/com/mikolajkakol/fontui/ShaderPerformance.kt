package com.mikolajkakol.fontui

import android.graphics.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.sp

private const val DURATION = 200f
private const val SHADER_ANIM_COLOR = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform float iDuration;

    half4 main(in float2 fragCoord) {
      float2 scaled = abs(1.0-mod(fragCoord/iResolution.xy+iTime/(iDuration/2.0),2.0));
      return half4(scaled, 0, 1.0);
    }
"""

private val infiniteAnimation = infiniteRepeatable<Float>(
    tween(DURATION.toInt(), easing = LinearEasing),
    RepeatMode.Restart
)

@Composable
fun ShaderPerformance1() = Column {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val shader = remember {
        RuntimeShader(SHADER_ANIM_COLOR)
            .apply { setFloatUniform("iDuration", DURATION) }
    }
    val brush = remember { ShaderBrush(shader) }
    val time by timeAnimation()
    shader.setFloatUniform("iTime", time)

    RenderText(
        brush = brush,
        modifier = Modifier
            .onSizeChanged {
                shader.setFloatUniform(
                    "iResolution",
                    it.width.toFloat(),
                    it.height.toFloat()
                )
            }
            .alpha(1 - (time + 1) / 1000 / DURATION),
    )
}


@Composable
fun ShaderPerformance2() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    var brush by remember { mutableStateOf(AnimatableShaderBrush()) }
    val time by timeAnimation()

    LaunchedEffect(time) {
        brush = brush.setTime(time)
    }

    RenderText(
        brush = brush,
    )
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class AnimatableShaderBrush(val time: Float = -1f) : ShaderBrush() {
    private var internalShader: RuntimeShader? = null
    private var previousSize: Size? = null

    override fun createShader(size: Size): Shader {
        val shader = if (internalShader == null || previousSize != size) {
            RuntimeShader(SHADER_ANIM_COLOR).apply {
                setFloatUniform("iResolution", size.width, size.height)
                setFloatUniform("iDuration", DURATION)
            }
        } else {
            internalShader!!
        }
        shader.setFloatUniform("iTime", time)
        internalShader = shader
        previousSize = size
        return shader
    }

    fun setTime(newTime: Float): AnimatableShaderBrush {
        return AnimatableShaderBrush(newTime).apply {
            this@apply.internalShader = this@AnimatableShaderBrush.internalShader
            this@apply.previousSize = this@AnimatableShaderBrush.previousSize
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AnimatableShaderBrush) return false
        if (other.internalShader != this.internalShader) return false
        if (other.previousSize != this.previousSize) return false
        if (other.time != this.time) return false
        return true
    }
}

@Composable
private fun timeAnimation() = rememberInfiniteTransition().animateFloat(
    initialValue = 0f,
    targetValue = DURATION,
    animationSpec = infiniteAnimation
)

@Composable
private fun RenderText(brush: Brush, lines: Int = 4, modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        withStyle(ParagraphStyle(lineHeight = 12.sp)) {
            append((demoText + "\n").repeat(lines).trim())
        }
    }
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(brush = brush),
    )
}
