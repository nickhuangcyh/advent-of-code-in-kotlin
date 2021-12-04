fun main() {
    fun part1(drawList: List<Int>, bingoBoardList: List<BingoBoard>): Int {
        var winnersBingoBoard: BingoBoard? = null
        var lastDrawNumber: Int = drawList.first()

        run gameLoop@ {
            drawList.forEach { drawNumber ->
                bingoBoardList.forEach { bingoBoard ->
                    bingoBoard.mark(drawNumber)
                    val isBingo = bingoBoard.checkBoard()
                    if (isBingo) {
                        lastDrawNumber = drawNumber
                        winnersBingoBoard = bingoBoard
                        return@gameLoop
                    }
                }
            }
        }

        winnersBingoBoard?.let { bingoBoard ->
            val sumOfUnmarkedValues = bingoBoard.unmarkedValues.fold(0) { sum: Int, value: Int ->
                sum + value
            }
            println("sumOfUnmarkedValues: $sumOfUnmarkedValues")
            println("lastDrawNumber: $lastDrawNumber")
            return sumOfUnmarkedValues * lastDrawNumber
        }
        return 0
    }

    fun part2(drawList: List<Int>, bingoBoardList: List<BingoBoard>): Int {
        var losersBingoBoard: BingoBoard? = null
        var lastDrawNumber: Int = drawList.first()
        var bingoBoardWinList = bingoBoardList.map { false }.toMutableList()

        run gameLoop@ {
            drawList.forEach { drawNumber ->
                bingoBoardList.forEachIndexed { index, bingoBoard ->
                    bingoBoard.mark(drawNumber)
                    val isBingo = bingoBoard.checkBoard()
                    if (isBingo) {
                        lastDrawNumber = drawNumber
                        losersBingoBoard = bingoBoard
                        bingoBoardWinList[index] = true
                        val isAllWin = bingoBoardWinList.all { it }
                        if (isAllWin) return@gameLoop
                    }
                }
            }
        }

        losersBingoBoard?.let { bingoBoard ->
            val sumOfUnmarkedValues = bingoBoard.unmarkedValues.fold(0) { sum: Int, value: Int ->
                sum + value
            }
            println("sumOfUnmarkedValues: $sumOfUnmarkedValues")
            println("lastDrawNumber: $lastDrawNumber")
            return sumOfUnmarkedValues * lastDrawNumber
        }
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    val (testDrawList, testBingoBoardList) = getDrawListAndBingoBoards(testInput)
    check(part1(testDrawList, testBingoBoardList) == 4512)
    check(part2(testDrawList, testBingoBoardList) == 1924)

    val input = readInput("Day04")
    val (drawList, bingoBoardList) = getDrawListAndBingoBoards(input)
    println(part1(drawList, bingoBoardList))
    println(part2(drawList, bingoBoardList))

}

fun getDrawListAndBingoBoards(input: List<String>): Pair<List<Int>, List<BingoBoard>> {
    val drawList = input.first().split(",").map { it.toInt() }
    val bingoBoardStringList = input.drop(1).chunked(6).map { singleBoardLines ->
        singleBoardLines.filter { singleBoardLine ->
            singleBoardLine.isNotBlank()
        }
    }
    val bingoBoardList = bingoBoardStringList.map { BingoBoard(it) }
    return Pair(drawList, bingoBoardList)
}

data class BingoValue(val value: Int, var marked: Boolean = false)

class BingoBoard {
    val columns: Int
    val rows: Int
    val board = mutableListOf<List<BingoValue>>()
    val unmarkedValues: List<Int>
        get() = board.flatten().filter { !it.marked }.map { it.value }

    constructor(list: List<String>) {
        list.forEach { valueList ->
            val bingoValueList = valueList
                .split("  ", " ")
                .filter { it.isNotBlank() }
                .map {
                    BingoValue(it.toInt())
                }
            board.add(bingoValueList)
        }

        columns = board.first().count()
        rows = board.count()
    }

    fun mark(value: Int) {
        board.forEach { bingoValueList ->
            bingoValueList.forEach {
                if (it.value == value) {
                    it.marked = true
                }
            }
        }
    }

    fun checkBoard(): Boolean {
        return checkRow() || checkColumn()
    }

    fun checkRow(): Boolean {
        return board.any { bingoValueList -> bingoValueList.all { it.marked } }
    }

    fun checkColumn(): Boolean {
        (0 until columns).forEach { colume ->
            var isColumeBingo = true
            (0 until rows).forEach { row ->
                if (!board[row][colume].marked) {
                    isColumeBingo = false
                }
            }
            if (isColumeBingo) return true
        }
        return false
    }
}