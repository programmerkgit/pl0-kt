package org.example.pl0_2

import org.example.*
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame

class TestLexer {
    @Test
    fun testConstDecl() {
        val l = Lexer(
            """
const a = 3;            
function a(b,c,d) var e,f,g;;
""".trimIndent()
        )
        val tokens = listOf(
            ConstToken(),
            IdentifierToken("a"),
            AssignToken(),
            IntToken("3"),
            SemicolonToken(),
            FuncToken(),
            IdentifierToken("a"),
            LParenToken(),
            IdentifierToken("b"),
            CommaToken(),
            IdentifierToken("c"),
            CommaToken(),
            IdentifierToken("d"),
            RParentToken(),
            VarToken(),
            IdentifierToken("e"),
            CommaToken(),
            IdentifierToken("f"),
            CommaToken(),
            IdentifierToken("g"),
            SemicolonToken(),
            SemicolonToken()
        )
        tokens.forEach {
            assertEquals(l.nextToken(), it)
        }
    }
}