package com.sedilant.cachosfridge.ui.payment

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sedilant.cachosfridge.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PaymentSuccessAnimation(
    cardPayerName: String?,
    onFinished: () -> Unit
) {
    val bgAlpha = remember { Animatable(0f) }
    val circleScale = remember { Animatable(0f) }
    val checkProgress = remember { Animatable(0f) }
    val particlesProgress = remember { Animatable(0f) }
    val particlesAlpha = remember { Animatable(1f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        launch { bgAlpha.animateTo(0.85f, tween(300)) }

        launch {
            circleScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.5f,
                    stiffness = 200f
                )
            )
        }

        delay(200)

        launch {
            checkProgress.animateTo(1f, tween(400, easing = LinearOutSlowInEasing))
        }
        launch {
            textAlpha.animateTo(1f, tween(400))
        }
        launch {
            textOffset.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
        }

        launch {
            particlesProgress.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        }
        launch {
            delay(300)
            particlesAlpha.animateTo(0f, tween(300))
        }

        delay(1200)

        launch { bgAlpha.animateTo(0f, tween(200)) }
        launch { circleScale.animateTo(0f, tween(200)) }
        launch { textAlpha.animateTo(0f, tween(200)) }

        delay(200)
        onFinished()
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val checkColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = bgAlpha.value)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2 - 70.dp.toPx())
            val baseRadius = 64.dp.toPx()

            if (particlesProgress.value > 0f && particlesAlpha.value > 0f) {
                val numParticles = 8
                val maxRadius = baseRadius * 2.8f
                val currentRadius = baseRadius + (maxRadius - baseRadius) * particlesProgress.value
                val particleSize = 8.dp.toPx()

                for (i in 0 until numParticles) {
                    val angle = (i * (360f / numParticles)) * (Math.PI / 180f)
                    val x = center.x + cos(angle).toFloat() * currentRadius
                    val y = center.y + sin(angle).toFloat() * currentRadius

                    val color = if (i % 2 == 0) primaryColor else primaryColor.copy(alpha = 0.6f)

                    drawCircle(
                        color = color.copy(alpha = particlesAlpha.value),
                        radius = particleSize * (1f - (particlesProgress.value * 0.5f)),
                        center = Offset(x, y)
                    )
                }
            }

            if (circleScale.value > 0f) {
                drawCircle(
                    color = primaryColor,
                    radius = baseRadius * circleScale.value,
                    center = center
                )
            }

            if (checkProgress.value > 0f) {
                val path = Path().apply {
                    moveTo(center.x - 20.dp.toPx(), center.y + 5.dp.toPx())
                    lineTo(center.x - 5.dp.toPx(), center.y + 20.dp.toPx())
                    lineTo(center.x + 25.dp.toPx(), center.y - 15.dp.toPx())
                }

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(path, false)
                val length = pathMeasure.length

                val dstPath = Path()
                pathMeasure.getSegment(0f, length * checkProgress.value, dstPath, true)

                drawPath(
                    path = dstPath,
                    color = checkColor,
                    style = Stroke(
                        width = 10.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = textAlpha.value
                    translationY = 80.dp.toPx() + textOffset.value
                }
        ) {
            Text(
                text = stringResource(id = R.string.pago_exitoso),
                color = primaryColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            if (cardPayerName != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.pago_cobrado_a, cardPayerName),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
