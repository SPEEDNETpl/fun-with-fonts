@file:SuppressLint("NewApi")

package com.mikolajkakol.fontui

import android.annotation.SuppressLint
import android.graphics.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import kotlin.math.abs

@Composable
fun ShaderPerformance1() = repeat(REPEATS) {
    val shader = remember {
        RuntimeShader(SHADER_ANIM_COLOR)
            .apply { setFloatUniform("iDuration", DURATION) }
    }
    val brush = remember { ShaderBrush(shader) }
    val time by timeAnimation()
    shader.setFloatUniform("iTime", time)

    Text(
        text = prefText,
        style = TextStyle(brush = brush),
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
fun ShaderPerformance2() = repeat(REPEATS) {
    class AnimShaderBrush(val time: Float = -1f) : ShaderBrush() {
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

        fun setTime(newTime: Float): AnimShaderBrush {
            return AnimShaderBrush(newTime).apply {
                this@apply.internalShader = this@AnimShaderBrush.internalShader
                this@apply.previousSize = this@AnimShaderBrush.previousSize
            }
        }

        override fun equals(other: Any?): Boolean {
            if (other !is AnimShaderBrush) return false
            if (other.internalShader != this.internalShader) return false
            if (other.previousSize != this.previousSize) return false
            if (other.time != this.time) return false
            return true
        }
    }

    var brush by remember { mutableStateOf(AnimShaderBrush()) }
    val time by timeAnimation()

    LaunchedEffect(time) {
        brush = brush.setTime(time)
    }

    Text(
        text = prefText,
        style = TextStyle(brush = brush),
    )
}

@Composable
fun ShaderPerformance3() = repeat(REPEATS) {
    data class Info(val layout: TextLayoutResult, val width: Float, val height: Float)

    val shader = remember {
        RuntimeShader(SHADER_ANIM_COLOR)
            .apply { setFloatUniform("iDuration", DURATION) }
    }
    val brush = remember { ShaderBrush(shader) }
    val time by timeAnimation()

    val textMeasurer = rememberTextMeasurer()
    val info = remember(prefText) {
        textMeasurer.measure(
            text = AnnotatedString(prefText),
            style = TextStyle(brush = brush)
        ).let { textLayout ->
            val lines = (0 until textLayout.lineCount)
            val start = lines.minOf { textLayout.getLineLeft(it) }
            val end = lines.maxOf { textLayout.getLineRight(it) }
            val top = textLayout.getLineTop(lines.first)
            val bottom = textLayout.getLineBottom(lines.last)
            val width = abs(end - start)
            val height = bottom - top
            shader.setFloatUniform("iResolution", width, height)
            Info(textLayout, width, height)
        }
    }
    val wdp = with(LocalDensity.current) { info.width.toDp() }
    val hdp = with(LocalDensity.current) { info.height.toDp() }

    Canvas(
        Modifier
            .size(wdp, hdp)
    ) {
        shader.setFloatUniform("iTime", time)
        drawText(info.layout, brush)
    }
}


private const val REPEATS = 30
private const val DURATION = 2000f
private const val SHADER_ANIM_COLOR = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform float iDuration;

    half4 main(in float2 fragCoord) {
      float2 scaled = abs(1.0-mod(fragCoord/iResolution.xy+iTime/(iDuration/2.0),2.0));
      return half4(scaled, 0, 1.0);
    }
"""

private val prefText = (demoText + "\n").repeat(10).trim()

@Composable
private fun timeAnimation() = rememberInfiniteTransition().animateFloat(
    initialValue = 0f,
    targetValue = DURATION,
    animationSpec = infiniteAnimation
)

private val infiniteAnimation = infiniteRepeatable<Float>(
    tween(DURATION.toInt(), easing = LinearEasing),
    RepeatMode.Restart
)

