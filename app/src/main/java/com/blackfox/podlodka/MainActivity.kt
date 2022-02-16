package com.blackfox.podlodka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TargetBasedAnimation
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import com.blackfox.podlodka.ui.theme.Grape
import com.blackfox.podlodka.ui.theme.PodlodkaAnimationTheme
import com.blackfox.podlodka.ui.theme.Primary
import kotlin.math.max

@ExperimentalMotionApi
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PodlodkaAnimationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MotionLayoutCompose()
                }
            }
        }
    }
}


@ExperimentalMotionApi
@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PodlodkaAnimationTheme {
        MotionLayoutCompose()
    }
}

private fun startConstraintSet() = ConstraintSet {
    val topContent = createRefFor("topContent")
    val bottomContent = createRefFor("bottomContent")

    constrain(topContent) {
        width = Dimension.fillToConstraints
        height = Dimension.value(40.dp)
        start.linkTo(parent.start)
        top.linkTo(parent.top)
        end.linkTo(parent.end)
        bottom.linkTo(bottomContent.top)
    }

    constrain(bottomContent) {
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
        start.linkTo(parent.start)
        top.linkTo(topContent.bottom)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
    }
}

private fun endConstraintSet() = ConstraintSet {
    val topContent = createRefFor("topContent")
    val bottomContent = createRefFor("bottomContent")

    constrain(topContent) {
        width = Dimension.fillToConstraints
        height = Dimension.percent(0.6f)
        start.linkTo(parent.start)
        top.linkTo(parent.top)
        end.linkTo(parent.end)
        bottom.linkTo(bottomContent.top)
    }

    constrain(bottomContent) {
        width = Dimension.fillToConstraints
        height = Dimension.fillToConstraints
        start.linkTo(parent.start)
        top.linkTo(topContent.bottom)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
    }
}

enum class BoxStates {
    Collapsed,
    Expanded,
}

@Composable
fun BottomFloating(motionProgress: Float) {
    val transitionProgress = max(motionProgress - 0.5f, 0f) * 2f

    val transition = rememberInfiniteTransition()
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(transitionProgress)
            .padding(start = 80.dp, end = 80.dp)
            .fillMaxWidth()
            .scale(transitionProgress, transitionProgress)
            .offset(x = (-15).dp * offset, y = (-15).dp * offset),
        painter = painterResource(id = R.drawable.im_payment),
        contentDescription = "",
        contentScale = ContentScale.FillWidth
    )

}

@Composable
fun TopGlasses(modifier: Modifier = Modifier, motionProgress: Float) {

    var rotation by remember { mutableStateOf(0F) }
    var startAnimation by remember { mutableStateOf(true) }

    val anim = remember {
        TargetBasedAnimation(
            animationSpec = tween(1500, delayMillis = 200),
            typeConverter = Float.VectorConverter,
            initialValue = 0f,
            targetValue = 360f
        )
    }
    startAnimation = motionProgress == 1f
    LaunchedEffect(startAnimation) {
        var playTime: Long
        var startTime = withFrameNanos { it }
        do {
            if (motionProgress != 1f) {
                startTime = withFrameNanos { it }
            }
            playTime = withFrameNanos { it } - startTime
            rotation = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))
    }

    Image(
        modifier = modifier
            .scale(motionProgress)
            .alpha(motionProgress)
            .rotate(rotation),
        painter = painterResource(id = R.drawable.im_glasses),
        contentDescription = "",
        contentScale = ContentScale.FillWidth
    )
}

@Composable
fun TopTaxi(modifier: Modifier = Modifier, motionProgress: Float) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    var xOffset by remember { mutableStateOf(0F) }
    var startAnimation by remember { mutableStateOf(true) }

    val anim = remember {
        TargetBasedAnimation(
            animationSpec = tween(1500, easing = LinearEasing, delayMillis = 200),
            typeConverter = Float.VectorConverter,
            initialValue = 0f,
            targetValue = screenWidth.toFloat()
        )
    }
    startAnimation = motionProgress == 1f
    LaunchedEffect(startAnimation) {
        var playTime: Long
        var startTime = withFrameNanos { it }
        do {
            if (motionProgress != 1f) {
                startTime = withFrameNanos { it }
            }
            playTime = withFrameNanos { it } - startTime
            xOffset = anim.getValueFromNanos(playTime)
        } while (!anim.isFinishedFromNanos(playTime))
    }

    Image(
        modifier = modifier
            .padding(start = 16.dp, bottom = 40.dp)
            .graphicsLayer(
                scaleX = motionProgress,
                scaleY = motionProgress,
                transformOrigin = TransformOrigin(0f, 0.5f)
            )
            .alpha(motionProgress)
            .offset(x = xOffset.dp),
        painter = painterResource(id = R.drawable.im_taxi),
        contentDescription = "",
        contentScale = ContentScale.FillWidth
    )

}

@Composable
fun BottomGo(motionProgress: Float) {
    Column(
        modifier = Modifier
            .alpha(
                1f - motionProgress
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Поехали?!",
            modifier = Modifier
                .wrapContentHeight(),
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center
        )
        Text(
            text = "☝️",
            modifier = Modifier
                .padding(top = 16.dp)
                .rotate(-90f * motionProgress),
            style = MaterialTheme.typography.h3,
        )
    }
}

@ExperimentalMaterialApi
@ExperimentalMotionApi
@Composable
fun MotionLayoutCompose() {
    val screenHeight = LocalConfiguration.current.screenHeightDp.toFloat()

    val swipingState = rememberSwipeableState(initialValue = BoxStates.Collapsed)

    val animateMotionLayoutProgress by animateFloatAsState(
        targetValue = if (swipingState.progress.to == BoxStates.Collapsed) {
            1f - swipingState.progress.fraction
        } else {
            swipingState.progress.fraction
        },
        animationSpec = spring(Spring.DampingRatioNoBouncy)
    )

    MotionLayout(
        start = startConstraintSet(),
        end = endConstraintSet(),
        progress = animateMotionLayoutProgress,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight.dp)
            .swipeable(
                state = swipingState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    0f to BoxStates.Collapsed,
                    screenHeight to BoxStates.Expanded,
                ),
                reverseDirection = false
            )
    ) {
        Box(
            modifier = Modifier
                .layoutId("topContent")
                .background(Primary)
        ) {
            TopTaxi(
                modifier = Modifier
                    .align(Alignment.BottomStart),
                motionProgress = animateMotionLayoutProgress
            )
            TopGlasses(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 40.dp),
                motionProgress = animateMotionLayoutProgress
            )
        }
        Box(
            modifier = Modifier
                .layoutId("bottomContent")
                .background(Grape),
            contentAlignment = Alignment.Center
        ) {
            BottomFloating(motionProgress = animateMotionLayoutProgress)
            BottomGo(motionProgress = animateMotionLayoutProgress)
        }
    }
}