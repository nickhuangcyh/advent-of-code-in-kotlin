package aoc2023

import utils.readInput

fun main() {

    fun getTheFirstDigitInTheString(input: String): Int {
        for (c in input) {
            if (c.isDigit()) {
                return c.digitToInt()
            }
        }
        return 0
    }

    fun getCalibrationValueThatElvesRecovered(input: String): Int {
        val firstDigitNumber = getTheFirstDigitInTheString(input)
        val lastDigitNumber = getTheFirstDigitInTheString(input.reversed())
        return firstDigitNumber * 10 + lastDigitNumber
    }

    fun getAllCalibrationValuesSumThatElvesRecovered(input: List<String>): Int {
        return input.map { getCalibrationValueThatElvesRecovered(it) }.sum()
    }

    fun getAllCalibrationValuesSumWithSpelledOutWithLettersThatElvesRecovered(input: List<String>): Int {
        return input
            .map {
            it.replace("one", "o1e")
                .replace("two", "t2o")
                .replace("three", "t3e")
                .replace("four", "f4r")
                .replace("five", "f5e")
                .replace("six", "s6x")
                .replace("seven", "s7n")
                .replace("eight", "e8t")
                .replace("nine", "n9e")
            }
            .map {
//                println(it)
                getCalibrationValueThatElvesRecovered(it)
            }.map {
//                println(it)
                it
            }.sum()
    }

    fun part1(input: List<String>): Int {

        return getAllCalibrationValuesSumThatElvesRecovered(input)
    }

    fun part2(input: List<String>): Int {

        return getAllCalibrationValuesSumWithSpelledOutWithLettersThatElvesRecovered(input)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2023", "Day01_test")
    val testPart2Input = readInput("2023", "Day01_2_test")
    check(part1(testInput) == 142)
    check(part2(testPart2Input) == 281)

    val input = readInput("2023", "Day01")
    println(part1(input))
    println(part2(input))
}