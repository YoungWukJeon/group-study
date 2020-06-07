object FunctionExample {
    def main(args: Array[String]): Unit = {
        firstClass()
    }

    def firstClass(): Unit = {
        def isJavaMentioned(tweet: String): Boolean = tweet.contains("Java") // Predicate
        def isShortTweet(tweet: String): Boolean = tweet.length() < 20 // Predicate

        val tweets = List(
            "I love the new features in Java 8",
            "How's it going",
            "An SQL query walks into a bar, sees two tables and say 'Can I join you?'"
        )
        tweets.filter(isJavaMentioned).foreach(println)
        tweets.filter(isShortTweet).foreach(println)
    }
}
