object ImmutableSetExample {
    def main(args: Array[String]) {
        val numbers = Set(2, 5, 3)
        val newNumbers = numbers + 8
        println(newNumbers)
        println(numbers)
    }
}
