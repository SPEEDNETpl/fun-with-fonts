package com.mikolajkakol.fontui

import android.graphics.*
import android.graphics.BlendMode
import android.graphics.Matrix
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val shaderFont = androidx.compose.ui.text.font.FontFamily(
    Font(
        R.font.roboto_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(900),
            FontVariation.width(25f),
        )
    )
)

private val rainbowColors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)

@Composable
fun GradientFont() = Column {
    val brush = remember { Brush.linearGradient(colors = rainbowColors) }
    RenderText(brush, lines = 6)
}

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
        fontFamily = shaderFont,
        style = TextStyle(brush = brush),
    )
}

@Composable
fun GradientFontTileMode() = Column {
    val width = LocalDensity.current.run { 40.dp.toPx() }

    RenderText(remember { gradientBrush(width, TileMode.Clamp) }, 1)
    RenderText(remember { gradientBrush(width, TileMode.Mirror) }, 1)
    RenderText(remember { gradientBrush(width, TileMode.Repeated) }, 1)
    RenderText(remember { gradientBrush(width, TileMode.Decal) }, 1)
}

private fun gradientBrush(pxValue: Float, tileMode: TileMode) =
    Brush.linearGradient(
        start = Offset(0f, 0f),
        end = Offset(pxValue, 0f),
        colors = rainbowColors,
        tileMode = tileMode,
    )

@Composable
fun BitmapFont() = Column {
    val resources = LocalContext.current.resources

    val brush = remember {
        val bitmap = BitmapFactory
            .decodeResource(resources, R.drawable.cheetah_tile)
            .asImageBitmap()
        val shader = ImageShader(bitmap, TileMode.Repeated, TileMode.Repeated)

        val transform = Matrix()
        transform.postScale(0.4f, 0.4f)
        shader.setLocalMatrix(transform)

        ShaderBrush(shader)
    }
    RenderText(brush)
}

@Composable
fun ShadersComposition() = Column {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

    val resources = LocalContext.current.resources
    val pxValue = LocalDensity.current.run { 60.dp.toPx() }

    val brush = remember {

        val imageShader = ImageShader(
            BitmapFactory.decodeResource(resources, R.drawable.cheetah_tile)
                .asImageBitmap(), TileMode.Mirror, TileMode.Repeated
        )

        val blackColorShader = LinearGradientShader(
            from = Offset(0f, 0f),
            to = Offset(1f, 1f),
            colors = listOf(Color.Black, Color.Black),
            tileMode = TileMode.Clamp,
        )

        val grayImageShader = ComposeShader(
            imageShader,
            blackColorShader,
            BlendMode.SATURATION
        )

        val gradientShader = LinearGradientShader(
            from = Offset.Zero,
            to = Offset(0f, pxValue),
            colors = rainbowColors,
            tileMode = TileMode.Mirror,
        )

        val shader = ComposeShader(grayImageShader, gradientShader, PorterDuff.Mode.MULTIPLY)
        ShaderBrush(shader)
    }
    RenderText(brush)
}

private const val DURATION = 4000f
private const val SHADER_COLOR = """
    uniform float2 iResolution;
    half4 main(float2 fragCoord) {
      float2 scaled = fragCoord/iResolution.xy;
      return half4(scaled, 0, 1);
   }
"""

@Composable
fun ShaderFont() = Column {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val shader = remember { RuntimeShader(SHADER_COLOR) }
    val brush = remember { ShaderBrush(shader) }

    RenderText(
        brush,
        modifier = Modifier.onSizeChanged {
            shader.setFloatUniform(
                "iResolution",
                it.width.toFloat(),
                it.height.toFloat()
            )
        },
    )
}

private const val SHADER_ANIM_COLOR = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform float iDuration;
    
    half4 main(in float2 fragCoord) {
      float2 scaled = abs(1.0-mod(fragCoord/iResolution.xy+iTime/(iDuration/2.0),2.0));
      return half4(scaled, 0, 1.0);
    }
"""

@Composable
fun ShaderFontAnimated() = Column {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val shader = remember {
        RuntimeShader(SHADER_ANIM_COLOR)
            .apply { setFloatUniform("iDuration", DURATION) }
    }
    val brush = remember { ShaderBrush(shader) }

    val infiniteAnimation = remember {
        infiniteRepeatable<Float>(
            tween(DURATION.toInt(), easing = LinearEasing),
            RepeatMode.Restart
        )
    }
    val timePassed by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = DURATION,
        animationSpec = infiniteAnimation
    )
    shader.setFloatUniform("iTime", timePassed)

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
            .alpha(1 - (timePassed + 1) / 1000 / DURATION),
    )
}

//more shaders on https://shaders.skia.org/

private const val SHADER_WAVE = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform float iDuration;
    
    float f(vec3 p) {
        p.z -= iTime / 400.;
        float a = p.z * .1;
        p.xy *= mat2(cos(a), sin(a), -sin(a), cos(a));
        return .1 - length(cos(p.xy) + sin(p.yz));
    }

    half4 main(vec2 fragcoord) { 
        vec3 d = .5 - fragcoord.xy1 / iResolution.y;
        vec3 p=vec3(0);
        for (int i = 0; i < 32; i++) {
          p += f(p) * d;
        }
        return ((sin(p) + vec3(2, 5, 12)) / length(p)).xyz1;
    }
"""

private const val SHADER_SKY = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform float iDuration;
    
    const float cloudscale = 1.1;
    const float speed = 0.0003;
    const float clouddark = 0.5;
    const float cloudlight = 0.3;
    const float cloudcover = 0.2;
    const float cloudalpha = 8.0;
    const float skytint = 0.5;
    const vec3 skycolour1 = vec3(0.2, 0.4, 0.6);
    const vec3 skycolour2 = vec3(0.4, 0.7, 1.0);

    const mat2 m = mat2( 1.6,  1.2, -1.2,  1.6 );

    vec2 hash( vec2 p ) {
        p = vec2(dot(p,vec2(127.1,311.7)), dot(p,vec2(269.5,183.3)));
        return -1.0 + 2.0*fract(sin(p)*43758.5453123);
    }

    float noise( in vec2 p ) {
        const float K1 = 0.366025404; // (sqrt(3)-1)/2;
        const float K2 = 0.211324865; // (3-sqrt(3))/6;
        vec2 i = floor(p + (p.x+p.y)*K1);	
        vec2 a = p - i + (i.x+i.y)*K2;
        vec2 o = (a.x>a.y) ? vec2(1.0,0.0) : vec2(0.0,1.0); //vec2 of = 0.5 + 0.5*vec2(sign(a.x-a.y), sign(a.y-a.x));
        vec2 b = a - o + K2;
        vec2 c = a - 1.0 + 2.0*K2;
        vec3 h = max(0.5-vec3(dot(a,a), dot(b,b), dot(c,c) ), 0.0 );
        vec3 n = h*h*h*h*vec3( dot(a,hash(i+0.0)), dot(b,hash(i+o)), dot(c,hash(i+1.0)));
        return dot(n, vec3(70.0));	
    }

    float fbm(vec2 n) {
        float total = 0.0, amplitude = 0.1;
        for (int i = 0; i < 7; i++) {
            total += noise(n) * amplitude;
            n = m * n;
            amplitude *= 0.4;
        }
        return total;
    }

    // -----------------------------------------------

    half4 main(in vec2 fragCoord ) {
        vec2 p = fragCoord.xy / iResolution.xy;
        vec2 uv = p*vec2(iResolution.x/iResolution.y,1.0);    
        float time = iTime * speed;
        float q = fbm(uv * cloudscale * 0.5);
        
        //ridged noise shape
        float r = 0.0;
        uv *= cloudscale;
        uv -= q - time;
        float weight = 0.8;
        for (int i=0; i<8; i++){
            r += abs(weight*noise( uv ));
            uv = m*uv + time;
            weight *= 0.7;
        }
        
        //noise shape
        float f = 0.0;
        uv = p*vec2(iResolution.x/iResolution.y,1.0);
        uv *= cloudscale;
        uv -= q - time;
        weight = 0.7;
        for (int i=0; i<8; i++){
            f += weight*noise( uv );
            uv = m*uv + time;
            weight *= 0.6;
        }
        
        f *= r + f;
        
        //noise colour
        float c = 0.0;
        time = iTime * speed * 2.0;
        uv = p*vec2(iResolution.x/iResolution.y,1.0);
        uv *= cloudscale*2.0;
        uv -= q - time;
        weight = 0.4;
        for (int i=0; i<7; i++){
            c += weight*noise( uv );
            uv = m*uv + time;
            weight *= 0.6;
        }
        
        //noise ridge colour
        float c1 = 0.0;
        time = iTime * speed * 3.0;
        uv = p*vec2(iResolution.x/iResolution.y,1.0);
        uv *= cloudscale*3.0;
        uv -= q - time;
        weight = 0.4;
        for (int i=0; i<7; i++){
            c1 += abs(weight*noise( uv ));
            uv = m*uv + time;
            weight *= 0.6;
        }
        
        c += c1;
        
        vec3 skycolour = mix(skycolour2, skycolour1, p.y);
        vec3 cloudcolour = vec3(1.1, 1.1, 0.9) * clamp((clouddark + cloudlight*c), 0.0, 1.0);
       
        f = cloudcover + cloudalpha*f*r;
        
        vec3 result = mix(skycolour, clamp(skytint * skycolour + cloudcolour, 0.0, 1.0), clamp(f + c, 0.0, 1.0));
        
        return vec4( result, 1.0 );
    }
"""
