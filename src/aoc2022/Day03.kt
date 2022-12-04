import utils.readInput

fun main() {

    fun findCommonItem(firstCompartmentItem: String, secondCompartmentItem: String): Char {
        firstCompartmentItem.forEach { firstItem ->
            secondCompartmentItem.forEach { secondItem ->
                if (firstItem == secondItem) {
                    return firstItem
                }
            }
        }
        return 'a'
    }

    fun findBadgeItemType(firstItems: String, secondItems: String, thirdItems: String): Char {
        firstItems.forEach { firstItem ->
            secondItems.forEach { secondItem ->
                thirdItems.forEach { thirdItem ->
                    if (firstItem == secondItem && secondItem == thirdItem) {
                        return firstItem
                    }
                }
            }
        }
        return 'a'
    }

    fun calculatingPriorityOfItemType(item: Char): Int {
        var priority = when (item.lowercaseChar()) {
            'a' -> 1
            'b' -> 2
            'c' -> 3
            'd' -> 4
            'e' -> 5
            'f' -> 6
            'g' -> 7
            'h' -> 8
            'i' -> 9
            'j' -> 10
            'k' -> 11
            'l' -> 12
            'm' -> 13
            'n' -> 14
            'o' -> 15
            'p' -> 16
            'q' -> 17
            'r' -> 18
            's' -> 19
            't' -> 20
            'u' -> 21
            'v' -> 22
            'w' -> 23
            'x' -> 24
            'y' -> 25
            'z' -> 26
            else -> 0
        }
        if (item.isUpperCase()) {
            priority += 26
        }
        return priority
    }

    fun part1(input: List<String>): Int {
        var sumOfPriorities = 0
        for (items in input) {
            val firstCompartmentItem = items.substring(0, items.length / 2)
            val secondCompartmentItem = items.substring(items.length / 2, items.length)
            println("$firstCompartmentItem")
            println("$secondCompartmentItem")
            val commonItem = findCommonItem(firstCompartmentItem, secondCompartmentItem)
            val priorityOfCommomItem = calculatingPriorityOfItemType(commonItem)
            sumOfPriorities += priorityOfCommomItem
        }

        return sumOfPriorities
    }

    fun part2(input: List<String>): Int {
        var sumOfBadgesPriorities = 0
        for (i in input.indices step 3) {
            val firstItems = input[i]
            val secondItems = input[i + 1]
            val thirdItems = input[i + 2]
            val badgeItemType = findBadgeItemType(firstItems, secondItems, thirdItems)
            val priorityOfBadgeItem = calculatingPriorityOfItemType(badgeItemType)
            sumOfBadgesPriorities += priorityOfBadgeItem
        }

        return sumOfBadgesPriorities
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day03_test")
    check(part1(testInput) == 157)

    val input = readInput("2022", "Day03")
    println(part1(input))
    println(part2(input))
}