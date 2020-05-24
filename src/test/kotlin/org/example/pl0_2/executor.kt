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
 *              | if ( condition ) { statement }
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
    fun testExecutor2() {
        val l = Lexer(
            """
function fibonacci(n) { 
    begin
        if(n == 0) {
            return 0
        };
        if (n == 1) {
            return 1 
        };
        return fibonacci(n -1) + fibonacci(n - 2); 
    end
}
var i;
begin
    i = 0;
    while(i < 10) do { 
        begin
            write fibonacci(i);
            writeln;
            i = i + 1;
        end
    }
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