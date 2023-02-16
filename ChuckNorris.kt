fun main() {
    while (true) {
        println("Please input operation (encode/decode/exit):")
        val op = readln()
        when (op) {
            "encode" -> encode()
            "decode" -> decode()
            "exit" -> break
            else ->println("There is no '$op' operation")
        }
        println()
    }
    println("Bye!")
}
fun decode() {
    println("Input encoded string:")
    val inputLine = readln()
    val encodedBlocks = inputLine.split(" ")
    var binaryLine = ""
    var flag = false
    for(i in 1..encodedBlocks.size - 1 step 2) {
        if(encodedBlocks[i - 1] != "0" && encodedBlocks[i - 1] != "00")
            flag = true
        binaryLine += "".padStart(encodedBlocks[i].length, if(encodedBlocks[i - 1] == "0") '1' else '0')
    }
    if(encodedBlocks.size % 2 != 0 || binaryLine.length % 7 != 0 || !(inputLine.contains("^[0 ]+\$".toRegex())) || flag){
        println("Encoded string is not valid.")
        return
    }
    val binAscii = binaryLine.chunked(7)
    println("Decoded string:")
    for(str in binAscii) {
        print(str.toInt(2).toChar())
    }
}
fun encode() {
    println("Input string:")
    val inputLine = readln()
    var binaryLine = ""
    for (char in inputLine.toCharArray()) {
        binaryLine += Integer.toBinaryString(char.toInt()).padStart(7, '0')
    }

    println("Encoded string:")
    println(chuckNorris(binaryLine))
}
fun chuckNorris(binaryLine: String):String {
    var chuckLine = ""
    var blockKey = ' '
    for(ch in binaryLine) {
        if(ch != blockKey) {
            chuckLine += if(ch == '1') " 0 0" else " 00 0"
            blockKey = ch
        } else {
            chuckLine += "0"
        }
    }
    return chuckLine.trim()
}
