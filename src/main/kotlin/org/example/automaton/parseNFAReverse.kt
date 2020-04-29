package org.example.automaton


fun parseReversed(text: String): NFA {
    val stack: MutableList<NFA> = mutableListOf<NFA>()
    for (char: Char in text) {
        when (char) {
            '*' -> {
                val last = stack.removeAt(stack.lastIndex)
                stack.add(last.closure())
            }
            '_' -> {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)
                stack.add(a + b)
            }
            '|' -> {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)
                stack.add(a or b)
            }
            '(' -> {
            }
            ')' -> {
            }
            in Regex("^[a-zA-Z]$") -> {
                stack.add(NFA(char))
            }
        }
    }
    return checkNotNull(stack.last())
}