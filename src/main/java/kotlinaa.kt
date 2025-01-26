import java.awt.Color
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.util.*

fun main(args: Array<String>) = with(BufferedReader(InputStreamReader(System.`in`))) {
//    print("함수 사용 문자열 입력: ")
//    printString(readln())
//    print("BufferedReader 문자열 입력 :")
//    var string = readLine()
//    println("name: $string")
//
//    print("숫자 입력: ")
//    printInt(readln().toInt())
//    print("BufferedReader 숫자 입력: ")
//    var number = readLine().toInt()
//    println("number : $number");
//
//    for(i in 1..number) {
//        number += i
//    }
//    println(number)
    println("StringTokenizer String 입력")
    val string = readLine()
    val st = StringTokenizer(string)

    println("newString StringTokenizer : $st")
    for (c in st) {
        println("newString = $c")
        if(c == "ab") {
            println("fuck")
        }
    }

    val a = LocalDateTime.now()
    println("a = $a")
    println(Color.PINK)
}

private fun printString(string: String) {
    println(string)
}

public fun printInt(int: Int) {
    println(int)
}
