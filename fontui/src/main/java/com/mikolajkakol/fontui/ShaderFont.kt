package com.mikolajkakol.fontui

import android.graphics.BitmapFactory
import android.graphics.BlendMode
import android.graphics.ComposeShader
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
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

@Composable
fun GradientFont() = Column {
    val pxValue = LocalDensity.current.run { 70.dp.toPx() }

    val shader = remember {
        val m = Matrix()
        m.postRotate(50f)
        LinearGradientShader(
            from = Offset(0f, 0f),
            to = Offset(0f, pxValue),
            colors = listOf(Color.Red, Color.Green, Color.Blue),
            tileMode = TileMode.Mirror,
        )
            .also { it.setLocalMatrix(m) }
    }
    RenderText(remember { ShaderBrush(shader) })
}

@Composable
fun BitmapFont() = Column(Modifier.background(Color.Black)) {
    val resources = LocalContext.current.resources

    val shader = remember {
        val m = Matrix()
        val scale = 0.4f
        m.postScale(scale, scale)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.pattern)
            .asImageBitmap()
        ImageShader(bitmap, TileMode.Mirror, TileMode.Repeated)
            .also { it.setLocalMatrix(m) }
    }
    RenderText(remember { ShaderBrush(shader) })
}

@Composable
fun ShadersComposition() = Column {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return

    val resources = LocalContext.current.resources
    val pxValue = LocalDensity.current.run { 70.dp.toPx() }

    val shader = remember {
        val m = Matrix()
        val scale = 1.0f
        m.postScale(scale, scale)

        val imageShader = ImageShader(
            BitmapFactory.decodeResource(resources, R.drawable.cheetah_tile)
                .asImageBitmap(), TileMode.Mirror, TileMode.Repeated
        ).also { it.setLocalMatrix(m) }

        val grayImageShader = ComposeShader(
            imageShader,
            LinearGradientShader(
                from = Offset(0f, 0f),
                to = Offset(1f, 1f),
                colors = listOf(Color.Black, Color.Black),
                tileMode = TileMode.Clamp,
            ),
            BlendMode.SATURATION
        )

        val gradientShader = LinearGradientShader(
            from = Offset(0f, 0f),
            to = Offset(0f, pxValue),
            colors = listOf(Color.Red, Color.Green, Color.Blue),
            tileMode = TileMode.Mirror,
        )

        ComposeShader(grayImageShader, gradientShader, PorterDuff.Mode.MULTIPLY)
    }
    RenderText(remember { ShaderBrush(shader) })
}

@Composable
private fun RenderText(brush: ShaderBrush) {
    val text = remember {
        val msg = (demoText + "\n").repeat(4).trim()
        buildAnnotatedString {
            withStyle(ParagraphStyle(lineHeight = 12.sp)) {
                withStyle(SpanStyle(brush = brush)) {
                    append(msg)
                }
            }
        }
    }
    Text(
        text = text,
        fontFamily = shaderFont,
    )
}

private const val DURATION = 4000f
private const val SHADER_COLOR = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform float iDuration;
    
    half4 main(in float2 fragCoord) {
      float2 scaled = abs(1.0-mod(fragCoord/iResolution.xy+iTime/(iDuration/2.0),2.0));
      return half4(scaled, 0, 1.0);
    }
"""

@Composable
fun ShaderFont() = Column {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val shader = remember {
        RuntimeShader(SHADER_COLOR)
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

    val text = remember {
        val msg = (demoText + "\n").repeat(4).trim()
        buildAnnotatedString {
            withStyle(SpanStyle(brush = brush)) {
                append(msg)
            }
        }
    }
    Text(
        modifier = Modifier.alpha(1 - (timePassed + 1) / 1000 / DURATION),
        text = text,
        fontFamily = shaderFont,
        onTextLayout = {
            shader.setFloatUniform(
                "iResolution",
                it.size.width.toFloat(),
                it.size.height.toFloat()
            )
        }
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
