package aoc2021

import utils.readInput
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
    val testInput = readInput("2021", "Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("2021", "Day05")
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

/*
--- Day 5: Hydrothermal Venture ---
You come across a field of hydrothermal vents on the ocean floor! These vents constantly produce large, opaque clouds, so it would be best to avoid them if possible.

They tend to form in lines; the submarine helpfully produces a list of nearby lines of vents (your puzzle input) for you to review. For example:

0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2
Each line of vents is given as a line segment in the format x1,y1 -> x2,y2 where x1,y1 are the coordinates of one end the line segment and x2,y2 are the coordinates of the other end. These line segments include the points at both ends. In other words:

An entry like 1,1 -> 1,3 covers points 1,1, 1,2, and 1,3.
An entry like 9,7 -> 7,7 covers points 9,7, 8,7, and 7,7.
For now, only consider horizontal and vertical lines: lines where either x1 = x2 or y1 = y2.

So, the horizontal and vertical lines from the above list would produce the following diagram:

.......1..
..1....1..
..1....1..
.......1..
.112111211
..........
..........
..........
..........
222111....
In this diagram, the top left corner is 0,0 and the bottom right corner is 9,9. Each position is shown as the number of lines which cover that point or . if no line covers that point. The top-left pair of 1s, for example, comes from 2,2 -> 2,1; the very bottom row is formed by the overlapping lines 0,9 -> 5,9 and 0,9 -> 2,9.

To avoid the most dangerous areas, you need to determine the number of points where at least two lines overlap. In the above example, this is anywhere in the diagram with a 2 or larger - a total of 5 points.

Consider only horizontal and vertical lines. At how many points do at least two lines overlap?

Your puzzle answer was 5294.

--- Part Two ---
Unfortunately, considering only horizontal and vertical lines doesn't give you the full picture; you need to also consider diagonal lines.

Because of the limits of the hydrothermal vent mapping system, the lines in your list will only ever be horizontal, vertical, or a diagonal line at exactly 45 degrees. In other words:

An entry like 1,1 -> 3,3 covers points 1,1, 2,2, and 3,3.
An entry like 9,7 -> 7,9 covers points 9,7, 8,8, and 7,9.
Considering all lines from the above example would now produce the following diagram:

1.1....11.
.111...2..
..2.1.111.
...1.2.2..
.112313211
...1.2....
..1...1...
.1.....1..
1.......1.
222111....
You still need to determine the number of points where at least two lines overlap. In the above example, this is still anywhere in the diagram with a 2 or larger - now a total of 12 points.

Consider all of the lines. At how many points do at least two lines overlap?

Your puzzle answer was 21698.

Both parts of this puzzle are complete! They provide two gold stars: **

At this point, you should return to your Advent calendar and try another puzzle.

If you still want to see it, you can get your puzzle input.

You can also [Share] this puzzle.
 */