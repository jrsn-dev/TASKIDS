package com.example.game

enum class GameThemeKey {
    SKY,
    SPACE,
    FOREST,
    ISLAND;

    companion object {
        fun from(value: String?): GameThemeKey =
            entries.firstOrNull { it.name == value } ?: SKY
    }
}
