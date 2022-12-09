package aoc2022

import utils.readInput

enum class TreeLookDirectory {
    Up, Down, Left, Right;
}

fun main() {
    fun generatingTreeGrid(input: List<String>): List<List<Int>> {
        return input.map { it.map { char -> char.digitToInt() } }
    }

    fun isTreeVisibleFromDirectory(directory: TreeLookDirectory, treeGrid: List<List<Int>>, row: Int, column: Int, treeHeight: Int): Boolean {
        if (row == 0 || row == treeGrid.size - 1 || column == 0 || column == treeGrid.first().size - 1) {
            return true
        }

        val (nextTreeRow, nextTreeColumn) = when (directory) {
            TreeLookDirectory.Up -> Pair(row - 1, column)
            TreeLookDirectory.Down -> Pair(row + 1, column)
            TreeLookDirectory.Left -> Pair(row, column - 1)
            TreeLookDirectory.Right -> Pair(row, column + 1)
        }

        val nextTreeHeight = treeGrid[nextTreeRow][nextTreeColumn]

        return (nextTreeHeight < treeHeight) && isTreeVisibleFromDirectory(directory, treeGrid, nextTreeRow, nextTreeColumn, treeHeight)
    }

    fun calculatingVisibleTreesInTreeGrid(treeGrid: List<List<Int>>): Int {
        var visibleTrees = 0

        treeGrid.forEachIndexed { row, treeRowList ->
            treeRowList.forEachIndexed { column, treeHeight ->
                val isTreeVisible = isTreeVisibleFromDirectory(
                    TreeLookDirectory.Up,
                    treeGrid,
                    row,
                    column,
                    treeGrid[row][column]
                ) || isTreeVisibleFromDirectory(
                    TreeLookDirectory.Down,
                    treeGrid,
                    row,
                    column,
                    treeGrid[row][column]
                ) || isTreeVisibleFromDirectory(
                    TreeLookDirectory.Left,
                    treeGrid,
                    row,
                    column,
                    treeGrid[row][column]
                ) || isTreeVisibleFromDirectory(
                    TreeLookDirectory.Right,
                    treeGrid,
                    row,
                    column,
                    treeGrid[row][column]
                )
                if (isTreeVisible) {
                    println("row: $row")
                    println("column: $column")
                    visibleTrees += 1
                }
            }
        }

        return visibleTrees
    }

    fun calculatingTheFurthestTreeThatCanSee(directory: TreeLookDirectory, treeGrid: List<List<Int>>, row: Int, column: Int, treeHeight: Int): Pair<Int, Int> {

        val (nextTreeRow, nextTreeColumn) = when (directory) {
            TreeLookDirectory.Up -> Pair(row - 1, column)
            TreeLookDirectory.Down -> Pair(row + 1, column)
            TreeLookDirectory.Left -> Pair(row, column - 1)
            TreeLookDirectory.Right -> Pair(row, column + 1)
        }

        if (nextTreeRow < 0 || nextTreeRow > treeGrid.size - 1 || nextTreeColumn < 0 || nextTreeColumn > treeGrid.first().size - 1) {
            return Pair(row, column)
        }

        val nextTreeHeight = treeGrid[nextTreeRow][nextTreeColumn]

        return if (nextTreeHeight < treeHeight) {
            calculatingTheFurthestTreeThatCanSee(directory, treeGrid, nextTreeRow, nextTreeColumn, treeHeight)
        } else {
            Pair(nextTreeRow, nextTreeColumn)
        }
    }

    fun calculatingTheScenicScoreOfTreeGridWithDirectory(directory: TreeLookDirectory, treeGrid: List<List<Int>>, row: Int, column: Int, treeHeight: Int): Int {
        val (furthestTreeRow, furthestTreeColumn) = calculatingTheFurthestTreeThatCanSee(directory, treeGrid, row, column, treeHeight)
        return when (directory) {
            TreeLookDirectory.Up -> row - furthestTreeRow
            TreeLookDirectory.Down -> furthestTreeRow - row
            TreeLookDirectory.Left -> column - furthestTreeColumn
            TreeLookDirectory.Right -> furthestTreeColumn - column
        }
    }

    fun calculatingTheScenicScoresOfTreeGrid(treeGrid: List<List<Int>>): List<List<Int>> {
        return treeGrid.mapIndexed { row, treeRowList ->
            treeRowList.mapIndexed { column, treeHeight ->
                val scenicScoreWithUp = calculatingTheScenicScoreOfTreeGridWithDirectory(
                    TreeLookDirectory.Up,
                    treeGrid,
                    row,
                    column,
                    treeHeight
                )
                val scenicScoreWithDown = calculatingTheScenicScoreOfTreeGridWithDirectory(
                    TreeLookDirectory.Down,
                    treeGrid,
                    row,
                    column,
                    treeHeight
                )
                val scenicScoreWithLeft = calculatingTheScenicScoreOfTreeGridWithDirectory(
                    TreeLookDirectory.Left,
                    treeGrid,
                    row,
                    column,
                    treeHeight
                )
                val scenicScoreWithRight = calculatingTheScenicScoreOfTreeGridWithDirectory(
                    TreeLookDirectory.Right,
                    treeGrid,
                    row,
                    column,
                    treeHeight
                )
                scenicScoreWithUp * scenicScoreWithDown * scenicScoreWithLeft * scenicScoreWithRight
            }
        }
    }

    fun part1(input: List<String>): Int {
        val treeGrid = generatingTreeGrid(input)
        println(treeGrid)

        return calculatingVisibleTreesInTreeGrid(treeGrid)
    }

    fun part2(input: List<String>): Int {
        val treeGrid = generatingTreeGrid(input)
        println(treeGrid)

        val scenicScoreOfTreeGrid = calculatingTheScenicScoresOfTreeGrid(treeGrid)

        return scenicScoreOfTreeGrid.flatten().max()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("2022", "Day08")
    println(part1(input))
    println(part2(input))
}

/**
--- Day 8: Treetop Tree House ---
The expedition comes across a peculiar patch of tall trees all planted carefully in a grid. The Elves explain that a previous expedition planted these trees as a reforestation effort. Now, they're curious if this would be a good location for a tree house.

First, determine whether there is enough tree cover here to keep a tree house hidden. To do this, you need to count the number of trees that are visible from outside the grid when looking directly along a row or column.

The Elves have already launched a quadcopter to generate a map with the height of each tree (your puzzle input). For example:

30373
25512
65332
33549
35390
Each tree is represented as a single digit whose value is its height, where 0 is the shortest and 9 is the tallest.

A tree is visible if all of the other trees between it and an edge of the grid are shorter than it. Only consider trees in the same row or column; that is, only look up, down, left, or right from any given tree.

All of the trees around the edge of the grid are visible - since they are already on the edge, there are no trees to block the view. In this example, that only leaves the interior nine trees to consider:

The top-left 5 is visible from the left and top. (It isn't visible from the right or bottom since other trees of height 5 are in the way.)
The top-middle 5 is visible from the top and right.
The top-right 1 is not visible from any direction; for it to be visible, there would need to only be trees of height 0 between it and an edge.
The left-middle 5 is visible, but only from the right.
The center 3 is not visible from any direction; for it to be visible, there would need to be only trees of at most height 2 between it and an edge.
The right-middle 3 is visible from the right.
In the bottom row, the middle 5 is visible, but the 3 and 4 are not.
With 16 trees visible on the edge and another 5 visible in the interior, a total of 21 trees are visible in this arrangement.

Consider your map; how many trees are visible from outside the grid?

Your puzzle answer was 1807.

--- Part Two ---
Content with the amount of tree cover available, the Elves just need to know the best spot to build their tree house: they would like to be able to see a lot of trees.

To measure the viewing distance from a given tree, look up, down, left, and right from that tree; stop if you reach an edge or at the first tree that is the same height or taller than the tree under consideration. (If a tree is right on the edge, at least one of its viewing distances will be zero.)

The Elves don't care about distant trees taller than those found by the rules above; the proposed tree house has large eaves to keep it dry, so they wouldn't be able to see higher than the tree house anyway.

In the example above, consider the middle 5 in the second row:

30373
25512
65332
33549
35390
Looking up, its view is not blocked; it can see 1 tree (of height 3).
Looking left, its view is blocked immediately; it can see only 1 tree (of height 5, right next to it).
Looking right, its view is not blocked; it can see 2 trees.
Looking down, its view is blocked eventually; it can see 2 trees (one of height 3, then the tree of height 5 that blocks its view).
A tree's scenic score is found by multiplying together its viewing distance in each of the four directions. For this tree, this is 4 (found by multiplying 1 * 1 * 2 * 2).

However, you can do even better: consider the tree of height 5 in the middle of the fourth row:

30373
25512
65332
33549
35390
Looking up, its view is blocked at 2 trees (by another tree with a height of 5).
Looking left, its view is not blocked; it can see 2 trees.
Looking down, its view is also not blocked; it can see 1 tree.
Looking right, its view is blocked at 2 trees (by a massive tree of height 9).
This tree's scenic score is 8 (2 * 2 * 1 * 2); this is the ideal spot for the tree house.

Consider each tree on your map. What is the highest scenic score possible for any tree?

Your puzzle answer was 480000.
 */