package org.example.pl0_2

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * program := block .
 * block := [constDecl|varDecl|funcDecl] statement
 * constDecl := const identifier = number{, identifier = number } ;
 * varDecl := var identifier{, identifier};
 * funcDecl := function ident ([ident{, ident}]) {block};
 * statement := identifier = expression
 *              | begin statement{; statement} end
 *              | if ( condition ) then { statement }
 *              | while (condition) do { statement }
 *              | return expression
 *              | write expression
 *              | writeln
 * condition := expression =|<>|<|>|<=|>= expression
 * expression := [+|-] term {+ term}
 * term := factor {(*|/) factor }
 * factor := ident
 *          | number
 *          | ident ([expression{, expression}])
 *          | ( expression )
 *
 * */

class TestParser {
    @Test
    fun testProgram1() {
        val l = Lexer(
            """
var x, y;
begin
    x = 3;
    y = 5;
    write x + y; 
end.

        """.trimIndent()
        )
        val p = Parser(l)
        p.parse()
        p.codes.forEachIndexed { i, v ->
            println("i: $i $v")
        }
    }
    @Test
    fun testProgram2() {
        val l = Lexer(
            """
function a(x, y) {
    begin
        return x * y;
    end
}
begin
    write a(2, 3); 
    writeln; 
end

        """.trimIndent()
        )
        val p = Parser(l)
        p.parse()
        p.codes.forEachIndexed { i, v ->
            println("i: $i $v")
        }
        println("start")
        Executor(p.codes).execute()
        println("done")
    }

    @Test
    fun testFactor() {
        val l = Lexer(
            """
function a(x, y) {
    begin
        write x;
        write y;
    end
}
begin
    write a(2,3)
end
                
        """.trimIndent()
        )
        val p = Parser(l)
        p.parse()
        p.codes.forEachIndexed { i, v ->
            println("i: $i $v")
        }
    }

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
        val l = Lexer("function a(e,b,c) {const a = 3}")
        val p = Parser(l)
        p.parse()
        assertEquals(true, true)
    }

    @Test
    fun testProgram() {
        val input = """
var a, b;            
fn a()
   const a = 3;
begin
 while b > 0 do
 begin
 end;
 if(1 = true) then 
    begin r := r-w
    end
 writeln(a);
end;
"""
        val l = Lexer(input)
        val p = Parser(l)
        p.parse()
        // assertEquals(true, true)
    }
}