package aoc2021

import utils.readInput

fun main() {
    fun part1(input: List<String>): Int {
        val totalBitCounter = MutableList(input.first().count()) { _ -> BitCounter() }

        for (encodedInfo in input) {
            encodedInfo.forEachIndexed { index, char ->
                val bitValue = Character.getNumericValue(char)
                val bitCounter = totalBitCounter[index]
                when (bitValue) {
                    0 -> bitCounter.zeroCount += 1
                    1 -> bitCounter.oneCount += 1
                }
            }
        }

        var gammaRate = totalBitCounter.joinToString("") {
            if (it.zeroCount > it.oneCount) {
                "0"
            } else {
                "1"
            }
        }.toInt(2)
        val epsilonRate = totalBitCounter.joinToString("") {
            if (it.zeroCount > it.oneCount) {
                "1"
            } else {
                "0"
            }
        }.toInt(2)
        println("gammaRate: $gammaRate")
        println("epsilonRate: $epsilonRate")

        val powerConsumption = gammaRate * epsilonRate
        println("powerConsumption: $powerConsumption")
        return powerConsumption
    }

    fun part2(input: List<String>): Int {
        var remainingOxygenGeneratorRatingList = input
        var remainingCo2ScrubberRatingList = input

        (0 until input.first().count()).forEach { bitIndex ->
            // oxygen generator rating
            val oxygenBitCounter = calculateBitCount(remainingOxygenGeneratorRatingList, bitIndex)
            val oxygenBitCriteria = when {
                oxygenBitCounter.zeroCount > oxygenBitCounter.oneCount -> 0
                oxygenBitCounter.zeroCount <= oxygenBitCounter.oneCount -> 1
                else -> 1
            }
            if (remainingOxygenGeneratorRatingList.count() > 1) {
                remainingOxygenGeneratorRatingList = remainingOxygenGeneratorRatingList.filter {
                    val bitValue = Character.getNumericValue(it[bitIndex])
                    bitValue == oxygenBitCriteria
                }
                println("remainingOxygenGeneratorRatingList: $remainingOxygenGeneratorRatingList")
            }

            // CO2 scrubber rating
            val co2BitCounter = calculateBitCount(remainingCo2ScrubberRatingList, bitIndex)
            val co2BitCriteria = when {
                co2BitCounter.zeroCount > co2BitCounter.oneCount -> 1
                co2BitCounter.zeroCount <= co2BitCounter.oneCount -> 0
                else -> 1
            }
            if (remainingCo2ScrubberRatingList.count() > 1) {
                remainingCo2ScrubberRatingList = remainingCo2ScrubberRatingList.filter {
                    val bitValue = Character.getNumericValue(it[bitIndex])
                    bitValue == co2BitCriteria
                }
                println("remainingCo2ScrubberRatingList: $remainingCo2ScrubberRatingList")
            }

            if (remainingOxygenGeneratorRatingList.count() == 1 && remainingCo2ScrubberRatingList.count() == 1) {
                return@forEach
            }
        }

        val oxygenGeneratorRating = remainingOxygenGeneratorRatingList.first().toInt(2)
        val co2GeneratorRating = remainingCo2ScrubberRatingList.first().toInt(2)
        println("oxygenGeneratorRating: $oxygenGeneratorRating")
        println("co2GeneratorRating: $co2GeneratorRating")

        val lifeSupportRating = oxygenGeneratorRating * co2GeneratorRating
        println("lifeSupportRating: $lifeSupportRating")
        return lifeSupportRating
    }



    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2021", "Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("2021", "Day03")
    println(part1(input))
    println(part2(input))
}

fun calculateBitCount(input: List<String>, bitIndex: Int): BitCounter {
    val bitCounter = BitCounter()
    input.forEach {
        val bitValue = Character.getNumericValue(it[bitIndex])
        when (bitValue) {
            0 -> bitCounter.zeroCount += 1
            1 -> bitCounter.oneCount += 1
        }
    }
    return bitCounter
}

data class BitCounter(var zeroCount: Int = 0, var oneCount: Int = 0)

/*
--- Day 3: Binary Diagnostic ---
The submarine has been making some odd creaking noises, so you ask it to produce a diagnostic report just in case.

The diagnostic report (your puzzle input) consists of a list of binary numbers which, when decoded properly, can tell you many useful things about the conditions of the submarine. The first parameter to check is the power consumption.

You need to use the binary numbers in the diagnostic report to generate two new binary numbers (called the gamma rate and the epsilon rate). The power consumption can then be found by multiplying the gamma rate by the epsilon rate.

Each bit in the gamma rate can be determined by finding the most common bit in the corresponding position of all numbers in the diagnostic report. For example, given the following diagnostic report:

00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010
Considering only the first bit of each number, there are five 0 bits and seven 1 bits. Since the most common bit is 1, the first bit of the gamma rate is 1.

The most common second bit of the numbers in the diagnostic report is 0, so the second bit of the gamma rate is 0.

The most common value of the third, fourth, and fifth bits are 1, 1, and 0, respectively, and so the final three bits of the gamma rate are 110.

So, the gamma rate is the binary number 10110, or 22 in decimal.

The epsilon rate is calculated in a similar way; rather than use the most common bit, the least common bit from each position is used. So, the epsilon rate is 01001, or 9 in decimal. Multiplying the gamma rate (22) by the epsilon rate (9) produces the power consumption, 198.

Use the binary numbers in your diagnostic report to calculate the gamma rate and epsilon rate, then multiply them together. What is the power consumption of the submarine? (Be sure to represent your answer in decimal, not binary.)

Your puzzle answer was 2583164.

--- Part Two ---
Next, you should verify the life support rating, which can be determined by multiplying the oxygen generator rating by the CO2 scrubber rating.

Both the oxygen generator rating and the CO2 scrubber rating are values that can be found in your diagnostic report - finding them is the tricky part. Both values are located using a similar process that involves filtering out values until only one remains. Before searching for either rating value, start with the full list of binary numbers from your diagnostic report and consider just the first bit of those numbers. Then:

Keep only numbers selected by the bit criteria for the type of rating value for which you are searching. Discard numbers which do not match the bit criteria.
If you only have one number left, stop; this is the rating value for which you are searching.
Otherwise, repeat the process, considering the next bit to the right.
The bit criteria depends on which type of rating value you want to find:

To find oxygen generator rating, determine the most common value (0 or 1) in the current bit position, and keep only numbers with that bit in that position. If 0 and 1 are equally common, keep values with a 1 in the position being considered.
To find CO2 scrubber rating, determine the least common value (0 or 1) in the current bit position, and keep only numbers with that bit in that position. If 0 and 1 are equally common, keep values with a 0 in the position being considered.
For example, to determine the oxygen generator rating value using the same example diagnostic report from above:

Start with all 12 numbers and consider only the first bit of each number. There are more 1 bits (7) than 0 bits (5), so keep only the 7 numbers with a 1 in the first position: 11110, 10110, 10111, 10101, 11100, 10000, and 11001.
Then, consider the second bit of the 7 remaining numbers: there are more 0 bits (4) than 1 bits (3), so keep only the 4 numbers with a 0 in the second position: 10110, 10111, 10101, and 10000.
In the third position, three of the four numbers have a 1, so keep those three: 10110, 10111, and 10101.
In the fourth position, two of the three numbers have a 1, so keep those two: 10110 and 10111.
In the fifth position, there are an equal number of 0 bits and 1 bits (one each). So, to find the oxygen generator rating, keep the number with a 1 in that position: 10111.
As there is only one number left, stop; the oxygen generator rating is 10111, or 23 in decimal.
Then, to determine the CO2 scrubber rating value from the same example above:

Start again with all 12 numbers and consider only the first bit of each number. There are fewer 0 bits (5) than 1 bits (7), so keep only the 5 numbers with a 0 in the first position: 00100, 01111, 00111, 00010, and 01010.
Then, consider the second bit of the 5 remaining numbers: there are fewer 1 bits (2) than 0 bits (3), so keep only the 2 numbers with a 1 in the second position: 01111 and 01010.
In the third position, there are an equal number of 0 bits and 1 bits (one each). So, to find the CO2 scrubber rating, keep the number with a 0 in that position: 01010.
As there is only one number left, stop; the CO2 scrubber rating is 01010, or 10 in decimal.
Finally, to find the life support rating, multiply the oxygen generator rating (23) by the CO2 scrubber rating (10) to get 230.

Use the binary numbers in your diagnostic report to calculate the oxygen generator rating and CO2 scrubber rating, then multiply them together. What is the life support rating of the submarine? (Be sure to represent your answer in decimal, not binary.)

Your puzzle answer was 2784375.

Both parts of this puzzle are complete! They provide two gold stars: **

At this point, you should return to your Advent calendar and try another puzzle.

If you still want to see it, you can get your puzzle input.

You can also [Share] this puzzle.
 */