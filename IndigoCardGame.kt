package indigo
enum class Rank(val symbol: String) {
    ACE("A"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SEX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K");
    override fun toString(): String {
        return symbol
    }
}
enum class Suit(val symbol: Char) {
    CLUBS('♣'),
    DIAMONDS('♦'),
    HEARTS('♥'),
    SPADES('♠');
    override fun toString(): String {
        return symbol.toString()
    }
}
data class Card(val rank: Rank, val suit: Suit) {
    override fun toString(): String {
        return "$rank$suit"
    }
}
class Deck{
    private val deck = mutableListOf<Card>()
    private val originalDeck = mutableListOf<Card>()
    init {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deck.add(Card(rank, suit))
            }
        }
        deck.shuffle()
        originalDeck.addAll(deck)
    }
    fun printDeck(){
        println(deck.joinToString(" "))
    }
    fun shuffleDeck() {
        deck.shuffle()
        println("Card deck is shuffled.")
    }
    fun resetDeck() {
        deck.clear()
        deck.addAll(originalDeck)
        println("Card deck is reset.")
    }
    fun getCards(numOfCards: Int) :MutableList<Card>{
        val temp = deck.slice(0 until numOfCards).toMutableList()
        deck.removeAll(temp)
        return temp
    }
}
fun main() {
    println("Indigo Card Game\n")
    val game = Game(Deck())
    while (true) {
        println("Play first?")
        when (readln().lowercase()) {
            "yes" -> {game.playerFirst()
                break}
            "no" -> {game.computerFirst()
                break}
            else -> continue
        }
    }
}
class Game (val deck: Deck) {
    private var tableCards = mutableListOf<Card>()
    private var playerHand = mutableListOf<Card>()
    private var playerWonCards = mutableListOf<Card>()
    private var playerScore: Int = 0
    private var computerHand = mutableListOf<Card>()
    private var computerWonCards = mutableListOf<Card>()
    private var computerScore: Int = 0
    private var playerIsLastWinner = false
    private var winningRanks: List<Rank> = listOf(Rank.ACE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING)
    private fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() } && !(toCheck.equals(""))
    }
    private fun respond(message: String): String {
        var str: String
        while (true) {
            println(message)
            str = readln()
            if (str == "exit" || (isNumeric(str) && str.toInt() in 1..playerHand.size))
                return str
        }
    }
    private fun printInitialTopCards () :List<Card>{
        val topCards = deck.getCards(4)
        println("Initial cards on the table: ${topCards.joinToString(" ")}")
        return topCards
    }
    private fun initializeGame() {
        tableCards = printInitialTopCards().toMutableList()
        playerHand = deck.getCards(6)
        computerHand = deck.getCards(6)
    }
    private fun printGameState (currentDeck: MutableList<Card>) {
        println(if (currentDeck.size == 0) "No cards on the table" else "\n${currentDeck.size} cards on the table, and the top card is ${currentDeck.last()}")
    }
    private fun printScore() {
        println("Score: Player $playerScore - Computer $computerScore")
        println("Cards: Player ${playerWonCards.size} - Computer ${computerWonCards.size}")
    }
    private fun isWinner(): Boolean {
        if (tableCards.size <= 1) return false
        if (tableCards[tableCards.lastIndex].rank == tableCards[tableCards.lastIndex - 1].rank || tableCards[tableCards.lastIndex].suit == tableCards[tableCards.lastIndex - 1].suit)
            return true
        return false
    }
    private fun checkForComputerWin(){
        if (isWinner()) {
            computerWonCards.addAll(tableCards)
            tableCards.clear()
            playerIsLastWinner = false
            computerScore = computerWonCards.filter{ winningRanks.contains(it.rank) }.size
            println("Computer wins cards")
            printScore()
        }
    }
    private fun checkForPlayerWin(){
        if (isWinner()) {
            playerWonCards.addAll(tableCards)
            tableCards.clear()
            playerIsLastWinner = true
            playerScore = playerWonCards.filter{ winningRanks.contains(it.rank) }.size
            println("Player wins cards")
            printScore()
        }
    }
    private fun getCandidateCards(): List<Card> {
        return computerHand.filter{it.rank == tableCards[tableCards.lastIndex].rank || it.suit == tableCards[tableCards.lastIndex].suit}
    }
    private fun getLeastValuableCard(): Card {
        val duplicateSuits = computerHand.filter{card -> computerHand.count{it.suit == card.suit} > 1}
        if(duplicateSuits.isNotEmpty())
            return duplicateSuits.random()
        val duplicateRanks = computerHand.filter{card -> computerHand.count{it.rank == card.rank} > 1}
        if(duplicateRanks.isNotEmpty())
            return duplicateRanks.random()
        return computerHand.random()
    }
    private fun getPlayedCard(): Card {
        if (tableCards.size == 0) return getLeastValuableCard()
        if (computerHand.size == 1) return computerHand[0]
        val candidateCards = getCandidateCards()
        if (candidateCards.size == 0 && tableCards.isNotEmpty()) return getLeastValuableCard()
        if (candidateCards.size == 1) return candidateCards.first()
        if (candidateCards.filter{it.suit == tableCards.last().suit}.size > 1)
            return candidateCards.filter{it.suit == tableCards.last().suit}.random()
        if (candidateCards.filter{it.rank == tableCards.last().rank}.size > 1)
            return candidateCards.filter{it.rank == tableCards.last().rank}.random()
        return candidateCards.random()
    }
    private fun playComputerTurn() {
        println(computerHand.joinToString(" "))
        val playedCard = getPlayedCard()
        println("Computer plays $playedCard")
        tableCards.add(playedCard)
        computerHand.remove(playedCard)
        checkForComputerWin()
        printGameState(tableCards)
    }
    private fun playPlayerTurn(index: Int) {
        tableCards.add(playerHand[index])
        playerHand.removeAt(index)
        checkForPlayerWin()
        printGameState(tableCards)
    }
    private fun endGame() {
        if(tableCards.size != 0 && playerIsLastWinner) {
            playerWonCards.addAll(tableCards)
            tableCards.clear()
            playerScore = playerWonCards.filter{ winningRanks.contains(it.rank) }.size
        } else if (tableCards.size != 0 && !playerIsLastWinner) {
            computerWonCards.addAll(tableCards)
            tableCards.clear()
            computerScore = computerWonCards.filter{ winningRanks.contains(it.rank) }.size
        }
        if (playerWonCards.size >= computerWonCards.size)
            playerScore += 3
        else
            computerScore += 3
        printScore()
    }
    fun playerFirst() {
        initializeGame()
        playerIsLastWinner = true
        printGameState(tableCards)
        println("Cards in hand: ${playerHand.joinToString(separator = " ") { (playerHand.indexOf(it) + 1).toString() + ")" + it.toString() }}")
        var inputPrompt = respond("Choose a card to play (1-${playerHand.size}):")
        while (true) {
            if (inputPrompt == "exit") {
                break
            } else if (inputPrompt.toInt() in 1..playerHand.size) {
                try {
                    playPlayerTurn(inputPrompt.toInt() - 1)
                    playComputerTurn()
                    if (playerHand.size == 0)
                        playerHand.addAll(deck.getCards(6))
                    if (computerHand.size == 0)
                        computerHand.addAll(deck.getCards(6))
                    println("Cards in hand: ${playerHand.joinToString(separator = " ") { (playerHand.indexOf(it) + 1).toString() + ")" + it.toString() }}")
                    inputPrompt = respond("Choose a card to play (1-${playerHand.size}):")
                } catch (e: Exception) {
                    endGame()
                    break
                }
            }
        }
        println("Game Over")
    }
    fun computerFirst () {
        initializeGame()
        playerIsLastWinner = false
        printGameState(tableCards)
        playComputerTurn()
        println("Cards in hand: ${playerHand.joinToString(separator = " ") {(playerHand.indexOf(it) + 1).toString()+ ")" + it.toString()}}")
        var inputPrompt = respond("Choose a card to play (1-${playerHand.size}):")
        while (true) {
            if (inputPrompt == "exit") {
                break
            } else if (inputPrompt.toInt() in 1..playerHand.size) {
                try {
                    playPlayerTurn(inputPrompt.toInt() - 1)
                    if (playerHand.size == 0)
                        playerHand.addAll(deck.getCards(6))
                    if (computerHand.size == 0)
                        computerHand.addAll(deck.getCards(6))
                    playComputerTurn()
                    println("Cards in hand: ${playerHand.joinToString(separator = " ") { (playerHand.indexOf(it) + 1).toString() + ")" + it.toString() }}")
                    inputPrompt = respond("Choose a card to play (1-${playerHand.size}):")
                } catch (e: Exception) {
                    endGame()
                    break
                }
            }
        }
        println("Game Over")
    }
}
