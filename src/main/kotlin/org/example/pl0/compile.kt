package org.example.pl0

import org.example.LL1Parser.leftPar
import kotlin.math.acos
import kotlin.math.tan

var token: Token? = null
val codes: MutableList<Instruction> = mutableListOf()
val nameTable = mutableListOf<TableEntry>()
var level = 0
var localAddress = 0
val index = mutableMapOf<Int, Int>()
val addr = mutableMapOf<Int, Int>()
var tIndex: Int = 0

enum class IdentKind {
    varId,
    constId,
    funcId,
    parId,
}

/* Tokneが保持する情報を確認 */
abstract class TableEntry(
    val kind: IdentKind, val name: String
)

class ValEntry(name: String, val value: Int) : TableEntry(IdentKind.constId, name)
class VarEntry(name: String, val level: Int, var addr: Int) : TableEntry(IdentKind.varId, name)
class ParEntry(name: String, val level: Int, var addr: Int) : TableEntry(IdentKind.parId, name)
class ConstEntry(name: String, val value: Int) : TableEntry(IdentKind.constId, name)
class FuncEntry(name: String, val level: Int = 0, var addr: Int = 0, var pars: Int = 0) :
    TableEntry(IdentKind.funcId, name)

fun tIdentifier(id: KeyId) {
    if (checkNotNull(token).kind == id) {
        token = nextToken()
    } else {
        error("Expected identifier is $id")
    }
}

/**
 * block {constDec|varDec|funcDec} statement
 * */

/* What is pIndex? */
fun block(pIndex: Int) {
    /* what is backP? */
    val jmp = Jmp()
    codes.add(jmp)
    /* T({constDec|varDec|funcDec}statement) */
    loop@ while (token in first("constDec|verDec|funcDec")) {
        /* T(constDec|verDec|funcDec) */
        when (checkNotNull(token)) {
            in first("constDec") -> constDec()
            in first("verDec") -> verDec()
            in first("funcDec") -> funcDec()
        }
    }
    /* 関数の本体 */
    jmp.value = codes.size
    (nameTable[pIndex] as FuncEntry).addr = codes.size
    /* frame size */
    codes.add(Ict(1000))
    /* T(statement) */
    statement()
    codes.add(Ret(level, (nameTable[index[level - 1]!!] as FuncEntry).pars))

}

fun blockEnd() {
    level--
    tIndex = index[level]!!
    localAddress = addr[level]!!
}

/* T(constDec -> const {ident = number//,} ;) */
fun constDec() {
    /* T(const) */
    tIdentifier(KeyId.Const)
    /* T({ident = number//,})*/
    while (true) {
        /* T(ident = number) */
        if (checkNotNull(token).kind == KeyId.Identifier) {
            val tmp = checkNotNull(token)
            token = nextToken()
        } else {
            error("Expected identifier is ${KeyId.Identifier}")
        }
        val tmp = tIdentifier(KeyId.Identifier)
        tIdentifier(KeyId.Equal)
        if (checkNotNull(token).kind == KeyId.Num) {
            val tmp = checkNotNull(token)
            token = nextToken()
        } else {
            error("Expected identifier is ${KeyId.Num}")
        }
        if (token !in first(",")) break
        tIdentifier(KeyId.colon)
    }
    /* T(;) */
    tIdentifier(KeyId.Semicolon)
}

/* T(verDec) */
fun verDec() {
    /* T(var {ident//,} ; ) */
    tIdentifier(KeyId.Var)
    while (true) {
        tIdentifier(KeyId.Identifier)
        if (token !in first(",")) break
        tIdentifier(KeyId.colon)
    }
    tIdentifier(KeyId.Semicolon)
}

/* T(funcDec) */
fun funcDec() {
    var fIndex = 0
    tIdentifier(KeyId.Func)
    if (token!! is IdentifierToken) {
        val entry = FuncEntry((token as IdentifierToken).id, 0, codes.size, 0)
        fIndex = nameTable.lastIndex
        nameTable.add(entry)
        tTerminal(KeyId.Lparen)
        blockBegin(2)
        while (true) {
            tTerminal(KeyId.Identifier) {
                nameTable.add(FuncEntry((token as IdentifierToken).id, level))
                entry.pars++
            }
            if (token?.kind != KeyId.Comma) break
            tTerminal(KeyId.Comma)
        }
    }
    tTerminal(KeyId.Rparen) {
        endPar(fIndex)
    }
    block(fIndex)
    /* T(function ( {ident//,}? ) block ; ) */
}

fun endPar(funIndex: Int) {
    val pars = (nameTable[funIndex] as FuncEntry).pars
    if (pars == 0) return
    (1 until pars + 1).forEach { i ->
        /* ?? */
        (nameTable[funIndex + i] as ParEntry).addr = i - 1 - pars
    }
}

fun blockBegin(firstAddress: Int) {
    index[level] = nameTable.lastIndex
    addr[level] = localAddress
    localAddress = firstAddress
    level++
    return
}

/* T(statement) */
fun statement() {
    /*T({(indext | begein.. | if | while | return | write | writeln) }) */
    /*T((indext | begein.. | if | while | return | write | writeln) ) */
    when (token) {
        in first("ident := expression") -> {
            val tableIndex = nameTable.indexOfLast { it.name == (token as IdentifierToken).id }
            if (token is IdentifierToken) {
                if (nameTable[tableIndex] !is ValEntry && nameTable[tableIndex] !is ParEntry) {
                    /* errorType() */
                }
                token = nextToken()
            } else {
                error("not Identifier")
            }
            if (token!!.kind == KeyId.Assign) {
                token = nextToken()
            } else {
                error("assign")
            }
            expression()
            codes.add(Sto(0, (nameTable[tableIndex] as ParEntry).addr))
        }
        in first("begin") -> {
            tIdentifier(KeyId.Begin)
            loop@ while (true) {
                statement()
                /* semicolonを忘れたことにする処理を描きたい  */
                if (token?.kind !in first(KeyId.Semicolon)) break
                tIdentifier(KeyId.Semicolon)
            }
            tIdentifier(KeyId.End)
        }
        in first("if") -> {
            tIdentifier(KeyId.If)
            condition()
            val jmp = Jpc(0)
            codes.add(Jpc(0))
            tIdentifier(KeyId.Then)
            statement()
            jmp.value = codes.size
        }
        in first("while") -> {
            tIdentifier(KeyId.While)
            /* condition評価からループ */
            val start = codes.size
            condition()
            /* endへ飛ぶ。バックパッチ */
            val jmpEnd = Jpc(0)
            tIdentifier(KeyId.Do)
            codes.add(jmpEnd)
            statement()
            /* 必ずスタートに飛ぶ */
            codes.add(Jmp(start))
            /* 一番最後のCodesへバックパッチ  */
            jmpEnd.value = codes.size
        }
        in first("Write") -> {
            tIdentifier(KeyId.Write)
            expression()
            codes.add(Wrt())
        }
        in first("Writeln") -> {
            tIdentifier(KeyId.WriteLn)
            codes.add(Wrl())
        }
        in first("return") -> {
            tIdentifier(KeyId.Ret)
            expression()
            /* 現在のレベルを取得, パラメーター数を取得 */
            codes.add(Ret(0, 1))
        }
    }
}

fun condition() {
    when (token) {
        in first("odd") -> {
            tIdentifier(KeyId.Odd)
            expression()
            codes.add(Odd())
        }
        in first("expression") -> {
            expression()
            val tmp = token
            when (token) {
                in first("=") -> {
                    tIdentifier(KeyId.Equal)
                }
                in first("<>") -> {
                    tIdentifier(KeyId.NotEq)
                }
                in first("<") -> {
                    tIdentifier(KeyId.Lss)
                }
                in first(">") -> {
                    tIdentifier(KeyId.Gtr)
                }
                in first("<=") -> {
                    tIdentifier(KeyId.LssEq)
                }
                in first(">=") -> {
                    tIdentifier(KeyId.GtrEq)
                }
            }
            expression()
            when (tmp) {
                in first("=") -> codes.add(Eq())
                in first("<>") -> codes.add(Neq())
                in first("<") -> codes.add(Ls())
                in first(">") -> codes.add(Gr())
                in first("<=") -> codes.add(Lseq())
                in first(">=") -> codes.add(Greq())
            }
        }
    }
}

fun expression() {
    // T("+|-|ε"))
    val temp = token!!
    when (token) {
        in first("+") -> {
            if (token?.kind == KeyId.Plus) {
                token = nextToken()
            } else {
                error("")
            }
        }
        in first('-') -> {
            if (token?.kind == KeyId.Minus) {

            } else {
                error("")
            }
        }
        else -> {

        }
    }
    term()
    if (temp.kind == KeyId.Minus) {
        codes.add(Sub())
    }
    T("{(+|-)term}")
    while (true) {
        while (token !in first("(+|-)"))
            break
        val temp = token!!
        when (token) {
            in first("+") -> {
                if (token?.kind == KeyId.Plus) {
                    token = nextToken()
                } else {
                    error("error")
                }
            }
            in first("-") -> {
                if (token?.kind == KeyId.Minus) {
                    token = nextToken()
                } else {
                    error("error")
                }
            }
        }
        term()
        codes.add(if (token?.kind == KeyId.Minus) Sub() else Add())
    }
}

fun term() {
    factor()
    val temp = token
    while (token in first("*|/")) {
        when (token) {
            in first("*") -> {
                if (token?.kind == KeyId.Mult) {
                    token = nextToken()
                } else {

                }
            }
            in first('/') -> {
                if (token?.kind == KeyId.Div) {
                    token = nextToken()
                } else {

                }
            }
        }
        factor()
        codes.add(if (token?.kind == KeyId.Mult) Mul() else Div())
    }
}

fun factor() {
    T("ident|number|(ident ()) | (expression)")
    when (token) {
        in first("ident") -> {
            if (token is IdentifierToken) {
                val tIndex = nameTable.indexOfLast { it.name == (token as IdentifierToken).id }
                val tableEntry = nameTable[tIndex]
                when (tableEntry) {
                    is VarEntry -> {
                        codes.add(Lod(0, tIndex))
                        token = nextToken()
                    }
                    is ParEntry -> {
                        codes.add(Lod(0, tIndex))
                        token = nextToken()
                    }
                    is ConstEntry -> {
                        codes.add(Lit(tableEntry.value))
                        token = nextToken()
                    }
                    is FuncEntry -> {
                        /* ident ( expression, ) */
                        tTerminal(KeyId.Identifier)
                        /* T(() */
                        tTerminal(KeyId.Lparen)
                        /* T({expression,} ) */
                        while (token in first("expression")) {
                            while (true) {
                                expression()
                                if (token?.kind != KeyId.Comma) break
                                tTerminal(KeyId.Comma)
                            }
                        }
                        tTerminal(KeyId.Rparen)
                        codes.add(Cal(0, tableEntry.addr))
                    }
                }
            }
        }
        in first("number") -> {
            if (token is NumToken) {
                tTerminal(KeyId.Num) {
                    codes.add(Lit((token as NumToken).value))
                }
            }
        }
        in first("(") -> {
            tTerminal(KeyId.Lparen)
            expression()
            tTerminal(KeyId.Rparen)

        }
    }
}

fun first(v: Any): List<Token> {
    TODO("not implemented")
}

fun genCodeV(code: Code, index: Int) {}

fun tTerminal(keyId: KeyId, action: (() -> Unit)? = null) {
    if (token?.kind == keyId) {
        if (action != null) {
            action()
        }
        token = nextToken()
    } else {
        error("")
    }
}