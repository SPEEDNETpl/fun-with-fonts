import org.jetbrains.letsPlot.GGBunch
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.geom.geomJitter
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.letsPlot
import java.io.File

fun main() {
    val workingDir = getWorkingDir()
    val prefix = "test_results"
    val inputData = parseResults(File(workingDir, prefix))

    val bunch = GGBunch()
    bunch.addPlot(plotLine(inputData, 50), 0, 0)
    bunch.addPlot(plotLine(inputData, 90), 0, 400)
    bunch.addPlot(plotLine(inputData, 95), 0, 800)
    ggsave(bunch, "$prefix.png", path = workingDir.absolutePath)
    ggsave(plotBox(inputData), "${prefix}_plot_box.png", path = workingDir.absolutePath)
    ggsave(plotLine(inputData, 50), "${prefix}_plot_line_50.png", path = workingDir.absolutePath)
    ggsave(plotLine(inputData, 90), "${prefix}_plot_lines_90.png", path = workingDir.absolutePath)
    ggsave(plotLine(inputData, 95), "${prefix}_plot_lines_95.png", path = workingDir.absolutePath)
}

private fun plotBox(inputData: List<TestRun>): Plot {
    val data = mapOf<String, Any>(
        "supp" to inputData.map { it.name },
        "time" to inputData.map { it.results[90] },
        "min" to inputData.map { it.results[50] },
        "max" to inputData.map { it.results[95] }
    )
    return ggplot(data) { x = "supp"; y = "time"; fill = "supp" } +
            geomBoxplot() +
            geomJitter()
}

private fun plotLine(lineData: List<TestRun>, percentile: Int): Plot {
    val data = mapOf(
        "iteration" to lineData.map { it.loop },
        "time" to lineData.map { it.results[percentile] },
        "cond" to lineData.map { it.name + " " + percentile },
        "g" to lineData.map { it.name },
    )

    return letsPlot(data) +
            ggtitle("Average time for $percentile percentile") +
            geomLine {
                x = "iteration"
                y = "time"
                color = "cond"
            }
}

private fun getWorkingDir() =
    File((System.getProperty("user.dir") + "/macrobenchmark/src/main/resources"))
