package aoc2021

import utils.readInput

fun main() {
    fun part1(input: List<String>): Int {
        val point2DList: List<List<CavePoint>> = input.mapIndexed { row, pointList ->
            pointList.mapIndexed { column, value ->
                CavePoint(row, column, value.toString().toInt())
            }
        }

        val lowPoints = getLowerCavePoints(point2DList)

        return lowPoints.sumOf { it.height + 1 }
    }

    fun part2(input: List<String>): Int {
        val point2DList: List<List<CavePoint>> = input.mapIndexed { row, pointList ->
            pointList.mapIndexed { column, value ->
                CavePoint(row, column, value.toString().toInt())
            }
        }

        val lowPoints = getLowerCavePoints(point2DList)
        val basins = lowPoints.map { Basin(it) }
        basins.forEach {
            it.expandBasin(point2DList)
        }

        val threeLargestBasins =  basins.sortedByDescending { it.cavePoints.count() }.take(3)
        val result = threeLargestBasins.map { it.cavePoints.count() }.reduce { acc, basinCount ->
            acc * basinCount
        }

        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2021", "Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("2021", "Day09")
    println(part1(input))
    println(part2(input))
}

fun getLowerCavePoints(point2DList: List<List<CavePoint>>): List<CavePoint> {
    val lowPoints = mutableListOf<CavePoint>()
    point2DList.forEachIndexed { row, pointList ->
        pointList.forEachIndexed { column, point ->
            val upPointHeight = point2DList.getOrNull(row - 1)?.getOrNull(column)?.height ?: 9
            val downPointHeight = point2DList.getOrNull(row + 1)?.getOrNull(column)?.height ?: 9
            val leftPointHeight = pointList.getOrNull(column - 1)?.height ?: 9
            val rightPointHeight = pointList.getOrNull(column + 1)?.height ?: 9

            if (point.height < upPointHeight &&
                point.height < downPointHeight &&
                point.height < leftPointHeight &&
                point.height < rightPointHeight) {
                lowPoints.add(point)
            }
        }
    }

    println("lowPoints : $lowPoints")

    return lowPoints
}

data class CavePoint(val row: Int, val column: Int, val height: Int, var isMarkedToBasin: Boolean = false)
class Basin(val lowerPoint: CavePoint) {
    var cavePoints = mutableSetOf<CavePoint>(lowerPoint)

    fun expandBasin(cavePointsMap: List<List<CavePoint>>): Set<CavePoint> {

        while (true) {
            val currentCavePoint = cavePoints.toSet()

            for (point in currentCavePoint) {
                val upPoint = cavePointsMap.getOrNull(point.row - 1)?.getOrNull(point.column)
                val downPoint = cavePointsMap.getOrNull(point.row + 1)?.getOrNull(point.column)
                val leftPoint = cavePointsMap.getOrNull(point.row)?.getOrNull(point.column - 1)
                val rightPoint  = cavePointsMap.getOrNull(point.row)?.getOrNull(point.column + 1)
                val expandPoints = listOfNotNull(upPoint, downPoint, leftPoint, rightPoint).filter { it.height != 9 }
                cavePoints.addAll(expandPoints)
            }

            if (currentCavePoint == cavePoints) break
        }

        return cavePoints
    }
}

/*
--- Day 9: Smoke aoc2021.Basin ---
These caves seem to be lava tubes. Parts are even still volcanically active; small hydrothermal vents release smoke into the caves that slowly settles like rain.

If you can model how the smoke flows through the caves, you might be able to avoid it and be that much safer. The submarine generates a heightmap of the floor of the nearby caves for you (your puzzle input).

Smoke flows to the lowest point of the area it's in. For example, consider the following heightmap:

2199943210
3987894921
9856789892
8767896789
9899965678
Each number corresponds to the height of a particular location, where 9 is the highest and 0 is the lowest a location can be.

Your first goal is to find the low points - the locations that are lower than any of its adjacent locations. Most locations have four adjacent locations (up, down, left, and right); locations on the edge or corner of the map have three or two adjacent locations, respectively. (Diagonal locations do not count as adjacent.)

In the above example, there are four low points, all highlighted: two are in the first row (a 1 and a 0), one is in the third row (a 5), and one is in the bottom row (also a 5). All other locations on the heightmap have some lower adjacent location, and so are not low points.

The risk level of a low point is 1 plus its height. In the above example, the risk levels of the low points are 2, 1, 6, and 6. The sum of the risk levels of all low points in the heightmap is therefore 15.

Find all of the low points on your heightmap. What is the sum of the risk levels of all low points on your heightmap?

Your puzzle answer was 486.

--- Part Two ---
Next, you need to find the largest basins so you know what areas are most important to avoid.

A basin is all locations that eventually flow downward to a single low point. Therefore, every low point has a basin, although some basins are very small. Locations of height 9 do not count as being in any basin, and all other locations will always be part of exactly one basin.

The size of a basin is the number of locations within the basin, including the low point. The example above has four basins.

The top-left basin, size 3:

2199943210
3987894921
9856789892
8767896789
9899965678
The top-right basin, size 9:

2199943210
3987894921
9856789892
8767896789
9899965678
The middle basin, size 14:

2199943210
3987894921
9856789892
8767896789
9899965678
The bottom-right basin, size 9:

2199943210
3987894921
9856789892
8767896789
9899965678
Find the three largest basins and multiply their sizes together. In the above example, this is 9 * 14 * 9 = 1134.

What do you get if you multiply together the sizes of the three largest basins?

Your puzzle answer was 1059300.

Both parts of this puzzle are complete! They provide two gold stars: **

At this point, you should return to your Advent calendar and try another puzzle.

If you still want to see it, you can get your puzzle input.

You can also [Share] this puzzle.
 */