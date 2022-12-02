package aoc2021

import utils.readInput

fun main() {
    fun part1(input: List<String>): Int {
        val sevenSegmentDisplayList = input.map { SevenSegmentDisplay.from(it) }
        val digit1478AppearTimes = sevenSegmentDisplayList.fold(0) { result, sevenSegmentDisplay ->
            result + sevenSegmentDisplay.digitOutputValues.count {
                it.count() == 2 || it.count() == 4 || it.count() == 3 || it.count() == 7
            }
        }
        return digit1478AppearTimes
    }

    fun part2(input: List<String>): Int {
        val sevenSegmentDisplayList = input.map { SevenSegmentDisplay.from(it) }
        var allOutputValues: Int = 0
        for (sevenSegmentDisplay in sevenSegmentDisplayList) {
            val patternsSet = sevenSegmentDisplay.uniqueSignalPatterns.map { it.toSet() }
            var digit0Pattern = setOf<Char>()
            var digit1Pattern = setOf<Char>()
            var digit2Pattern = setOf<Char>()
            var digit3Pattern = setOf<Char>()
            var digit4Pattern = setOf<Char>()
            var digit5Pattern = setOf<Char>()
            var digit6Pattern = setOf<Char>()
            var digit7Pattern = setOf<Char>()
            var digit8Pattern = setOf<Char>()
            var digit9Pattern = setOf<Char>()

            patternsSet.forEach { pattern ->
                when (pattern.count()) {
                    2 -> digit1Pattern = pattern
                    4 -> digit4Pattern = pattern
                    3 -> digit7Pattern = pattern
                    7 -> digit8Pattern = pattern
                }
            }

            patternsSet.forEach { pattern ->
                when (pattern.count()) {
                    5 -> when {
                        pattern.containsAll(digit8Pattern subtract digit4Pattern) -> digit2Pattern = pattern
                        pattern.containsAll(digit1Pattern) -> digit3Pattern = pattern
                        pattern.containsAll(digit4Pattern subtract digit1Pattern) -> digit5Pattern = pattern
                    }
                    6 -> when {
                        !pattern.containsAll(digit8Pattern subtract digit7Pattern) && !pattern.containsAll(digit4Pattern) -> digit0Pattern = pattern
                        pattern.containsAll(digit8Pattern subtract digit7Pattern) -> digit6Pattern = pattern
                        pattern.containsAll(digit4Pattern) -> digit9Pattern = pattern
                    }
                }
            }

//            println("digit0Pattern : $digit0Pattern")
//            println("digit1Pattern : $digit1Pattern")
//            println("digit2Pattern : $digit2Pattern")
//            println("digit3Pattern : $digit3Pattern")
//            println("digit4Pattern : $digit4Pattern")
//            println("digit5Pattern : $digit5Pattern")
//            println("digit6Pattern : $digit6Pattern")
//            println("digit7Pattern : $digit7Pattern")
//            println("digit8Pattern : $digit8Pattern")
//            println("digit9Pattern : $digit9Pattern")
            val digitPatternList = listOf(
                digit0Pattern, digit1Pattern, digit2Pattern, digit3Pattern, digit4Pattern,
                digit5Pattern, digit6Pattern, digit7Pattern, digit8Pattern, digit9Pattern
            )

            val outputValue = sevenSegmentDisplay.digitOutputValues.fold("") { digitValue: String, pattern: String ->
                val outputValue = getOutputValue(pattern.toSet(), digitPatternList)
//                println("pattern : $pattern")
//                println("outputValue : $outputValue")
                digitValue + "$outputValue"
            }
//            println("outputValue : $outputValue")
            allOutputValues += outputValue.toInt()
        }
        return allOutputValues
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2021", "Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("2021", "Day08")
    println(part1(input))
    println(part2(input))
}

fun getOutputValue(outputPattern: Set<Char>, digitPatternList: List<Set<Char>>): Int {
    digitPatternList.forEachIndexed { index, set ->
        if (outputPattern == set) {
            return index
        }
    }
    return 0
}

data class SevenSegmentDisplay(
    val uniqueSignalPatterns: List<String>,
    val digitOutputValues: List<String>
) {

    companion object {
        fun from(input: String): SevenSegmentDisplay {
            val inputs = input.split(" | ")
            val uniqueSignalPatterns = inputs.first().split(" ")
            val digitOutputValues = inputs.last().split(" ")

            return SevenSegmentDisplay(uniqueSignalPatterns, digitOutputValues)
        }
    }
}

/*
--- Day 8: Seven Segment Search ---
You barely reach the safety of the cave when the whale smashes into the cave mouth, collapsing it. Sensors indicate another exit to this cave at a much greater depth, so you have no choice but to press on.

As your submarine slowly makes its way through the cave system, you notice that the four-digit seven-segment displays in your submarine are malfunctioning; they must have been damaged during the escape. You'll be in a lot of trouble without them, so you'd better figure out what's wrong.

Each digit of a seven-segment display is rendered by turning on or off any of seven segments named a through g:

  0:      1:      2:      3:      4:
 aaaa    ....    aaaa    aaaa    ....
b    c  .    c  .    c  .    c  b    c
b    c  .    c  .    c  .    c  b    c
 ....    ....    dddd    dddd    dddd
e    f  .    f  e    .  .    f  .    f
e    f  .    f  e    .  .    f  .    f
 gggg    ....    gggg    gggg    ....

  5:      6:      7:      8:      9:
 aaaa    aaaa    aaaa    aaaa    aaaa
b    .  b    .  .    c  b    c  b    c
b    .  b    .  .    c  b    c  b    c
 dddd    dddd    ....    dddd    dddd
.    f  e    f  .    f  e    f  .    f
.    f  e    f  .    f  e    f  .    f
 gggg    gggg    ....    gggg    gggg
So, to render a 1, only segments c and f would be turned on; the rest would be off. To render a 7, only segments a, c, and f would be turned on.

The problem is that the signals which control the segments have been mixed up on each display. The submarine is still trying to display numbers by producing output on signal wires a through g, but those wires are connected to segments randomly. Worse, the wire/segment connections are mixed up separately for each four-digit display! (All of the digits within a display use the same connections, though.)

So, you might know that only signal wires b and g are turned on, but that doesn't mean segments b and g are turned on: the only digit that uses two segments is 1, so it must mean segments c and f are meant to be on. With just that information, you still can't tell which wire (b/g) goes to which segment (c/f). For that, you'll need to collect more information.

For each display, you watch the changing signals for a while, make a note of all ten unique signal patterns you see, and then write down a single four digit output value (your puzzle input). Using the signal patterns, you should be able to work out which pattern corresponds to which digit.

For example, here is what you might see in a single entry in your notes:

acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab |
cdfeb fcadb cdfeb cdbaf
(The entry is wrapped here to two lines so it fits; in your notes, it will all be on a single line.)

Each entry consists of ten unique signal patterns, a | delimiter, and finally the four digit output value. Within an entry, the same wire/segment connections are used (but you don't know what the connections actually are). The unique signal patterns correspond to the ten different ways the submarine tries to render a digit using the current wire/segment connections. Because 7 is the only digit that uses three segments, dab in the above example means that to render a 7, signal lines d, a, and b are on. Because 4 is the only digit that uses four segments, eafb means that to render a 4, signal lines e, a, f, and b are on.

Using this information, you should be able to work out which combination of signal wires corresponds to each of the ten digits. Then, you can decode the four digit output value. Unfortunately, in the above example, all of the digits in the output value (cdfeb fcadb cdfeb cdbaf) use five segments and are more difficult to deduce.

For now, focus on the easy digits. Consider this larger example:

be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb |
fdgacbe cefdb cefbgd gcbe
edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec |
fcgedb cgb dgebacf gc
fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef |
cg cg fdcagb cbg
fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega |
efabcd cedba gadfec cb
aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga |
gecf egdcabf bgf bfgea
fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf |
gebdcfa ecba ca fadegcb
dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf |
cefg dcbef fcge gbcadfe
bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd |
ed bcgafe cdgba cbgef
egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg |
gbdfcae bgc cg cgb
gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc |
fgae cfgab fg bagce
Because the digits 1, 4, 7, and 8 each use a unique number of segments, you should be able to tell which combinations of signals correspond to those digits. Counting only digits in the output values (the part after | on each line), in the above example, there are 26 instances of digits that use a unique number of segments (highlighted above).

In the output values, how many times do digits 1, 4, 7, or 8 appear?

Your puzzle answer was 521.

--- Part Two ---
Through a little deduction, you should now be able to determine the remaining digits. Consider again the first example above:

acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab |
cdfeb fcadb cdfeb cdbaf
After some careful analysis, the mapping between signal wires and segments only make sense in the following configuration:

 dddd
e    a
e    a
 ffff
g    b
g    b
 cccc
So, the unique signal patterns would correspond to the following digits:

acedgfb: 8
cdfbe: 5
gcdfa: 2
fbcad: 3
dab: 7
cefabd: 9
cdfgeb: 6
eafb: 4
cagedb: 0
ab: 1
Then, the four digits of the output value can be decoded:

cdfeb: 5
fcadb: 3
cdfeb: 5
cdbaf: 3
Therefore, the output value for this entry is 5353.

Following this same process for each entry in the second, larger example above, the output value of each entry can be determined:

fdgacbe cefdb cefbgd gcbe: 8394
fcgedb cgb dgebacf gc: 9781
cg cg fdcagb cbg: 1197
efabcd cedba gadfec cb: 9361
gecf egdcabf bgf bfgea: 4873
gebdcfa ecba ca fadegcb: 8418
cefg dcbef fcge gbcadfe: 4548
ed bcgafe cdgba cbgef: 1625
gbdfcae bgc cg cgb: 8717
fgae cfgab fg bagce: 4315
Adding all of the output values in this larger example produces 61229.

For each entry, determine all of the wire/segment connections and decode the four-digit output values. What do you get if you add up all of the output values?

Your puzzle answer was 1016804.

Both parts of this puzzle are complete! They provide two gold stars: **

At this point, you should return to your Advent calendar and try another puzzle.

If you still want to see it, you can get your puzzle input.

You can also [Share] this puzzle.
 */