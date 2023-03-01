package tasklist
import kotlinx.datetime.*
import java.lang.Exception
import com.squareup.moshi.*
import java.io.File

val inputCommands = listOf<String>("add", "print", "edit", "delete", "end")
val priorityTags = listOf<String>("C", "H", "N", "L")
val fields = listOf<String>("priority", "date", "time", "task")
enum class COLORS(val ansi: String) {
    RED("\u001B[101m \u001B[0m"),
    GREEN("\u001B[102m \u001B[0m"),
    YELLOW("\u001B[103m \u001B[0m"),
    BLUE("\u001B[104m \u001B[0m");
}
data class Task(val priority: String, val date: String, val time: String, val dueTag: String, val value: String)
class TaskList (list: MutableList<Task>){
    private val taskList: MutableList<Task>
    init{
        taskList = list
    }
    fun getList() = taskList
    fun add(task: Task) {
        if(task.value == "") return
        taskList.add(task)
    }
    fun delete(index: Int) {
        if(index == -1)
            return
        taskList.removeAt(index)
    }
    fun edit(args: List<String>) {
        if(args[0].toInt() == -1)
            return
        val(tempPrio, tempDate, tempTime, tempTag, tempVal) = taskList[args[0].toInt()]
        val temp:Task
        when (args[1]) {
            "priority" -> temp = Task(args[2], tempDate, tempTime, tempTag, tempVal)
            "time" -> temp = Task(tempPrio, tempDate, args[2], tempTag, tempVal)
            "task" -> temp = Task(tempPrio, tempDate, tempTime, tempTag, args[2])
            "date" -> temp = Task(tempPrio, args[2], tempTime, Display().computeDueTag(args[2]), tempVal)
            else -> temp = Task("","","","","")
        }
        taskList.removeAt(args[0].toInt())
        taskList.add(args[0].toInt(), temp)
    }
    fun print() {
        val row = "| %-3s| %-11s| %-6s| %s | %s |%-44s|\n"
        val separator = row.format(" ", " ", " ", " ", " ", " ").replace(" ", "-").replace("|", "+")
        val header = row.format("N", "   Date", "Time", "P", "D",  " ".repeat(19) + "Task")
        if (taskList.isEmpty())
            println("No tasks have been input")
        else{
            print("$separator$header$separator")
            for (i in 0..taskList.lastIndex) {
                val listOfLines = taskList[i].value.split("\n")
                val listOfChunks = mutableListOf<String>()
                for(line in listOfLines) {
                    listOfChunks.addAll(line.chunked(44))
                }
                for (line in listOfChunks) {
                    if(listOfChunks.indexOf(line) == 0)
                        print(row.format("${i+1}", taskList[i].date, taskList[i].time, Display().getPriorityColor(taskList[i].priority), Display().getDueTagColor(taskList[i].dueTag),line))
                    else
                        print(row.format(" "," "," "," "," ", line))
                }
                print(separator)
            }
        }
    }
    fun isEmpty() = taskList.isEmpty()
    fun size() = taskList.size
}
class Display{
    private fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() } && !(toCheck.equals(""))
    }
    private fun takePriorityInput(): String{
        var priority ="."
        while(!priorityTags.contains(priority)) {
            println("Input the task priority (C, H, N, L):")
            priority = readln().uppercase()
        }
        return priority
    }
    private fun takeDateInput(): String{
        while(true) {
            try {
                println("Input the date (yyyy-mm-dd):")
                val dateTokens = readln().split("-")
                val testDate = LocalDate(dateTokens[0].toInt(), dateTokens[1].toInt(), dateTokens[2].toInt())
                return listOf(dateTokens[0], dateTokens[1].padStart(2, '0'), dateTokens[2].padStart(2, '0')).joinToString("-")
            } catch(e: Exception) {
                println("The input date is invalid")
                continue
            }
        }
    }
    private fun takeTimeInput(): String{
        while(true) {
            try {
                println("Input the time (hh:mm):")
                val timeTokens = readln().split(":")
                val testDate = LocalDateTime(2023, 3, 1, timeTokens[0].toInt(), timeTokens[1].toInt())
                return timeTokens[0].padStart(2, '0')+":"+timeTokens[1].padStart(2,'0')
            } catch(e: Exception) {
                println("The input time is invalid")
                continue
            }
        }
    }
    fun computeDueTag(date: String): String {
        val dateTokens = date.split("-")
        val taskDate = LocalDate(dateTokens[0].toInt(), dateTokens[1].toInt(), dateTokens[2].toInt())
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        return if (currentDate.daysUntil(taskDate) == 0) "T" else if (currentDate.daysUntil(taskDate) > 0) "I" else "O"
    }
    fun getPriorityColor(priority: String): String {
        return when (priority) {
            "C" -> COLORS.RED.ansi
            "H" -> COLORS.YELLOW.ansi
            "N" -> COLORS.GREEN.ansi
            "L" -> COLORS.BLUE.ansi
            else -> ""
        }
    }
    fun getDueTagColor(dueTag: String): String {
        return when (dueTag) {
            "I" -> COLORS.GREEN.ansi
            "T" -> COLORS.YELLOW.ansi
            "O" -> COLORS.RED.ansi
            else -> ""
        }
    }
    private fun takeValueInput(): String{
        var str ="first"
        println("Input a new task (enter a blank line to end):")
        val linesList = mutableListOf<String>()
        while (str != "") {
            str = readln().trim()
            if (str != "")
                linesList.add(str)
        }
        if (linesList.isEmpty()) {
            println("The task is blank")
            return ""
        }
        return linesList.joinToString("\n")
    }
    private fun takeIndex(list: TaskList): Int{
        var index: String
        while (true) {
            println("Input the task number (1-${list.size()}):")
            index = readln()
            if(isNumeric(index) && index.toInt() in (1..(list.size())))
                return index.toInt() - 1
            else
                println("Invalid task number")
        }
    }
    private fun takeField(): String{
        var field: String
        while (true) {
            println("Input a field to edit (priority, date, time, task):")
            field = readln()
            if(fields.contains(field))
                return field
            else
                println("Invalid field")
        }
    }
    private fun takeNewValue(field: String): String{
        when (field) {
            "priority" -> return takePriorityInput()
            "date" -> return takeDateInput()
            "time" -> return takeTimeInput()
            "task" -> return takeValueInput()
        }
        return ""
    }
    fun takeActionInput(): String{
        var str =""
        while (true) {
            println("Input an action (add, print, edit, delete, end):")
            str = readln().lowercase()
            if (!inputCommands.contains(str))
                println("The input action is invalid")
            else
                return str
        }
    }
    fun addTaskInput(): Task{
        val priority = takePriorityInput()
        val date = takeDateInput()
        return Task(priority, date, takeTimeInput(),  computeDueTag(date), takeValueInput())
    }
    fun editTaskInput(taskList: TaskList): List<String> {
        taskList.print()
        if(taskList.isEmpty())
            return listOf("-1", "", "")
        val index = takeIndex(taskList)
        val field = takeField()
        val newValue = takeNewValue(field)
        println("The task is changed")
        return listOf(index.toString(), field, newValue)
    }
    fun deleteTaskInput(taskList: TaskList): Int {
        taskList.print()
        if(taskList.isEmpty())
            return -1
        val index = takeIndex(taskList)
        println("The task is deleted")
        return index
    }
}
fun main() {
    val display = Display()
    var inputCmd = display.takeActionInput()
    val list = TaskList(checkForJsonListAndInitiate())
    while (true) {
        when (inputCmd) {
            "add" -> list.add(display.addTaskInput())
            "print" -> list.print()
            "edit" -> list.edit(display.editTaskInput(list))
            "delete" -> list.delete(display.deleteTaskInput(list))
            "end" -> break
        }
        inputCmd = display.takeActionInput()
    }
    println("Tasklist exiting!")
    saveAndTerminate(list.getList())
}
fun checkForJsonListAndInitiate(): MutableList<Task>{
    if(File("tasklist.json").exists()) {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
        val taskListAdapter: JsonAdapter<MutableList<Task>> = moshi.adapter(type)
        val jsonFile = File("tasklist.json")
        return taskListAdapter.fromJson(jsonFile.readText())!!
    } else
        return mutableListOf<Task>()
}
fun saveAndTerminate (list: MutableList<Task>) {
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
    val taskListAdapter: JsonAdapter<MutableList<Task>> = moshi.adapter(type)
    val jsonFile = File("tasklist.json")
    jsonFile.writeText(taskListAdapter.toJson(list))
}
