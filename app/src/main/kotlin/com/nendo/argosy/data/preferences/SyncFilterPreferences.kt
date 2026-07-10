package com.nendo.argosy.data.preferences

data class SyncFilterPreferences(
    val enabledRegions: List<String> = DEFAULT_REGIONS,
    val regionMode: RegionFilterMode = RegionFilterMode.INCLUDE,
    val excludeBeta: Boolean = true,
    val excludePrototype: Boolean = true,
    val excludeDemo: Boolean = true,
    val excludeHack: Boolean = false,
    val deleteOrphans: Boolean = true
) {
    /** Enabled regions in priority order followed by disabled; canonical order when priority is off. */
    val pickerDisplayOrder: List<String>
        get() = if (regionMode != RegionFilterMode.INCLUDE) ALL_KNOWN_REGIONS
        else enabledRegions + ALL_KNOWN_REGIONS.filter { it !in enabledRegions }

    /** Rank of a rom's best region in the priority list; unranked and untagged roms sort last. */
    fun regionRank(romRegions: List<String>?): Int {
        if (romRegions.isNullOrEmpty()) return UNRANKED
        return romRegions.minOf { region ->
            enabledRegions.indexOfFirst { it.equals(region, ignoreCase = true) }
                .let { if (it == -1) UNRANKED else it }
        }
    }

    companion object {
        const val UNRANKED = Int.MAX_VALUE
        val ALL_KNOWN_REGIONS = listOf(
            "USA", "World", "Europe", "Japan", "Korea",
            "China", "Taiwan", "Australia", "Brazil",
            "France", "Germany", "Italy", "Spain"
        )
        val DEFAULT_REGIONS = ALL_KNOWN_REGIONS
    }
}

enum class RegionFilterMode {
    INCLUDE,
    EXCLUDE
}
