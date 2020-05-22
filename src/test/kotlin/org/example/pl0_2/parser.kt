package org.example.pl0_2

import org.example.Lexer
import org.junit.Test
import org.junit.Assert.assertEquals

class TestParser {
    @Test
    fun testConstDecl() {
        val l = Lexer("const a = 3;")
        val p = Parser(l)
        p.parse()
        assertEquals(true, true)
    }

    @Test
    fun testVarDecl() {
        val l = Lexer("var id, id2, id3;")
        val p = Parser(l)
        p.parse()
        assertEquals(true, true)
    }

    @Test
    fun testFuncDecl() {
        val l = Lexer("fn a(e,b,c) const a = 3;;")
        val p = Parser(l)
        p.parse()
        assertEquals(true, true)
    }
}