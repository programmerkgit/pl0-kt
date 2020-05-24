package org.example.pl0_2

fun main() {
    val l = Lexer(
        """
function a(x, y) {
    begin
        return x * y;
    end
}
begin
    write a(2, 3);  
end
        """.trimIndent()
    )
    val p = Parser(l)
    p.parse()
    Executor(p.codes).execute()
}