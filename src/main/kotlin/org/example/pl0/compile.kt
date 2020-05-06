package org.example.pl0

var token: Token? = null
val nameTable = mutableListOf<TableEntry>()

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
class VarEntry(name: String, val level: Int, val addr: Int) : TableEntry(IdentKind.varId, name)
class FuncEntry(name: String, val level: Int, val addr: Int, val pars: Int) : TableEntry(IdentKind.funcId, name)

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
fun block(pIndex: Int, codes: MutableList<Instruction>) {
    /* what is backP? */
    val code = Jmp(0)
    codes.add(code)
    val backP = codes.lastIndex
    /* T({constDec|varDec|funcDec}statement) */
    loop@ while (token in first("constDec|verDec|funcDec")) {
        /* T(constDec|verDec|funcDec) */
        when (checkNotNull(token)) {
            in first("constDec") -> constDec()
            in first("verDec") -> verDec()
            in first("funcDec") -> funcDec()
        }
    }
    /* T(statement) */
    statement()
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
    /* T(function ( {ident//,}? ) block ; ) */
}

/* T(statement) */
fun statement() {

}

fun first(v: Any): List<Token> {
    TODO("not implemented")
}

fun genCodeV(code: Code, index: Int) {}