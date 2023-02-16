import java.lang.Exception

var board = MutableList(6){MutableList(7){" "} }
var columnsCounter = MutableList(7) {5}
fun main() {
    println("Connect Four")
    //taking player's names
    println("First player's name:")
    val player1 = readln().trim()
    println("Second player's name:")
    val player2 = readln().trim()
    //default board dimensions
    var rows = 6
    var columns = 7
    //taking board dimensions
    while (true) {
        println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
        var dims = readln().trim().replace(" |\\\t".toRegex(),"")
        if (dims.contains("x|X".toRegex())) {
            val dimsList = dims.split("x|X".toRegex())
            if(isNumeric(dimsList[0]) && isNumeric(dimsList[1]) && !( dimsList[0].equals("") || dimsList[1].equals(""))) {
                rows = dimsList[0].toInt()
                columns = dimsList[1].toInt()
                if (rows in 5..9 && columns in 5..9) {
                    board = MutableList(rows) { MutableList(columns) { " " } }
                    columnsCounter = MutableList(columns) {rows - 1}
                    break
                } else if (!(rows in 5..9))
                    println("Board rows should be from 5 to 9")
                else if (!(columns in 5..9))
                    println("Board columns should be from 5 to 9")
            } else {
                println("Invalid input")
                continue
            }
        } else if (dims.equals("")) {
            break
        } else {
            println("Invalid input")
            continue
        }
    }
    var numOfGames = 0
    while (true) {
        println("""Do you want to play single or multiple games?
            |For a single game, input 1 or press Enter
            |Input a number of games:""".trimMargin())
        val input = readln()
        if((isNumeric(input) && input.toInt() > 0)|| input == ""){
            if(input == "")
                numOfGames = 1
            else
                numOfGames =input.toInt()
            break
        } else {
            println("Invalid input")
        }
    }
    //printing game information
    println("$player1 VS $player2")
    println("$rows X $columns board")
    println(if(numOfGames == 1) "Single game" else "Total $numOfGames games")
    var userInput = ""
    var player1Turn = true
    var player1Starting = true
    val multiGameFlag = numOfGames != 1
    var player1Score = 0
    var player2Score = 0
    outerLoop@ for(i in 1..numOfGames) {
        if(multiGameFlag) println("Game #$i")
        printBoard()
        while (true) {
            println("${if(player1Turn) player1 else player2}'s turn:")
            userInput = readln()
            if (isNumeric(userInput)) {
                if (player1Turn && userInput.toInt() in 1..columns && !columnIsFull(userInput.toInt() - 1)) {
                    addMove(userInput.toInt(), "o")
                    player1Turn = false
                    printBoard()
                } else if (!player1Turn && userInput.toInt() in 1..columns && !columnIsFull(userInput.toInt() - 1)) {
                    addMove(userInput.toInt(), "*")
                    player1Turn = true
                    printBoard()
                } else if (!(userInput.toInt() in 1..columns)) {
                    println("The column number is out of range (1 - $columns)")
                } else {
                    println("Column $userInput is full")
                }
                if (isWin()) {
                    println("Player ${if(player1Turn) player2 else player1} won")
                    if(player1Turn) player2Score += 2 else player1Score += 2
                    break
                }
                if (isDraw()) {
                    println("It is a draw")
                    player1Score++
                    player2Score++
                    break
                }
            } else if (userInput.equals("end")) {
                break@outerLoop
            } else {
                println("Incorrect column number")
            }
        }
        println("""Score
            |$player1: $player1Score $player2: $player2Score
        """.trimMargin())
        //reversing the starting player after each game
        player1Turn = !player1Starting
        player1Starting = !player1Starting
        board = MutableList(rows) { MutableList(columns) { " " } } //clearing board
        columnsCounter = MutableList(columns) {rows - 1}
    }
    println("Game over!")
}
fun isNumeric(toCheck: String): Boolean {
    return toCheck.all { char -> char.isDigit() } && !(toCheck.equals(""))
}
fun printBoard() {
    println(" ${(1.. board[0].size).toList().joinToString(" ")}")
    for (i in 0 until board.size) {
        for (j in 0 until board[0].size) {
            print("║${board[i][j]}")
        }
        println("║")
    }

    print("╚═")
    repeat (board[0].size - 1) {
        print("╩═")
    }
    println("╝")
}
fun addMove(column: Int, player: String) {
    board[columnsCounter[column - 1]][column - 1] = player
    columnsCounter[column - 1]--
}
fun columnIsFull(column: Int): Boolean{
    return columnsCounter[column] == -1
}
fun isWin(): Boolean{
    val rows = board.size
    val columns = board[0].size
    //checking for horizontal win
    for (i in rows - 1 downTo 0) {
        for (j in 0 until columns - 3) {
            if(board[i][j] == board[i][j+1] && board[i][j+1] == board[i][j+2] && board[i][j+2] == board[i][j+3] && board[i][j] != " ")
                return true
        }
    }
    //checking for vertical win
    for (i in rows - 1 downTo  3) {
        for (j in 0 until columns) {
            if(board[i][j] == board[i - 1][j] && board[i - 1][j] == board[i - 2][j] && board[i - 2][j] == board[i - 3][j] && board[i][j] != " ")
                return true
        }
    }
    //checking for diagonal(leftBottom-rightTop) win
    for (i in rows - 1 downTo  3) {
        for (j in 0 until columns - 3) {
            if(board[i][j] == board[i - 1][j + 1] && board[i - 1][j + 1] == board[i - 2][j + 2] && board[i - 2][j + 2] == board[i - 3][j + 3] && board[i][j] != " ")
                return true
        }
    }
    //checking for reverse diagonal (rightBottom-leftTop) win
    for (i in rows - 1 downTo  3) {
        for (j in columns - 1 downTo 3) {
            if(board[i][j] == board[i - 1][j - 1] && board[i - 1][j - 1] == board[i - 2][j - 2] && board[i - 2][j - 2] == board[i - 3][j - 3] && board[i][j] != " ")
                return true
        }
    }
    return false
}
fun isDraw(): Boolean{
    var flag = true
    for(i in columnsCounter)
        if(i != -1)
            flag = false
    return flag
}
