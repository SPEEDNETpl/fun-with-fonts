package com.mikolajkakol.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

/**
 * When clicking run test in IDE ensure that app variant is set to benchmark!!!
 */
@RunWith(Parameterized::class)
class ShaderPerformanceTest(
    @Suppress("unused") private val loop: Int,
    private val id: String
) {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    companion object {

        @Parameterized.Parameters(name = "anim{1} loop{0}")
        @JvmStatic
        fun initParameters() = buildList {
            repeat(100) {
                add(arrayOf(it, "1"))
                add(arrayOf(it, "2"))
                add(arrayOf(it, "3"))
            }
        }
    }

    @Test
    fun animation() = benchmarkRule.measureRepeated(
        packageName = "com.mikolajkakol.myapplication",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.Full(),
        iterations = 10,
        startupMode = StartupMode.HOT,
        setupBlock = {
            startActivityAndWait()
            device.findObject(By.text("Shader performance"))?.click()
            device.findObject(By.text("Shader performance $id"))?.click()
        }
    ) {
        Thread.sleep(100)
    }
}
