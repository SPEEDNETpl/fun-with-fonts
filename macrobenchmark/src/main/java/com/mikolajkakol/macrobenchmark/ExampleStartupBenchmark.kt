package com.mikolajkakol.macrobenchmark

import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun animation() = benchmarkRule.measureRepeated(
        packageName = "com.mikolajkakol.myapplication",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.DEFAULT,
        iterations = 2,
        startupMode = StartupMode.HOT,
        setupBlock = {
            startActivityAndWait()
            device.findObject(By.text("shaderAnim1"))?.click()
        }
    ) {
        Thread.sleep(2000)
    }

    @Test
    fun inputText() {
        benchmarkRule.measureRepeated(
            packageName = "com.mikolajkakol.myapplication",
            metrics = listOf(FrameTimingMetric()),
            // Try switching to different compilation modes to see the effect
            // it has on frame timing metrics.
            compilationMode = CompilationMode.None(),
            startupMode = StartupMode.WARM, // restarts activity each iteration
            iterations = 2,
            setupBlock = {
                startActivityAndWait()
            }
        ) {
            repeat(40) {
                device.pressEnter()
            }
        }
    }

}