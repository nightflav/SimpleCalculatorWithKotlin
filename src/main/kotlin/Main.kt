import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayList

fun calculateWithVariables(input: String, vars: MutableMap<String, BigInteger>) {
    var newInput = input
    for(pair in vars) {
        newInput = newInput.replace(pair.key, pair.value.toString())
    }
    calculate(newInput)
}

fun calculate(input: String){
    var fixedInput = input.replace(" ", "")
        .replace("[+]\\s*".toRegex(), "+")
        .replace("[-]\\s*".toRegex(), "-")
        .replace("(--)+".toRegex(), "+")
        .replace("((-[+])|([+]-))".toRegex(), "-")
        .replace("[+]+".toRegex(), "+")

        .replace("*", " * ")
        .replace("/", " / ")
        .replace("(", " ( ")
        .replace(")", " ) ")
        .replace("-", " - ")
        .replace("+", " + ")
        .split("( )+".toRegex()).toMutableList()
    try {
        if(fixedInput[0] == "") fixedInput[0] = " "
        if(fixedInput.last() == "") fixedInput[fixedInput.size - 1] = " "
        fixedInput.removeAll { it == " " }
        if(fixedInput[0] == "-") {
            fixedInput.removeAt(0)
            fixedInput[0] = "-" + fixedInput[0]
        }
        finalCalculation(fixedInput.toMutableList())
    } catch (e: Exception) {
        println("Invalid message")
    }
}

fun precede(ch: Char): Int {
    return if (ch == '+' || ch == '-') {
        1
    } else if (ch == '*' || ch == '/') {
        2
    } else if (ch == '^') {
        3
    } else {
        0
    }
}

fun finalCalculation(input: MutableList<String>) {
    val operators = Stack<String>()
    operators.push("#")
    val values = mutableListOf<String>()
    for(el in input) {
        if(el.last().isDigit()) values.add(el)
        else if(el == "(") operators.push(el)
        else if(el == "^") operators.push(el)
        else if(el == ")") {
            while (operators.peek() != "#" && operators.peek() != "(") {
                values.add(operators.pop())
            }
            operators.pop()
        } else {
            if(precede(el[0]) > precede(el[0]))
                operators.push(el)
            else {
                while(operators.peek() != "#" && precede(el[0]) <= precede(operators.peek()[0])) {
                    values.add(operators.peek())
                    operators.pop()
                }
                operators.push(el)
            }
        }
    }
    while(operators.peek() != "#") {
        values.add(operators.pop())
    }

    val answer = ArrayList<String>()

    for(item in values) {
        when(item) {
            "+" -> {
                if(answer.isNotEmpty()) {
                    val a = answer.removeLast()
                    val b = answer.removeLast()
                    answer.add((b.toBigInteger() + a.toBigInteger()).toString())
                }
            }
            "-" -> {
                if(answer.isNotEmpty()) {
                    val a = answer.removeLast()
                    val b = answer.removeLast()
                    answer.add((b.toBigInteger() - a.toBigInteger()).toString())
                }
            }
            "*" -> {
                if(answer.isNotEmpty()) {
                    val a = answer.removeLast()
                    val b = answer.removeLast()
                    answer.add((b.toBigInteger() * a.toBigInteger()).toString())
                }
            }
            "/" -> {
                if(answer.isNotEmpty()) {
                    val a = answer.removeLast()
                    val b = answer.removeLast()
                    answer.add((b.toBigInteger() / a.toBigInteger()).toString())
                }
            }
            "^" -> {
                if(answer.isNotEmpty()) {
                    val a = answer.removeLast().toBigInteger()
                    val b = answer.removeLast().toBigInteger()
                    answer.add(a.pow(b.toInt()).toString())
                }
            }
            else -> answer.add(item)
        }
    }
    println(answer.firstOrNull())
}

fun variableImpl(input: String, vars: MutableMap<String, BigInteger>) {
    val name = input.substringBefore('=').trimEnd().trim()
    val value = input.substringAfter('=').trimEnd().trim()
    when  {
        input.contains('=') -> {
            if (vars.containsKey(value)) {
                if(isCorrectVariableName(value))
                    vars[name] = vars[value]!!
                else println("Invalid identifier")
            } else {
                if(isCorrectVariableName(name))
                    if(isCorrectVariableValue(value))
                        vars[name] = value.toBigInteger()
                    else println("Invalid assignment")
                else println("Invalid assignment")
            }
        }
        !input.contains('+') &&
                !input.contains('-') &&
                !input.contains('*') &&
                !input.contains('/') &&
                !input.contains('^')
        -> if (vars.containsKey(name)) {
            println(vars[name])
        } else println("Unknown variable")
        else -> {
            calculateWithVariables(input, vars)
        }
    }
}

fun isCorrectVariableName(name: String): Boolean {
    name.forEach {
        if(!it.isLetter()) return false
    }
    return true
}

fun isCorrectVariableValue(name: String): Boolean {
    if(name.isEmpty()) return false
    if(!(name[0].isDigit() || name[0] == '-')) return false
    for(el in 1 until name.length) {
        if(!name[el].isDigit()) return false
    }
    return true
}

fun main() {
    val variables = mutableMapOf<String, BigInteger>()
    while(true) {
        val input = readLine()!!
        if (input.isEmpty())
            continue
        input.trim()

        when{
            input[0] == '/' -> when(input){
                "/exit" -> {
                    println("Bye!")
                    break
                }
                "/help" -> println("Simple calculator")
                else ->  println("Unknown command")
            }
            input.contains("[a-zA-Z]".toRegex()) &&
                    input[0] != '/' -> variableImpl(input, variables)

            input[0] == '+' || input[0] == '-' ||
                    input[0] in '0'..'9' ||
                    input[0] == '('
            -> calculate(input)
            else -> println("Invalid input")
        }
    }
}