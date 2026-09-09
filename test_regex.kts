fun extractNamesRegex(text: String): List<String> {
    val regex = Regex("""\*\*(.*?)\*\*""")
    val matches = regex.findAll(text)
    val extracted = matches.map { it.groupValues[1].trim() }.toList()
    
    return extracted.filter { name -> 
        val words = name.split(" ")
        words.size in 2..3 && words.all { it.isNotEmpty() && it.first().isUpperCase() }
    }
}
println(extractNamesRegex("The project titled **\"PCB Design for Battery Management System\"** by student **Aarav Bansal** from the **ELECTRONICS & COMMUNICATION** department was focused on battery technology but has been **discontinued**."))
