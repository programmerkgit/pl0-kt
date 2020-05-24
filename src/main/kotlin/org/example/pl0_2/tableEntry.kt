package org.example.pl0_2

enum class VarKind {
    ParId,
    VarId,
    ConstId,
    FuncId,
}

abstract class TableEntry(
    var kind: VarKind,
    var name: String
) {
}

/*
* ret
* temp = stack[--top]
* top = display[i.level]
* // stackのサイズの操作を変えた方が良い
* stack
* -2 arg1
* -1 arg0
* 0 display
* 1 return pc
*
*
* display[i.level] = stack[top]
* pc = stack[top + 1]
* top -= i.addr
* stack.add(temp)
* */

/* wat is func entry */
/* level: 新しいStackのlevelを使う */
/* stack[top] = display[level] 前のlevelのstackの開始場所の保存 */
/* stack[top + 1] = pc */
/* stack */
/*  */
class FuncEntry(
    name: String,
    val level: Int,
    var rAddr: Int,
    var parCount: Int
) : TableEntry(VarKind.FuncId, name)

/* How Par Entry Is used */
class ParEntry(
    name: String,
    var level: Int,
    var parAddr: Int = 0
) : TableEntry(VarKind.ParId, name)

class VarEntry(
    name: String,
    var level: Int,
    var addr: Int
) : TableEntry(VarKind.VarId, name)

class ConstEntry(
    name: String,
    var value: Int
) : TableEntry(VarKind.ConstId, name)
