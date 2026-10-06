package com.riverlines.game.data.sample

import com.riverlines.game.domain.model.EcoChallenge

object SampleData {

    val riverNames = listOf(
        "SPRING CREEK",
        "MOSS HOLLOW",
        "HERON BEND",
        "REED MARSH",
        "STONE FORD",
        "CLEARWATER RUN",
        "WETLAND RUN",
        "BLUE LAKE DELTA"
    )

    val ecoNotes = listOf(
        "WETLANDS FILTERED 180 LITRES",
        "THE HERON FOUND CLEAR WATER",
        "REED BEDS HELD THE BANKS",
        "NO DROP WAS LOST TODAY"
    )

    val ecoChallenges = listOf(
        EcoChallenge(
            id = "no_leaks",
            title = "NO LEAKS",
            description = "FINISH A RIVER WITHOUT A DEAD END",
            reward = 60,
            completed = true
        ),
        EcoChallenge(
            id = "marsh_friend",
            title = "MARSH FRIEND",
            description = "PASS THREE WETLANDS IN ONE RUN",
            reward = 80,
            completed = true
        ),
        EcoChallenge(
            id = "quick_current",
            title = "QUICK CURRENT",
            description = "FINISH WITH SIX TURNS LEFT",
            reward = 120,
            completed = false
        ),
        EcoChallenge(
            id = "stone_skipper",
            title = "STONE SKIPPER",
            description = "SOLVE A BOARD WITH FIVE ROCKS",
            reward = 140,
            completed = false
        ),
        EcoChallenge(
            id = "full_flow",
            title = "FULL FLOW",
            description = "REACH 100 PERCENT FLOW",
            reward = 200,
            completed = false
        )
    )
}
