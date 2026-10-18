package tk.glucodata.chart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Where an SMB triangle hangs: the main line's height at the dose's time. */
class ChartLineValueNearTests {

    private companion object {
        const val MINUTE = 60_000L
        const val T0 = 1_800_000_000_000L
        const val TOLERANCE = 6 * MINUTE
    }

    private fun series(vararg runs: List<Pair<Long, Float>>) = ChartSeriesModel(
        sensorId = "A",
        isPrimary = true,
        viewMode = 0,
        colorArgb = 0,
        runs = runs.map { points ->
            ChartRun(ChartLook.MAIN, points.map { (ts, v) -> ChartPointModel(ts, v) })
        },
    )

    @Test
    fun aDoseOnAReadingTakesThatReading() {
        val line = series(listOf(T0 to 120f, T0 + 5 * MINUTE to 130f))
        assertEquals(130f, line.lineValueNear(T0 + 5 * MINUTE, TOLERANCE)!!, 0f)
    }

    @Test
    fun aDoseBetweenFiveMinuteReadingsSitsOnTheLineBetweenThem() {
        val line = series(listOf(T0 to 120f, T0 + 5 * MINUTE to 130f))
        assertEquals(124f, line.lineValueNear(T0 + 2 * MINUTE, TOLERANCE)!!, 0.001f)
    }

    @Test
    fun aDosePastTheLastReadingTakesItWhileClose() {
        val line = series(listOf(T0 to 120f, T0 + 5 * MINUTE to 130f))
        assertEquals(130f, line.lineValueNear(T0 + 9 * MINUTE, TOLERANCE)!!, 0f)
    }

    @Test
    fun aDoseInAGapTheLineDoesNotCrossHasNoValue() {
        val line = series(listOf(T0 to 120f), listOf(T0 + 60 * MINUTE to 180f))
        assertNull(line.lineValueNear(T0 + 30 * MINUTE, TOLERANCE))
    }

    @Test
    fun theNearerRunEndWinsAcrossABreak() {
        val line = series(listOf(T0 to 120f), listOf(T0 + 10 * MINUTE to 180f))
        assertEquals(180f, line.lineValueNear(T0 + 6 * MINUTE, TOLERANCE)!!, 0f)
    }
}
