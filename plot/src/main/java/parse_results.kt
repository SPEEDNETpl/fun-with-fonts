import java.io.File

fun parseResults(file: File): List<TestRun> {
    return file.readLines()
        .windowed(2, 4)
        .map { (lineName, lineResults) ->
            val (name, loop) = testInfo(lineName)

            val t = lineResults.split("P")
                .drop(1)
                .associate {
                    val data = it.split(" ").filterNot { it.isEmpty() }
                    data[0].toInt() to data[1].removeSuffix(",").replace(",",".").toFloat()
                }

            TestRun(name, loop, t)
        }
}

private fun testInfo(testName: String) =
    """\[(.*?)]""".toRegex()
        .find(testName)!!
        .groupValues.last()
        .split(" ")
        .let { (name, loop) -> name to loop.removePrefix("loop").toInt() }