package com.example.data

/**
 * ExplorerForum / community Q&A from the repair-links pack.
 * Owner anecdotes only. Never override Owner Guide or CHARM numbers.
 */
 data class CommunityForumThread(
    val title: String,
    val url: String,
    val keywords: List<String>
)

object CommunityForumThreads {
    const val BOARD_2001_2005 =
        "https://www.explorerforum.com/forums/forums/2001-2005-explorer-sport-trac.120/"
    const val DISCLAIMER =
        "Community thread only. Not factory spec. Do not mix 2007-2010 Sport Trac advice onto this 2004 4WD VIN K truck."

    val catalog: List<CommunityForumThread> = listOf(
        CommunityForumThread(
            "2001-2005 Explorer Sport Trac board",
            BOARD_2001_2005,
            listOf("forum", "board", "community", "explorerforum")
        ),
        CommunityForumThread(
            "Blend door actuators",
            "https://www.explorerforum.com/forums/threads/how-many-blend-door-actuators-are-there.499399/page-2",
            listOf("blend door", "actuator", "hvac door", "temp door")
        ),
        CommunityForumThread(
            "5R55E valve body / shift kit",
            "https://www.explorerforum.com/forums/threads/5r55e-valve-body-replacement-or-shift-kit.137976/",
            listOf("valve body", "shift kit", "5r55e", "flare", "2-3")
        ),
        CommunityForumThread(
            "4.0 SOHC timing chain rattle",
            "https://www.explorerforum.com/forums/threads/4-0-sohc-timing-chain-rattle-resolution-thread.201407/",
            listOf("timing chain", "rattle", "tensioner", "cassette", "sohc")
        ),
        CommunityForumThread(
            "JustAnswer 2004 Sport brake Q&A",
            "https://www.justanswer.com/ford/1413h-2004-ford-explorer-sport-i-d-repair-brakes.html",
            listOf("justanswer", "brake question")
        )
    )

    fun matching(query: String): List<CommunityForumThread> {
        val haystack = query.lowercase()
        return catalog.filter { thread ->
            thread.keywords.any { keyword -> haystack.contains(keyword) }
        }.distinctBy { it.url }
    }

    fun format(threads: List<CommunityForumThread>, limit: Int = 3): String {
        if (threads.isEmpty()) {
            return "No packed community thread matches that request. Board: $BOARD_2001_2005. $DISCLAIMER"
        }
        return threads.take(limit).joinToString(" ") { thread ->
            "${thread.title}: ${thread.url}."
        } + " $DISCLAIMER"
    }
}
