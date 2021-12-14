import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int {
        val remainingPoints = calculateRemainingTransparentPaperPoints(input, 1)
        return remainingPoints.count()
    }

    fun part2(input: List<String>) {
        val remainingPoints = calculateRemainingTransparentPaperPoints(input)
        val transparentPaper = TransparentPaper(remainingPoints)
        transparentPaper.drawActivateTheInfraredThermalImagingCameraSystemCode()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)
    part2(testInput)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

private fun calculateRemainingTransparentPaperPoints(input: List<String>, foldTimes: Int? = null): List<TransparentPaperPoint> {
    val transparentPaperPoints = input.filter {
        it.isNotBlank() && !it.contains("fold")
    }.map {
        val pointInfo = it.split(",")
        TransparentPaperPoint(pointInfo.first().toInt(), pointInfo.last().toInt())
    }
    val foldLines = input.filter {
        it.contains("fold")
    }.mapNotNull {
        val foldLineInfo = it.removePrefix("fold along ").split("=")
        when (foldLineInfo.first()) {
            "y" -> HorizontalFoldLine(foldLineInfo.last().toInt())
            "x" -> VerticalFoldLine(foldLineInfo.last().toInt())
            else -> null
        }
    }

    var remainingCavePoints = transparentPaperPoints
    var totalFoldTimes = foldTimes ?: foldLines.count()
    foldLines.take(totalFoldTimes).forEach { foldLine ->
        remainingCavePoints = foldLine.fold(remainingCavePoints)
    }

    println(transparentPaperPoints)
    println(foldLines)
    println(remainingCavePoints)

    return remainingCavePoints
}

data class TransparentPaperPoint(val x: Int, val y: Int)

interface FoldLine {
    fun fold(points: List<TransparentPaperPoint>): List<TransparentPaperPoint>
}

data class VerticalFoldLine(val x: Int): FoldLine {
    override fun fold(points: List<TransparentPaperPoint>): List<TransparentPaperPoint> {
        return points.mapNotNull {
            if (it.x < x) {
                it
            } else if (it.x == x) {
                null
            } else {
                val distanceFromLine = it.x - x
                if (x - distanceFromLine >= 0) {
                    TransparentPaperPoint(x - distanceFromLine, it.y)
                } else {
                    null
                }
            }
        }.distinct()
    }
}

data class HorizontalFoldLine(val y: Int): FoldLine {
    override fun fold(points: List<TransparentPaperPoint>): List<TransparentPaperPoint> {
        return points.mapNotNull {
            if (it.y < y) {
                it
            } else if (it.y == y) {
                null
            } else {
                val distanceFromLine = it.y - y
                if (y - distanceFromLine >= 0) {
                    TransparentPaperPoint(it.x, y - distanceFromLine)
                } else {
                    null
                }
            }
        }.distinct()
    }
}

class TransparentPaper(val points: List<TransparentPaperPoint>) {
    fun drawActivateTheInfraredThermalImagingCameraSystemCode() {
        var maxX = 0
        var maxY = 0
        points.forEach {
            maxX = max(maxX, it.x)
            maxY = max(maxY, it.y)
        }

        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                val value = if (points.contains(TransparentPaperPoint(x, y))) {
                    "#"
                } else {
                    "."
                }
                print(value)
            }
            println()
        }
    }
}

/*
--- Day 13: Transparent Origami ---
You reach another volcanically active part of the cave. It would be nice if you could do some kind of thermal imaging so you could tell ahead of time which caves are too hot to safely enter.

Fortunately, the submarine seems to be equipped with a thermal camera! When you activate it, you are greeted with:

Congratulations on your purchase! To activate this infrared thermal imaging
camera system, please enter the code found on page 1 of the manual.
Apparently, the Elves have never used this feature. To your surprise, you manage to find the manual; as you go to open it, page 1 falls out. It's a large sheet of transparent paper! The transparent paper is marked with random dots and includes instructions on how to fold it up (your puzzle input). For example:

6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5
The first section is a list of dots on the transparent paper. 0,0 represents the top-left coordinate. The first value, x, increases to the right. The second value, y, increases downward. So, the coordinate 3,0 is to the right of 0,0, and the coordinate 0,7 is below 0,0. The coordinates in this example form the following pattern, where # is a dot on the paper and . is an empty, unmarked position:

...#..#..#.
....#......
...........
#..........
...#....#.#
...........
...........
...........
...........
...........
.#....#.##.
....#......
......#...#
#..........
#.#........
Then, there is a list of fold instructions. Each instruction indicates a line on the transparent paper and wants you to fold the paper up (for horizontal y=... lines) or left (for vertical x=... lines). In this example, the first fold instruction is fold along y=7, which designates the line formed by all of the positions where y is 7 (marked here with -):

...#..#..#.
....#......
...........
#..........
...#....#.#
...........
...........
-----------
...........
...........
.#....#.##.
....#......
......#...#
#..........
#.#........
Because this is a horizontal line, fold the bottom half up. Some of the dots might end up overlapping after the fold is complete, but dots will never appear exactly on a fold line. The result of doing this fold looks like this:

#.##..#..#.
#...#......
......#...#
#...#......
.#.#..#.###
...........
...........
Now, only 17 dots are visible.

Notice, for example, the two dots in the bottom left corner before the transparent paper is folded; after the fold is complete, those dots appear in the top left corner (at 0,0 and 0,1). Because the paper is transparent, the dot just below them in the result (at 0,3) remains visible, as it can be seen through the transparent paper.

Also notice that some dots can end up overlapping; in this case, the dots merge together and become a single dot.

The second fold instruction is fold along x=5, which indicates this line:

#.##.|#..#.
#...#|.....
.....|#...#
#...#|.....
.#.#.|#.###
.....|.....
.....|.....
Because this is a vertical line, fold left:

#####
#...#
#...#
#...#
#####
.....
.....
The instructions made a square!

The transparent paper is pretty big, so for now, focus on just completing the first fold. After the first fold in the example above, 17 dots are visible - dots that end up overlapping after the fold is completed count as a single dot.

How many dots are visible after completing just the first fold instruction on your transparent paper?

Your puzzle answer was 729.

--- Part Two ---
Finish folding the transparent paper according to the instructions. The manual says the code is always eight capital letters.

What code do you use to activate the infrared thermal imaging camera system?

Your puzzle answer was RGZLBHFP.

Both parts of this puzzle are complete! They provide two gold stars: **

At this point, you should return to your Advent calendar and try another puzzle.

If you still want to see it, you can get your puzzle input.

You can also [Share] this puzzle.
 */