import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Int {
        val ventLines = getVentLines(input)
        val oceanFloorCoordinateSystem = OceanFloorCoordinateSystem(OceanFloorCoordinateSystem.CalculateMode.H_AND_V)
        oceanFloorCoordinateSystem.addVentLines(ventLines)

        val overlapPoints = oceanFloorCoordinateSystem.calculateOverLapPoints()
        println("overlapPoints: $overlapPoints")
        return overlapPoints.count()
    }

    fun part2(input: List<String>): Int {
        val ventLines = getVentLines(input)
        val oceanFloorCoordinateSystem = OceanFloorCoordinateSystem(OceanFloorCoordinateSystem.CalculateMode.H_V_AND_D)
        oceanFloorCoordinateSystem.addVentLines(ventLines)

        val overlapPoints = oceanFloorCoordinateSystem.calculateOverLapPoints()
        println("overlapPoints: $overlapPoints")
        return overlapPoints.count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

fun getVentLines(input: List<String>): MutableList<VentLine> {
    val ventLines = mutableListOf<VentLine>()
    input.forEach {
        val pointsString = it.split(" -> ")
        val startPoint = getPoint(pointsString.first())
        val endPoint = getPoint(pointsString.last())
        ventLines.add(VentLine(startPoint, endPoint))
    }
    return ventLines
}

fun getPoint(pointString: String): Point {
    val pointList = pointString.split(",")
    val x = pointList.first().toInt()
    val y = pointList.last().toInt()
    return Point(x, y)
}

data class Point(val x: Int, val y: Int)
data class VentLine(val startPoint: Point, val endPoint: Point)
class OceanFloorCoordinateSystem(val calculateMode: CalculateMode) {
    enum class CalculateMode {
        H_AND_V,    // Horizontal, Vertical
        H_V_AND_D;  // Horizontal, Vertical and Diagonal
    }
    val ventPointsCounter = mutableMapOf<Point, Int>()

    fun getLineSegments(startPoint: Point, endPoint: Point): List<Point> {
        return when {
            startPoint.x == endPoint.x -> { // Vertical
                val x = startPoint.x
                val maxY = max(startPoint.y, endPoint.y)
                val minY = min(startPoint.y, endPoint.y)
                (minY..maxY).map { Point(x, it) }
            }
            startPoint.y == endPoint.y -> { // Horizontal
                val y = startPoint.y
                val maxX = max(startPoint.x, endPoint.x)
                val minX = min(startPoint.x, endPoint.x)
                (minX..maxX).map { Point(it, y) }
            }
            else -> {
                if (calculateMode == CalculateMode.H_V_AND_D) {
                    // Diagonal
                    var xDistance = abs(startPoint.x - endPoint.x)
                    var yDistance =  abs(startPoint.y - endPoint.y)
                    if (xDistance == yDistance) {
                        val xIncreaseUnit = when {
                            startPoint.x < endPoint.x -> 1
                            else -> -1
                        }
                        val yIncreaseUnit = when {
                            startPoint.y < endPoint.y -> 1
                            else -> -1
                        }

                        var x = startPoint.x
                        var y = startPoint.y
                        val points = mutableListOf<Point>()
                        while (xDistance + 1 > 0 && yDistance + 1 > 0) {
                            points.add(Point(x, y))
                            x += xIncreaseUnit
                            y += yIncreaseUnit
                            xDistance -= 1
                            yDistance -= 1
                        }
                        points
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }

        }

    }

    fun addVent(ventPoint: Point) {
        val countInVentPoint = ventPointsCounter[ventPoint] ?: 0
        ventPointsCounter[ventPoint] = countInVentPoint + 1
    }

    fun addVentLine(ventLine: VentLine) {
        val vents = getLineSegments(ventLine.startPoint, ventLine.endPoint)
        vents.forEach { addVent(it) }
    }

    fun addVentLines(ventLines: List<VentLine>) {
        ventLines.forEach { addVentLine(it) }
    }

    fun calculateOverLapPoints(): List<Point> {
        val overlapPoints = ventPointsCounter.entries.filter { it.value >= 2 }.map { it.key }
        return overlapPoints
    }
}
