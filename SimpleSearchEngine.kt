import java.io.File
fun main(args: Array<String>) {
    if (args.contains("--data")) {
        val linesList = File(args.last()).readLines().toMutableList()
        while (true) {
            displayMenu()
            when (readln().toInt()) {
                0 -> break
                1 -> findPerson(linesList)
                2 -> printAll(linesList)
                else -> println("Incorrect option! Try again.")
            }
            println()
        }
        println("Bye!")
    } else {
        println("Add the name of the text file preceded by --data")
    }
}
fun findPerson(linesList : MutableList<String>){
    val peopleMap = mutableMapOf<String, MutableList<Int>>()
    println("Select a matching strategy: ALL, ANY, NONE")
    val strat = readln()
    println("Enter data to search people:")
    val searchQuery = readln().lowercase()
    for(str in linesList){
        for (word in str.split(" ")) {
            if(peopleMap.containsKey(word.lowercase())){
                peopleMap[word.lowercase()]?.add(linesList.indexOf(str))
            } else {
                peopleMap.put(word.lowercase(), mutableListOf(linesList.indexOf(str)))
            }
        }
    }
    when (strat) {
        "ALL" -> findAll(linesList, peopleMap, searchQuery)
        "ANY" -> findAny(linesList, peopleMap, searchQuery)
        "NONE" -> findNone(linesList, peopleMap, searchQuery)
    }

}
fun findAny(linesList: MutableList<String>, peopleMap: MutableMap<String, MutableList<Int>>, query: String) {
    val queryList = query.split(" ")
    var matchingIndeces = mutableListOf<Int>()
    for (word in queryList) {
        matchingIndeces.addAll(peopleMap.get(word)!!)
    }
    matchingIndeces.addAll(peopleMap.get(queryList[0])!!)
    for(i in 1..queryList.size-1) {
        matchingIndeces = (matchingIndeces union  peopleMap.get(queryList[i])!!).toMutableList()
    }
    if (!matchingIndeces.isEmpty()) {
        println(if(matchingIndeces.size == 1)
            "1 person found :"
        else
            "${matchingIndeces.size} persons found :")
        for(i in matchingIndeces) {
            println(linesList[i])
        }
    } else {
        println("No matching people found.")
    }
}
fun findAll(linesList: MutableList<String>, peopleMap: MutableMap<String, MutableList<Int>>, query: String) {
    val queryList = query.split(" ")
    var matchingIndeces = mutableListOf<Int>()
    try {
        matchingIndeces.addAll(peopleMap.get(queryList[0])!!)
        for (i in 1..queryList.size - 1) {
            matchingIndeces = (matchingIndeces intersect peopleMap.get(queryList[i])!!).toMutableList()
        }
        if (!matchingIndeces.isEmpty()) {
            println(
                if (matchingIndeces.size == 1)
                    "1 person found :"
                else
                    "${matchingIndeces.size} persons found :"
            )
            for (i in matchingIndeces) {
                println(linesList[i])
            }
        } else {
            println("No matching people found.")
        }
    } catch (e: Exception) {
        println("No matching people found.")
    }
}
fun findNone(linesList: MutableList<String>, peopleMap: MutableMap<String, MutableList<Int>>, query: String) {
    val queryList = query.split(" ")
    var matchingIndeces = mutableListOf<Int>()
    for (word in queryList) {
        matchingIndeces.addAll(peopleMap.get(word)!!)
    }
    matchingIndeces.addAll(peopleMap.get(queryList[0])!!)
    for(i in 1..queryList.size-1) {
        matchingIndeces = (matchingIndeces union  peopleMap.get(queryList[i])!!).toMutableList()
    }
    if (matchingIndeces.size != linesList.size) {
        println(if(matchingIndeces.size == linesList.size - 1)
            "1 person found :"
        else
            "${linesList.size - matchingIndeces.size} persons found :")
        for(i in 0..linesList.size - 1) {
            if(matchingIndeces.contains(i))
                continue
            else
                println(linesList[i])
        }
    } else {
        println("No matching people found.")
    }
}
fun printAll(linesList : MutableList<String>){
    println("=== List of People ===")
    for(str in linesList)
        println(str)
}
fun displayMenu() {
    println("=== Menu ===")
    println("1. Find a person")
    println("2. Print all people")
    println("0. Exit")
}
