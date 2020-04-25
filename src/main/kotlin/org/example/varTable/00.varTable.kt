package org.example.varTable

interface VarTableRow {
    var name: String
    var type: String
    var pointer: Number
}

fun main() {

    /* 変数表は順番を持つ */
    val table: MutableList<VarTableRow> = mutableListOf()
    /* p17. テストケースを利用してシミュレートしたい */
    /* 変数名に対してIndexしたいので、tableはクラスでラップしたい。 */
    table.add(object : VarTableRow {
        override var name = "abc"
        override var type = "real"
        override var pointer: Number = 100
    })


}