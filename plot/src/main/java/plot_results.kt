import org.jetbrains.letsPlot.GGBunch
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomBoxplot
import org.jetbrains.letsPlot.geom.geomJitter
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomViolin
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.intern.Plot
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleColorManual
import org.jetbrains.letsPlot.scale.scaleFillManual
import java.io.File

fun main() {
    val workingDir = getWorkingDir()
    val prefix = "test_results2"
    val inputData = parseResults(File(workingDir, prefix))

    saveMultiple("${prefix}_plot_line", workingDir) { plotLine(inputData, it) }
    saveMultiple("${prefix}_plot_violin", workingDir) { plotViolin(inputData, it) }
    saveMultiple("${prefix}_plot_box", workingDir) { plotBox(inputData, it) }
}

fun saveMultiple(name: String, workingDir: File, plotFactory: (percentile: Int) -> Plot) {
    val factory = decorateFactory(plotFactory)
    val bunch = GGBunch()
    bunch.addPlot(factory(50), 0, 0)
    bunch.addPlot(factory(90), 0, 400)
    bunch.addPlot(factory(95), 0, 800)
    ggsave(bunch, "${name}_mix.png", path = workingDir.absolutePath)
    ggsave(factory(50), "${name}_50.png", path = workingDir.absolutePath)
    ggsave(factory(90), "${name}_90.png", path = workingDir.absolutePath)
    ggsave(factory(95), "${name}_95.png", path = workingDir.absolutePath)
}

private fun decorateFactory(original: (percentile: Int) -> Plot): (Int) -> Plot = {
    val colors = listOf("#F33A00", "#14BEBE", "#AAE95A")
    original(it) +
            scaleFillManual(colors) +
            scaleColorManual(colors)
}

private fun plotBox(inputData: List<TestRun>, percentile: Int): Plot {
    val data = mapOf<String, Any>(
        "anim" to inputData.map { it.name },
        "time" to inputData.map { it.results[percentile] },
    )
    return ggplot(data) { x = "anim"; y = "time"; fill = "anim" } +
            title(percentile) +
            geomBoxplot(showLegend = false) +
            geomJitter(showLegend = false, width = 0.35)
}

private fun plotViolin(inputData: List<TestRun>, percentile: Int): Plot {
    val data = mapOf<String, Any>(
        "anim" to inputData.map { it.name },
        "time" to inputData.map { it.results[percentile] },
    )
    return ggplot(data) { x = "anim"; y = "time"; fill = "anim" } +
            title(percentile) +
            geomViolin(showLegend = false) +
            geomJitter(showLegend = false, width = 0.35)
}

private fun plotLine(lineData: List<TestRun>, percentile: Int): Plot {
    val data = mapOf(
        "iteration" to lineData.map { it.loop },
        "time" to lineData.map { it.results[percentile] },
        "anim" to lineData.map { it.name },
    )

    return letsPlot(data) +
            title(percentile) +
            geomLine {
                x = "iteration"
                y = "time"
                color = "anim"
            }
}

private fun title(percentile: Int) = ggtitle("Average time for $percentile percentile")

private fun getWorkingDir() =
    File((System.getProperty("user.dir") + "/macrobenchmark/src/main/resources"))
