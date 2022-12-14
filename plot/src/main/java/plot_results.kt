import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.letsPlot
import java.io.File

fun main() {
    val workingDir = getWorkingDir()
    val rand = java.util.Random(123)
    val n = 400
    val data = mapOf(
        "rating" to List(n / 2) { rand.nextGaussian() } + List(n / 2) { rand.nextGaussian() * 1.5 + 1.5 },
        "cond" to List(n / 2) { "A" } + List(n / 2) { "B" }
    )

    val p = letsPlot(data) +
            geomDensity { x = "rating"; color = "cond" } +
            ggsize(500, 900)

    ggsave(p, "test_results_plot.png", path = workingDir.absolutePath)
}

private fun getWorkingDir() =
    File((System.getProperty("user.dir") + "/macrobenchmark/src/main/resources"))
