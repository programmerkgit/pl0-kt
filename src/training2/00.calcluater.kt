package training2

import java.io.File
import java.util.regex.Pattern

/* 電卓計算機の実装 */

/**
 *
 * */
fun main() {
    val tokens = tokenize()
    val reversed = reverseTokens(tokens)
    val stack: MutableList<Number> = mutableListOf()
    reversed.forEach {
        val value = it.matchResult?.value!!
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

const val EOF = (-1).toChar()


private val tokenList: List<TokenMatcher> = listOf(
    TokenMatcher(Regex("[1-9][0-9]*"), "number"),
    TokenMatcher(Regex("[/*+-]"), "operator")
)

private fun tokenize(): MutableList<TokenMatcher> {
    println("tokenize start")
    val path = System.getProperty("user.dir")
    var text = File(path).resolve("files/00.txt").reader().readText()
    val resultTokens: MutableList<TokenMatcher> = mutableListOf()

    var i = 0;
    while (i < text.length) {
        /* 文字を読み込みカット */
        val char = text[i]
        /* 文字を読み空白を削除 */
        if (char == ' ') {
            i++
            continue
        }
        /* 空白でもEOFでもない場合はTokenize開始 */
        /* Regex一覧からregexを順番に当てて、マッチした一覧を保持 */
        val matchedMatcher = tokenList.filter {
            val match = it.parse(text, i)
            match != null
        }
        /* マッチがなかったらエラー */
        if (matchedMatcher.isEmpty()) {
            throw Error("unexpected char $char at index $i")
        }
        /* マッチしたうち、最長のRegexを選択 */
        val result = matchedMatcher.maxBy { it.matchResult!!.value.length }
        val value: String = result?.matchResult!!.value
        i += value.length
        /* Tokenizeして文字を読み進める */
        val tokenCopy = TokenMatcher(result.pattern, result.tokenType)
        tokenCopy.parse(value, 0)
        resultTokens.add(tokenCopy)
        println("Value: ${tokenCopy.matchResult?.value} TokenType: ${tokenCopy.tokenType}")
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

private fun reverseTokens(tokens: List<TokenMatcher>): List<TokenMatcher> {
    val resultStack: MutableList<TokenMatcher> = mutableListOf()
    /* 最初の処理と最終処理を行う */
    val opStack: MutableList<TokenMatcher> = mutableListOf()
    val inputTokens: MutableList<TokenMatcher> = tokens.toMutableList()
    fun pop(readOp: TokenMatcher) {
        val topOpPriority = OpPriorities.getValue(opStack.last().matchResult!!.value)
        val readOpPriority = OpPriorities.getValue(readOp.matchResult!!.value)
        /* 読み込んだOpとStackOpを比較 */
        if (readOpPriority <= topOpPriority) {
            /* if opStackのTopが優先順位が高い場合,もしくは同一の場合(左結合) */
            /* opStackから演算子をresultStackに載せる */
            val top = opStack.removeAt(opStack.size - 1)
            resultStack.add(top)
            /* pop()継続 */
            pop(readOp)
        }
        /* else opStackのTopが優先順位が低い場合 */
        /* popしない */
    }
    /*　tokensの最初と最後にstartとend op を設置　*/
    /* TODO: 無理やりパースしたことにするより、元テキストをいじった方が直感的 */
    val endMatcher: TokenMatcher = TokenMatcher(Regex("\\$"), "operator")
    val startMatcher: TokenMatcher = TokenMatcher(Regex("\\^"), "operator")
    endMatcher.parse("$", 0)
    startMatcher.parse("^", 0)
    inputTokens.add(endMatcher)
    opStack.add(startMatcher)
    inputTokens.forEach {
        val value: String = it.matchResult!!.value
        when (it.tokenType) {
            /* tokenがoperatorの場合 */
            /* pop作業の開始 */
            /* popが終わったらopStackに読み込んだトークンをstackする */
            "operator" -> {
                pop(it)
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

private open class TokenMatcher(val pattern: Regex, val tokenType: String) {

    fun parse(text: String, i: Int): MatchResult? {
        val matchResult = pattern.find(text, i)
        val first = matchResult?.range?.first
        if (first == i) {
            this.matchResult = matchResult
            return matchResult
        } else {
            return null
        }
    }

    var matchResult: MatchResult? = null

}