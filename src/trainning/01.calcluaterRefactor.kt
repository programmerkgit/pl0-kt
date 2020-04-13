package trainning2

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

}

fun tokenize() {
    val tokens: ArrayList<Token>
    while (true) {
        /* 文字を読み、空白を削除 */
        /* 空白でない場合はTokenize開始 */
        /* Regex一覧からregexを順番に当てて、マッチした一覧を保持 */
        /* マッチしたうち、最長のRegexを選択 */
        /* Tokenizeして文字を読み進める */
    }
    return tokens
}

private fun convertToReverse(tokens: ArrayList<Token>) {
    val resultStack: MutableList<Token> = mutableListOf()
    /* 最初の処理と最終処理を行う */
    /*　tokensの最初と最後にstartとend op を設置　*/
    val opStack: MutableList<Token> = mutableListOf(OperatorToken("start"))
    resultStack.add(OperatorToken("end"))
    /* tokenが数字の場合 => stackする */
    /* tokenがoperatorの場合 */
    /* pop作業の開始 */
    /* popが終わったらopStackに読み込んだトークンをstackする */

}

private fun pop() {
    /* 読み込んだOpとStackOpを比較 */
    /* if opStackのTopが優先順位が高い場合 */
    /* stackから演算子をresultStackに載せる */
    /* pop() */
    /* else opStackのTopが優先順位が低い場合 */
    /* popしない */
}

private val numberRegex: Regex = Regex("[1-9][0-9]*]")

/* Tokenize */
/* 優先順位の低い演算子が出てきたらpopする */
/* Tokenを演算子の優先順位に */


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
