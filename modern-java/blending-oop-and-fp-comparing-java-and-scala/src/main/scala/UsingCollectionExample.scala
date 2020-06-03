object UsingCollectionExample {
    def main(args: Array[String]) {
        val fileLines = List(
            "I have a pen",
            "I have an apple",
            "Pen pineapple apple pen"
        )
        val linesLongUpper = fileLines.filter(l => l.length() > 15)
                .map(l => l.toUpperCase())
//        val linesLongUpper = fileLines filter (_.length() > 15) map (_.toUpperCase())
        println(linesLongUpper)
    }
}
