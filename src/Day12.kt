fun main() {
    fun part1(input: List<String>): Int {
        return findAllPathsOfCaveMap(input, Cave.VisitLimitation.VISIT_SMALL_CAVE_AT_MOST_ONCE)
    }

    fun part2(input: List<String>): Int {
        return findAllPathsOfCaveMap(input, Cave.VisitLimitation.VISIT_SINGLE_SMALL_CAVE_TWICE)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)
    check(part2(testInput) == 36)

    val test1Input = readInput("Day12_test1")
    check(part1(test1Input) == 19)
    check(part2(test1Input) == 103)

    val test2Input = readInput("Day12_test2")
    check(part1(test2Input) == 226)
    check(part2(test2Input) == 3509)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private fun findAllPathsOfCaveMap(input: List<String>, visitLimitation: Cave.VisitLimitation): Int {
    val caveMap = CaveMapFactory.build(input)
    println(caveMap)
    val startCave = caveMap.first { it is Cave.StartCave }
    val paths = startCave.findAllPath(emptyList(), visitLimitation)

    // print all paths
    paths.forEach { path ->
        println(path.map { it.name })
    }

    return paths.count()
}

object CaveMapFactory {
    fun build(input: List<String>): Set<Cave> {
        val caves = mutableSetOf<Cave>()
        input.forEach {
            val cavesName = it.split("-")
            val firstCaveName = cavesName.first()
            val lastCaveName = cavesName.last()
            val firstCave = caves.firstOrNull { it.name == firstCaveName } ?: Cave.from(firstCaveName)
            val lastCave = caves.firstOrNull { it.name == lastCaveName } ?: Cave.from(lastCaveName)
            firstCave.connectedCaves.add(lastCave)
            lastCave.connectedCaves.add(firstCave)
            caves.addAll(listOf(firstCave, lastCave))
        }
        return caves
    }
}

sealed class Cave(val name: String, val connectedCaves: MutableSet<Cave>) {
    class StartCave(name: String, connectedCaves: MutableSet<Cave> = mutableSetOf()): Cave(name, connectedCaves)
    class EndCave(name: String, connectedCaves: MutableSet<Cave> = mutableSetOf()): Cave(name, connectedCaves)
    class BigCave(name: String, connectedCaves: MutableSet<Cave> = mutableSetOf()): Cave(name, connectedCaves)
    class SmallCave(name: String, connectedCaves: MutableSet<Cave> = mutableSetOf()): Cave(name, connectedCaves)

    enum class VisitLimitation {
        VISIT_SMALL_CAVE_AT_MOST_ONCE,
        VISIT_SINGLE_SMALL_CAVE_TWICE;
    }

    fun findAllPath(currentPath: List<Cave>, visitLimitation: VisitLimitation): List<List<Cave>> {
        when (visitLimitation) {
            VisitLimitation.VISIT_SMALL_CAVE_AT_MOST_ONCE -> {
                if (currentPath.contains(this) && (this is SmallCave || this is StartCave)) {
                    return emptyList()
                }
            }
            VisitLimitation.VISIT_SINGLE_SMALL_CAVE_TWICE -> {
                val smallCaveCountMap = mutableMapOf<String, Int>()
                currentPath.filter { it is SmallCave }.forEach {
                    val count = smallCaveCountMap[it.name] ?: 0
                    smallCaveCountMap[it.name] = count + 1
                }
                val isVisitSingleCaveTwice = smallCaveCountMap.containsValue(2)
                when (isVisitSingleCaveTwice) {
                    true -> if (currentPath.contains(this) && (this is SmallCave || this is StartCave)) {
                        return emptyList()
                    }
                    false -> if (currentPath.contains(this) && this is StartCave) {
                        return emptyList()
                    }
                }
            }
        }

        val newCurrentPath = currentPath.toMutableList()
        newCurrentPath.add(this)

        if (this is EndCave) {
            return listOf(newCurrentPath)
        }


        val pathList = mutableListOf<List<Cave>>()
        for (connectedCave in connectedCaves) {
            val path = connectedCave.findAllPath(newCurrentPath, visitLimitation)
            if (path.isNotEmpty()) {
                pathList.addAll(path)
            }
        }
        return pathList
    }

    companion object {
        fun from(name: String): Cave {
            return when {
                name == "start" -> StartCave(name)
                name == "end" -> EndCave(name)
                name.first().isLowerCase() -> SmallCave(name)
                name.first().isUpperCase() -> BigCave(name)
                else -> SmallCave(name)
            }
        }
    }
}

/*
--- Day 12: Passage Pathing ---
With your submarine's subterranean subsystems subsisting suboptimally, the only way you're getting out of this cave anytime soon is by finding a path yourself. Not just a path - the only way to know if you've found the best path is to find all of them.

Fortunately, the sensors are still mostly working, and so you build a rough map of the remaining caves (your puzzle input). For example:

start-A
start-b
A-c
A-b
b-d
A-end
b-end
This is a list of how all of the caves are connected. You start in the cave named start, and your destination is the cave named end. An entry like b-d means that cave b is connected to cave d - that is, you can move between them.

So, the above cave system looks roughly like this:

    start
    /   \
c--A-----b--d
    \   /
     end
Your goal is to find the number of distinct paths that start at start, end at end, and don't visit small caves more than once. There are two types of caves: big caves (written in uppercase, like A) and small caves (written in lowercase, like b). It would be a waste of time to visit any small cave more than once, but big caves are large enough that it might be worth visiting them multiple times. So, all paths you find should visit small caves at most once, and can visit big caves any number of times.

Given these rules, there are 10 paths through this example cave system:

start,A,b,A,c,A,end
start,A,b,A,end
start,A,b,end
start,A,c,A,b,A,end
start,A,c,A,b,end
start,A,c,A,end
start,A,end
start,b,A,c,A,end
start,b,A,end
start,b,end
(Each line in the above list corresponds to a single path; the caves visited by that path are listed in the order they are visited and separated by commas.)

Note that in this cave system, cave d is never visited by any path: to do so, cave b would need to be visited twice (once on the way to cave d and a second time when returning from cave d), and since cave b is small, this is not allowed.

Here is a slightly larger example:

dc-end
HN-start
start-kj
dc-start
dc-HN
LN-dc
HN-end
kj-sa
kj-HN
kj-dc
The 19 paths through it are as follows:

start,HN,dc,HN,end
start,HN,dc,HN,kj,HN,end
start,HN,dc,end
start,HN,dc,kj,HN,end
start,HN,end
start,HN,kj,HN,dc,HN,end
start,HN,kj,HN,dc,end
start,HN,kj,HN,end
start,HN,kj,dc,HN,end
start,HN,kj,dc,end
start,dc,HN,end
start,dc,HN,kj,HN,end
start,dc,end
start,dc,kj,HN,end
start,kj,HN,dc,HN,end
start,kj,HN,dc,end
start,kj,HN,end
start,kj,dc,HN,end
start,kj,dc,end
Finally, this even larger example has 226 paths through it:

fs-end
he-DX
fs-he
start-DX
pj-DX
end-zg
zg-sl
zg-pj
pj-he
RW-he
fs-DX
pj-RW
zg-RW
start-pj
he-WI
zg-he
pj-fs
start-RW
How many paths through this cave system are there that visit small caves at most once?

Your puzzle answer was 4773.

--- Part Two ---
After reviewing the available paths, you realize you might have time to visit a single small cave twice. Specifically, big caves can be visited any number of times, a single small cave can be visited at most twice, and the remaining small caves can be visited at most once. However, the caves named start and end can only be visited exactly once each: once you leave the start cave, you may not return to it, and once you reach the end cave, the path must end immediately.

Now, the 36 possible paths through the first example above are:

start,A,b,A,b,A,c,A,end
start,A,b,A,b,A,end
start,A,b,A,b,end
start,A,b,A,c,A,b,A,end
start,A,b,A,c,A,b,end
start,A,b,A,c,A,c,A,end
start,A,b,A,c,A,end
start,A,b,A,end
start,A,b,d,b,A,c,A,end
start,A,b,d,b,A,end
start,A,b,d,b,end
start,A,b,end
start,A,c,A,b,A,b,A,end
start,A,c,A,b,A,b,end
start,A,c,A,b,A,c,A,end
start,A,c,A,b,A,end
start,A,c,A,b,d,b,A,end
start,A,c,A,b,d,b,end
start,A,c,A,b,end
start,A,c,A,c,A,b,A,end
start,A,c,A,c,A,b,end
start,A,c,A,c,A,end
start,A,c,A,end
start,A,end
start,b,A,b,A,c,A,end
start,b,A,b,A,end
start,b,A,b,end
start,b,A,c,A,b,A,end
start,b,A,c,A,b,end
start,b,A,c,A,c,A,end
start,b,A,c,A,end
start,b,A,end
start,b,d,b,A,c,A,end
start,b,d,b,A,end
start,b,d,b,end
start,b,end
The slightly larger example above now has 103 paths through it, and the even larger example now has 3509 paths through it.

Given these new rules, how many paths through this cave system are there?

Your puzzle answer was 116985.

Both parts of this puzzle are complete! They provide two gold stars: **

At this point, you should return to your Advent calendar and try another puzzle.

If you still want to see it, you can get your puzzle input.

You can also [Share] this puzzle.
 */