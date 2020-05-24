package org.example.pl0_2

import org.junit.Test

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

class TestExecutor {
    @Test
    fun testExecuter1() {
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
        Executor(p.codes).execute()
    }

    @Test
    fun testExecuter2() {
        val l = Lexer(
            """
function mul(x, y) { 
    function add(a, b) {
        begin
            return a + b;
        end
    } 
    begin
        return add(1, 2) * x * y;
    end
}
begin
    write mul(3,2);
end 
        """.trimIndent()
        )
        val p = Parser(l)
        p.parse()
        println()
        p.codes.forEachIndexed { i, it ->
            println("$i: $it")
        }
        Executor(p.codes).execute()
        println()
    }

}