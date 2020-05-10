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
    jpc
}

private fun <T> MutableList<T>.pop(): T {
    return removeAt(lastIndex)
}

abstract class Instruction(val code: Code)
abstract class Instruction1(opCode: Code, val level: Int, val addr: Int) : Instruction(opCode)
class Lod(level: Int, addr: Int) : Instruction1(Code.lod, level, addr)
class Sto(level: Int, addr: Int) : Instruction1(Code.sto, level, addr)
class Cal(level: Int, addr: Int) : Instruction1(Code.cal, level, addr)
class Ret(level: Int, addr: Int) : Instruction1(Code.ret, level, addr)
abstract class Instruction2(opCode: Code, var value: Int) : Instruction(opCode)
class Lit(value: Int) : Instruction2(Code.lit, value)
class Ict(value: Int) : Instruction2(Code.ict, value)
class Jmp(value: Int = 0) : Instruction2(Code.jmp, value)
class Jpc(value: Int) : Instruction2(Code.jpc, value)
abstract class Instruction3(opCode: Code) : Instruction(opCode)
class Neg : Instruction3(Code.opr)
class Eq : Instruction3(Code.opr)
class Neq : Instruction3(Code.opr)
class Gr : Instruction3(Code.opr)
class Ls : Instruction3(Code.opr)
class Greq : Instruction3(Code.opr)
class Lseq : Instruction3(Code.opr)
class Add : Instruction3(Code.opr)
class Sub : Instruction3(Code.opr)
class Mul : Instruction3(Code.opr)
class Div : Instruction3(Code.opr)
class Odd : Instruction3(Code.opr)
class Wrt : Instruction3(Code.opr)
class Wrl : Instruction3(Code.opr)

/* mutable ?  */
fun execute(instructions: MutableList<Instruction>) {
    var pc = 0;
    var stack = mutableListOf<Int>()
    stack.add(0)
    stack.add(0)
    /* top不要? */
    val display = mutableListOf(0)
    do {
        /* breakとcontinueの差は?? */
        when (val inst = instructions[pc++]) {
            is Lit -> stack.add(inst.value)
            is Lod -> stack.add(stack[display[inst.level] + inst.addr])
            is Sto -> stack[display[inst.level] + inst.addr] = stack.pop()
            is Cal -> {
                val level = inst.level + 1
                /* get or null? */
                stack.add(display[level])
                stack.add(pc)
                display[level] = stack.size - 2
                pc = inst.addr
            }
            is Ret -> {
                val ret = stack.pop()
                val top = display[inst.level]
                display[inst.level] = stack[top]
                pc = stack[top + 1]
                /* displayの手前に引数分の変数領域がある */
                stack = stack.slice(0 until stack.size - inst.addr).toMutableList()
                stack.add(ret)
            }
            is Ict -> {/* Is increment necessary */
            }
            is Jmp -> pc = inst.value
            is Jpc -> {
                if (stack.pop() == 0) {
                    pc = inst.value
                }
            }
            is Neg -> stack.add(-stack.pop())
            is Add -> stack.add(stack.pop() + stack.pop())
            is Sub -> stack.add(-stack.pop() + stack.pop())
            is Mul -> stack.add(stack.pop() * stack.pop())
            is Div -> stack.add(stack.pop() * stack.pop())
            is Odd -> stack.add(stack.pop() and 1)
            is Eq -> stack.add(if (stack.pop() == stack.pop()) 1 else 0)
            is Neq -> stack.add(if (stack.pop() != stack.pop()) 1 else 0)
            is Ls -> stack.add(if (stack.pop() > stack.pop()) 1 else 0)
            is Gr -> stack.add(if (stack.pop() < stack.pop()) 1 else 0)
            is Lseq -> stack.add(if (stack.pop() >= stack.pop()) 1 else 0)
            is Greq -> stack.add(if (stack.pop() <= stack.pop()) 1 else 0)
            is Wrt -> print(stack.pop())
            is Wrl -> println()
            else -> error("unexpected instruction")
        }
    } while (pc != 0)
}