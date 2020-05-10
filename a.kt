class A(val a: Int = 3) {
    override fun equals(other: Any?): Boolean {
        if (other is A) {
            return a == (other.a + 2)
        } else {
            return false
        }
    }
}

fun main() {
    val set = mutableListOf(A(2), A(4))
    println(set.contains(A(5)))
}
