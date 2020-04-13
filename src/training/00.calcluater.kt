package trainning

import java.io.File
import java.io.InputStreamReader
import java.lang.Exception

/* 電卓計算機の実装 */

/**
 * 前置きを後起きに変換する記法:  F(式, operator, 式) == F(式)F(式)operater
 * 文字を読み込む => 数字
 * 文字を読み込む => 演算子
 * 文字を読み込む => 数字
 * 文字を読み込む => 優先度の低い演算子を発見
 * F(数字 + 数字, +, (残り)
 * F(数字) = 数字
 * */
fun main() {
    /* Tokenize */
    /* Op Priority */
    /* Syntax Analyze */
    /* Pop */
    val numberRegex = Regex("[1-9][0-9]*|0")
    val spaceRegex = Regex("\\s")
    val operatorRegex = Regex("\\*|-|\\+|/")
    val n = numberRegex.matches("0")
    println(spaceRegex.matches(" "))

    val path = System.getProperty("user.dir")
    val reader: InputStreamReader = File(path).resolve("files/00.txt").reader()


    fun tokenize(reader: InputStreamReader): MutableList<Token> {
        var byte = reader.read()
        var state: String = "empty"
        val tokens = mutableListOf<Token>()
        var tokenStr: String = "empty"
        while (byte != -1) {
            val char: String = byte.toChar().toString()

            when {
                numberRegex.matches(char) -> {
                    when (state) {
                        "empty" -> {
                            state = "num"
                            tokenStr = char
                        }
                        "operator" -> {
                            state = "num"
                            tokens.add(OperatorToken(tokenStr))
                            tokenStr = char
                        }
                        "num" -> {
                            state = "num"
                            tokenStr += char
                        }
                    }
                }
                operatorRegex.matches(char) -> {
                    when (state) {
                        "empty" -> {
                            state = "operator"
                            tokenStr = char
                        }
                        "operator" -> {
                            throw Exception("operator should be successive")
                        }
                        "num" -> {
                            state = "operator"
                            tokens.add(NumberToken(tokenStr))
                            tokenStr = char
                        }
                    }

                }
                spaceRegex.matches(char) -> {
                    when (state) {
                        "empty" -> {
                            state = "empty"
                        }
                        "num" -> {
                            state = "empty"
                            tokens.add(NumberToken(tokenStr))
                            tokenStr = ""
                        }
                        "operator" -> {
                            state = "empty"
                            tokens.add(OperatorToken(tokenStr))
                            tokenStr = ""
                        }
                    }
                }
            }

            byte = reader.read()
        }
        if (state === "num") {
            tokens.add(NumberToken(tokenStr))
        }

        return tokens
    }

    val opStack: MutableList<Token> = mutableListOf()
    val reversedTokens: MutableList<Token> = mutableListOf()
    opStack.add(OperatorToken("start"))
    fun popStack(opStack: MutableList<Token>, opToken: Token, reversedTokens: MutableList<Token>) {
        val top = opStack.last()
        if (opToken.toString() == "*" || opToken.toString() == "/") {
            if (top.toString() == "*" || top.toString() == "/") {
                reversedTokens.add(opStack.removeAt(opStack.size - 1))
                popStack(opStack, opToken, reversedTokens)
            }
        }
        if (opToken.toString() == "+" || opToken.toString() == "-") {
            if (top.toString() == "*" || top.toString() == "/" || top.toString() == "+" || top.toString() == "-") {
                reversedTokens.add(opStack.removeAt(opStack.size - 1))
                popStack(opStack, opToken, reversedTokens)
            }
        }
        if (opToken.toString() == "end") {
            if (top.toString() != "start") {
                reversedTokens.add(opStack.removeAt(opStack.size - 1))
                popStack(opStack, opToken, reversedTokens)
            }
        }
    }

    val tokens = tokenize(reader)
    tokens.forEachIndexed() { i: Int, token: Token ->
        when (token.tokenType) {
            "num" -> {
                reversedTokens.add(token)
            }
            "op" -> {
                popStack(opStack, token, reversedTokens)
                opStack.add(token)
            }
        }
        if (i == tokens.size - 1) {
            popStack(opStack, OperatorToken("end"), reversedTokens)
        }
    }
    reversedTokens.forEach {
        println("${it.tokenType}: $it")
    }
    val numStack = mutableListOf<Int>()
    reversedTokens.forEach {
        if (it.tokenType == "num") {
            numStack.add(it.toString().toInt())
        }
        if (it.tokenType == "op") {
            val b = numStack.removeAt(numStack.size - 1)
            val a = numStack.removeAt(numStack.size - 1)
            var answer: Int = 0
            when (it.toString()) {
                "*" -> {
                    answer = a * b
                }
                "/" -> {
                    answer = a / b
                }
                "+" -> {
                    answer = a + b
                }
                "-" -> {
                    answer = a - b
                }
            }
            numStack.add(answer)
        }
    }
    println(numStack[0])
}

private interface Token {
    val tokenType: String
    override fun toString(): String
}

private class NumberToken(private val num: String) : Token {
    override val tokenType = "num"
    override fun toString(): String {
        return num
    }
}

private class OperatorToken(private val operator: String) : Token {
    override val tokenType = "op"
    override fun toString(): String {
        return operator
    }
}
