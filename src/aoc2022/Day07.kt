package aoc2022

import utils.readInput

interface FileSystemNode {
    val name: String
    val parentNode: FileSystemNode?
    val size: Int
    val detailDescription: String
    val descriptionPadding: String
        get() {
            var level = 0
            var node = parentNode
            while (node != null) {
                level++
                node = node.parentNode
            }
            return "  ".repeat(level)
        }

    fun fullDescription(): String {
        val desc = "$descriptionPadding - $name $detailDescription"
        return desc
    }
}

class FileNode(
    override val name: String,
    override val parentNode: FileSystemNode?,
    override val size: Int
) : FileSystemNode {

    override val detailDescription: String
        get() = "(file, size=$size)"

}

class DirectoryNode(
    override val name: String,
    override val parentNode: FileSystemNode?,
    val childNodeList: MutableList<FileSystemNode> = mutableListOf()
) : FileSystemNode {
    override val size: Int
        get() = childNodeList.sumOf { it.size }

    override val detailDescription: String
        get() = "(dir)"

    override fun fullDescription(): String {
        return super.fullDescription() + childNodeList.joinToString { "\n" + it.fullDescription() }
    }

    fun addFileNode(name: String, size: Int) {
        val fileNode = childNodeList.firstOrNull { it.name == name && it is FileNode }
        if (fileNode == null) {
            childNodeList.add(FileNode(name, this, size))
        }
    }

    fun addDirectoryNode(name: String) {
        val directoryNode = childNodeList.firstOrNull { it.name == name && it is DirectoryNode }
        if (directoryNode == null) {
            childNodeList.add(DirectoryNode(name, this))
        }
    }
}

fun main() {

    fun changeDirectory(directoryName: String, rootNode: FileSystemNode, currentNode: FileSystemNode?): FileSystemNode? {
        return when (directoryName) {
            ".." -> currentNode?.parentNode
            "/" -> rootNode
            else -> when (currentNode) {
                is DirectoryNode -> currentNode.childNodeList.firstOrNull { it.name == directoryName }
                else -> currentNode
            }
        }
    }

    fun createANewDirectoryNode(name: String, currentNode: FileSystemNode?) {
        if (currentNode is DirectoryNode) {
            currentNode.addDirectoryNode(name)
        }
    }

    fun createANewFileNode(name: String, size: Int, currentNode: FileSystemNode?) {
        if (currentNode is DirectoryNode) {
            currentNode.addFileNode(name, size)
        }
    }

    fun parsingCommandToGeneratingFilesystemTree(inputList: List<String>): FileSystemNode {
        val rootNode = DirectoryNode("/", null, mutableListOf())
        var currentNode: FileSystemNode? = rootNode
        for (input in inputList) {
            val inputDetailList = input.split(" ")
            when (inputDetailList.first()) {
                "$" -> when (inputDetailList[1]) {
                    "cd" -> currentNode = changeDirectory(inputDetailList[2], rootNode, currentNode)
                    "ls" -> {}
                }
                "dir" -> createANewDirectoryNode(inputDetailList[1], currentNode)
                else -> createANewFileNode(inputDetailList[1], inputDetailList[0].toInt(), currentNode)
            }
        }
        return rootNode
    }

    fun findAllOfDirWithATotalSizeOfAtMost100000(node: FileSystemNode): List<FileSystemNode> {
        return when (node) {
            is FileNode -> emptyList()
            is DirectoryNode -> {
                val nodeList = if (node.size <= 100000) {
                    listOf<FileSystemNode>(node)
                } else {
                    emptyList()
                }
                nodeList + node.childNodeList.flatMap { findAllOfDirWithATotalSizeOfAtMost100000(it) }
            }

            else -> emptyList()
        }
    }

    fun findAllOfDirThatCanMakeSystemUpdatedIfDeleted(node: FileSystemNode, neededSpace: Int): List<FileSystemNode> {
        return when (node) {
            is FileNode -> emptyList()
            is DirectoryNode -> {
                val nodeList = if (node.size >= neededSpace) {
                    listOf<FileSystemNode>(node)
                } else {
                    emptyList()
                }
                nodeList + node.childNodeList.flatMap { findAllOfDirThatCanMakeSystemUpdatedIfDeleted(it, neededSpace) }
            }
            else -> emptyList()
        }
    }

    fun findSmallestSizeNode(nodeList: List<FileSystemNode>): FileSystemNode {
        var smallestNode = nodeList.first()
        nodeList.forEach {
            if (smallestNode.size > it.size) {
                smallestNode = it
            }
        }
        return smallestNode
    }

    fun part1(input: List<String>): Int {
        val rootNode = parsingCommandToGeneratingFilesystemTree(input)
        println(rootNode.fullDescription())

        val nodeList = findAllOfDirWithATotalSizeOfAtMost100000(rootNode)
        return nodeList.sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val rootNode = parsingCommandToGeneratingFilesystemTree(input)
        println(rootNode.fullDescription())

        val unusedSpace = 70000000 - rootNode.size
        val neededSpace = 30000000 - unusedSpace
        val nodeList = findAllOfDirThatCanMakeSystemUpdatedIfDeleted(rootNode, neededSpace)

        return findSmallestSizeNode(nodeList).size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("2022", "Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("2022", "Day07")
    println(part1(input))
    println(part2(input))
}