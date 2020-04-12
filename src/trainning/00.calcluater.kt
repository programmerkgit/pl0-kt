package trainning

import java.io.File

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
    val expressionRegex = Regex("[1-9][0-9]*|0")
    val spaceRegex = Regex("¥s")
    val operatorRegex = Regex("x|¥-|¥+|/")
    val n = expressionRegex.matches("0")
    println(spaceRegex.matches(" "))

    val path = System.getProperty("user.dir")
    val reader = File(path).resolve("files/00.txt").reader()
    fun doSomething(f: (char: Char) -> Unit) {
        var byte = reader.read()
        while (byte != -1) {
            val char = byte.toChar()
            f(char)
            byte = reader.read()
        }
    }
}