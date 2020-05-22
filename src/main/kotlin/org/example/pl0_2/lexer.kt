package org.example.pl0_2

val keywords = mapOf(
    "begin" to KeyId.Begin,
    "end" to KeyId.End,
    "if" to KeyId.If,
    "while" to KeyId.While,
    "do" to KeyId.Do,
    "then" to KeyId.Then,
    "const" to KeyId.Const,
    "var" to KeyId.Var,
    "func" to KeyId.Func,
    "return" to KeyId.Ret,
    ":=" to KeyId.Assign,
    "odd" to KeyId.Odd,
    "write" to KeyId.Write,
    "writeln" to KeyId.WriteLn,
    "+" to KeyId.Plus,
    "*" to KeyId.Mult,
    "-" to KeyId.Minus,
    "/" to KeyId.Div,
    "(" to KeyId.Lparen,
    ")" to KeyId.Rparen,
    "=" to KeyId.Equal,
    "<>" to KeyId.NotEq,
    "<" to KeyId.Lss,
    "<=" to KeyId.LssEq,
    ">" to KeyId.Gtr,
    ">=" to KeyId.GtrEq,
    "." to KeyId.Period,
    "," to KeyId.Comma,
    ";" to KeyId.Semicolon,
)

enum class KeyId {
    Begin, End,                /*　予約語の名前　*/
    If, Then,
    While, Do,
    Ret, Func,
    Var, Const, Odd,
    Write, WriteLn,
    end_of_KeyWd,                /*　予約語の名前はここまで　*/
    Plus, Minus,                /*　演算子と区切り記号の名前　*/
    Mult, Div,
    Lparen, Rparen,
    Equal, Lss, Gtr,
    NotEq, LssEq, GtrEq,
    Comma, Period, Semicolon,
    Assign,
    end_of_KeySym,                /*　演算子と区切り記号の名前はここまで　*/
    Identifier, Num, nul,                /*　トークンの種類　*/
    end_of_Token,
    letter, digit, colon, others,        /*　上記以外の文字の種類　*/
    Null
}

abstract class Token(val kind: KeyId)
open class KeywordToken(val keyId: KeyId) : Token(keyId)
class ConstToken() : KeywordToken(KeyId.Const)
class VarToken() : KeywordToken(KeyId.Var)
class FuncToken() : KeywordToken(KeyId.Func)

class NumToken(val value: Int) : Token(KeyId.Num)
open class IdentifierToken(val id: String) : Token(KeyId.Identifier)

object CharClassMap {
    private val map = mutableMapOf<Char, KeyId>()

    init {
        ('a'..'z').forEach {
            map[it] = KeyId.letter
        }
        ('A'..'Z').forEach {
            map[it] = KeyId.letter
        }
        ('0'..'9').forEach {
            map[it] = KeyId.digit
        }
        map['+'] = KeyId.Plus
        map['-'] = KeyId.Minus
        map['*'] = KeyId.Mult
        map['/'] = KeyId.Div
        map['('] = KeyId.Lparen
        map[')'] = KeyId.Rparen
        map['='] = KeyId.Equal
        map['<'] = KeyId.Lss
        map['>'] = KeyId.Gtr
        map[','] = KeyId.Comma
        map[';'] = KeyId.Semicolon
        map[':'] = KeyId.colon
    }

    operator fun get(key: Char): KeyId {
        return map.getOrDefault(key, KeyId.others)
    }
}

/*
* 　文字の種類を示す表にする　
static org.example.pl0_2.KeyId charClassT[256];
static void initCharClassT(org.example.pl0_2.KeyId *charClassT)        /*　文字の種類を示す表を作る関数　*/
{
    int i;
    for (i = 0; i < 256; i++)
        charClassT[i] = others;
    for (i = '0'; i <= '9'; i++)
        charClassT[i] = digit;
    for (i = 'A'; i <= 'Z'; i++)
        charClassT[i] = letter;
    for (i = 'a'; i <= 'z'; i++)
        charClassT[i] = letter;
    charClassT['+'] = Plus;
    charClassT['-'] = Minus;
    charClassT['*'] = Mult;
    charClassT['/'] = org.example.pl0_2.Div;
    charClassT['('] = Lparen;
    charClassT[')'] = Rparen;
    charClassT['='] = Equal;
    charClassT['<'] = Lss;
    charClassT['>'] = Gtr;
    charClassT[','] = Comma;
    charClassT['.'] = Period;
    charClassT[';'] = Semicolon;
    charClassT[':'] = colon;
}

* */

var ch: Char? = null
fun nextChar(): Char {
    TODO("implement after")
}

fun nextToken(): Token {
    var spaceCount = 0
    var cr = 0
    var i = 0
    loop@ while (true) {
        when (ch) {
            ' ' -> {
                spaceCount++
            }
            '\t' -> {
                spaceCount += 5
            }
            '\n' -> {
                spaceCount = 0
                cr += 1
            }
            else -> {
                break@loop
            }
        }
        ch = nextChar()
    }
    when (val cc = CharClassMap[checkNotNull(ch)]) {
        KeyId.letter -> {
        }
        KeyId.digit -> {
            var num = 0
            do {
                num = num * 10 + (ch!! - '0')
                i++
                ch = nextChar()
            } while (CharClassMap[ch!!] == KeyId.digit)
            return NumToken(num)
        }
        KeyId.colon -> {
        }
        KeyId.Lss -> {
            ch = nextChar()
            return when (ch) {
                '=' -> {
                    ch = nextChar()
                    KeywordToken(KeyId.LssEq)
                }
                '>' -> {
                    ch = nextChar()
                    KeywordToken(KeyId.NotEq)
                }
                else -> KeywordToken(KeyId.Lss)
            }
        }
        KeyId.Gtr -> {
        }
        else -> {
        }
    }

    TODO("not implement yet")
}