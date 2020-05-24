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
        Executor(p.codes).execute()
    }

}