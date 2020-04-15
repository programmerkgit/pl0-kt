package varTable

interface VarTableRow {
    var name: String
    var type: String
    var pointer: Number
}

fun main() {

    /* 変数表は順番を持つ */
    val table: MutableList<VarTableRow> = mutableListOf()
    /* p17. テストケースを利用してシミュレートしたい */
    table.add(object : VarTableRow {
        override var name = "abc"
        override var type = "real"
        override var pointer: Number = 100
    })


}