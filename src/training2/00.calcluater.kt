package training2

import java.io.File

/* 電卓計算機の実装 */

/**
 *
 * */
fun main() {
    val tokens = tokenize()
    val reversed = reverseTokens(tokens)
    val stack: MutableList<Number> = mutableListOf()
    reversed.forEach {
        val value = it.value
        val tokenType: String = it.tokenType
        if (tokenType === "number") {
            stack.add(value.toFloat())
        }
        if (tokenType === "operator") {
            val b = stack.removeAt(stack.lastIndex).toFloat()
            val a = stack.removeAt(stack.lastIndex).toFloat()
            when (value) {
                "+" -> {
                    stack.add(a + b)
                }
                "-" -> {
                    stack.add(a - b)
                }
                "*" -> {
                    stack.add(a * b)
                }
                "/" -> {
                    stack.add(a / b)
                }
            }
        }
    }
    println(stack[0])
}

private val tokenMatchers: List<TokenMatcher> = listOf(
    TokenMatcher(Regex("\\$"), "operator"),
    TokenMatcher(Regex("\\^"), "operator"),
    TokenMatcher(Regex("([1-9][0-9]*(\\.[0-9]+)?)|0(\\.[0-9]+)?"), "number"),
    TokenMatcher(Regex("[/*+-]"), "operator")
)

private fun tokenize(): MutableList<Token> {
    println("tokenize start")
    val path = System.getProperty("user.dir")
    var text = File(path).resolve("files/00.txt").reader().readText()
    /* 開始と終了をトークン化するためのダミー文字 */
    text = "^$text$"
    val resultTokens: MutableList<Token> = mutableListOf()

    var i = 0
    while (i < text.length) {
        /* 1文字目を読み込み */
        val char = text[i]
        /* 文字を読み空白を削除 */
        if (char == ' ') {
            i++
            continue
        }
        /* 空白でもEOFでもない場合はTokenize開始 */
        /* Regex一覧からregexを順番に当てて、マッチした一覧を保持 */
        val matchedTokens: List<Token> = tokenMatchers.mapNotNull {
            it.parse(text, i)
        }
        /* マッチがなかったらエラー */
        if (matchedTokens.isEmpty()) {
            throw Error("unexpected char $char at index $i")
        }
        /* マッチしたうち、最長のRegexを選択 */
        val result = matchedTokens.maxBy { it.value.length }!!
        i += result.value.length
        /* Tokenizeして文字を読み進める */
        resultTokens.add(result)
        println("Value: ${result.value} TokenType: ${result.tokenType}")
    }
    println("EOF")
    return resultTokens
}

private val OpPriorities = mapOf(
    "^" to 0,
    "$" to 1,
    "+" to 2, "-" to 2,
    "*" to 3, "/" to 3
)

private fun reverseTokens(tokens: List<Token>): List<Token> {
    val resultStack: MutableList<Token> = mutableListOf()
    /* 最初の処理と最終処理を行う */
    val opStack: MutableList<Token> = mutableListOf()
    val inputTokens: MutableList<Token> = tokens.toMutableList()

    /* 演算子のPOPを定義 */
    fun pop(readOp: Token) {
        if (opStack.size == 0) return
        val topOpPriority = OpPriorities.getValue(opStack.last().value)
        val readOpPriority = OpPriorities.getValue(readOp.value)
        /* 読み込んだOpとStackOpを比較 */
        if (readOpPriority <= topOpPriority) {
            /* if opStackのTopが優先順位が高い場合,もしくは同一の場合(左結合なので同一だと先が優先度高い) */
            val top = opStack.removeAt(opStack.size - 1)
            /* opStackから演算子をresultStackに載せる */
            resultStack.add(top)
            /* pop()継続 */
            pop(readOp)
        }
        /* else opStackのTopが優先順位が低い場合 */
        /* popしない */
    }
    inputTokens.forEach {
        when (it.tokenType) {
            /* tokenがoperatorの場合 */
            "operator" -> {
                /* pop作業の開始 */
                pop(it)
                /* popが終わったらopStackに読み込んだトークンをstackする */
                opStack.add(it)
            }
            /* tokenが数字の場合 => stackする */
            "number" -> {
                resultStack.add(it)
            }
        }
    }
    return resultStack
}

class Token(val pattern: Regex, val tokenType: String, val value: String) {
    constructor(pattern: String, tokenType: String, value: String) : this(Regex(pattern), tokenType, value) {
    }
}

private open class TokenMatcher(val pattern: Regex, val tokenType: String) {
    fun parse(text: String, i: Int): Token? {
        val matchResult = pattern.find(text, i)
        val first = matchResult?.range?.first
        return if (first == i) {
            return Token(pattern, tokenType, matchResult.value)
        } else {
            null
        }
    }
}