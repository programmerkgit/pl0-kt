package org.example.pl0_2

/**
 * blockBegin
 *
 * */


/*
typedef enum codes{			*/
/*　命令語のコード　*//*

    lit, opr, lod, sto, cal, ret, ict, jmp, jpc
}OpCode;
*/

enum class Code {
    lit,
    opr,
    lod,
    sto,
    cal,
    ret,
    ict,
    jmp,
    jpc,
    neg,
    eq,
    noteq,
    grt,
    lss,
    grteq,
    lsseq,
    add,
    sub,
    mul,
    div,
    odd,
    wrt,
    wrl
}

private fun <T> MutableList<T>.pop(): T {
    return removeAt(lastIndex)
}

abstract class Instruction(val code: Code)
abstract class Instruction1(opCode: Code, val level: Int, val addr: Int) : Instruction(opCode) {
    override fun toString(): String {
        return "code: $code level: $level addr:$addr"
    }
}

class Lod(level: Int, addr: Int) : Instruction1(Code.lod, level, addr)
class Sto(level: Int, addr: Int) : Instruction1(Code.sto, level, addr)

/* Call level 0, addr func*/
/**
 * @param addr 相対アドレス
 *
 */
class Cal(level: Int, addr: Int) : Instruction1(Code.cal, level, addr)
class Ret(level: Int, pars: Int) : Instruction1(Code.ret, level, pars)
abstract class Instruction2(opCode: Code, var value: Int) : Instruction(opCode) {
    override fun toString(): String {
        return "code: $code value: $value"
    }
}

class Lit(value: Int) : Instruction2(Code.lit, value)
class Ict(value: Int) : Instruction2(Code.ict, value)
class Jmp(value: Int = 0) : Instruction2(Code.jmp, value)
class Jpc(value: Int = 0) : Instruction2(Code.jpc, value)
abstract class Instruction3(opCode: Code) : Instruction(opCode) {
    override fun toString(): String {
        return "code: $code"
    }
}

class Neg : Instruction3(Code.neg)
class Eq : Instruction3(Code.eq)
class NotEq : Instruction3(Code.noteq)
class Grt : Instruction3(Code.grt)
class Lss : Instruction3(Code.lss)
class GrtEq : Instruction3(Code.grteq)
class LssEq : Instruction3(Code.lsseq)
class Add : Instruction3(Code.add)
class Sub : Instruction3(Code.sub)
class Mul : Instruction3(Code.mul)
class Div : Instruction3(Code.div)
class Odd : Instruction3(Code.odd)
class Wrt : Instruction3(Code.wrt)
class Wrl : Instruction3(Code.wrl)

/* mutable ?  */
class Executor(val instructions: MutableList<Instruction>) {
    var pc = 0;
    var stack = mutableListOf<Int>()
    val display = mutableMapOf<Int, Int>(0 to 0)
    fun execute() {
        /* top不要? */
        do {
            /* breakとcontinueの差は?? */
            when (val inst = instructions[pc++]) {
                is Lit -> stack.add(inst.value)
                is Lod -> stack.add(stack[display[inst.level]!! + inst.addr])
                is Sto -> stack[display[inst.level]!! + inst.addr] = stack.pop()
                is Cal -> {
                    val level = inst.level + 1
                    /* get or null? */
                    stack.add(display.getOrElse(level) { 0 })
                    display[level] = stack.lastIndex
                    stack.add(pc)
                    pc = inst.addr
                }
                is Ret -> {
                    val ret = stack.pop()
                    val top = display[inst.level]!!
                    display[inst.level] = stack[top]
                    pc = stack[top + 1]
                    /* displayの手前に引数分の変数領域がある */
                    stack = stack.slice(0 until top).toMutableList()
                    repeat(inst.addr) {
                        stack.removeAt(stack.lastIndex)
                    }
                    stack.add(ret)
                }
                is Ict -> {/* Is increment necessary */
                    repeat(inst.value) {
                        stack.add(0)
                    }
                }
                is Jmp -> pc = inst.value
                is Jpc -> {
                    if (stack.pop() == 0) {
                        pc = inst.value
                    }
                }
                is Neg -> stack.add(-stack.pop())
                is Add -> stack.add(stack.pop() + stack.pop())
                is Sub -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.add(a - b)
                }
                is Mul -> stack.add(stack.pop() * stack.pop())
                is Div -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.add(a / b)
                }
                is Odd -> stack.add(stack.pop() and 1)
                is Eq -> stack.add(if (stack.pop() == stack.pop()) 1 else 0)
                is NotEq -> stack.add(if (stack.pop() != stack.pop()) 1 else 0)
                is Lss -> stack.add(if (stack.pop() > stack.pop()) 1 else 0)
                is Grt -> stack.add(if (stack.pop() < stack.pop()) 1 else 0)
                is LssEq -> stack.add(if (stack.pop() >= stack.pop()) 1 else 0)
                is GrtEq -> stack.add(if (stack.pop() <= stack.pop()) 1 else 0)
                is Wrt -> print(stack.pop())
                is Wrl -> println()
                else -> error("unexpected instruction")
            }
        } while (pc != 0)
    }
}