package com.nendo.argosy.data.cheats

object RomFileNameParser {

    private val PAREN_GROUP = """\s*\(([^)]*)\)""".toRegex()

    private val KNOWN_REGIONS = setOf(
        "usa", "europe", "japan", "world", "asia", "korea",
        "france", "germany", "spain", "italy", "brazil",
        "australia", "china", "taiwan", "netherlands",
        "sweden", "canada", "russia", "uk", "scandinavia",
        "hong kong", "mexico", "india", "new zealand",
        "portugal", "denmark", "norway", "finland",
        "greece", "ireland", "poland", "argentina"
    )

    private val REVISION_PATTERN =
        """^(?:rev\s*\w+|v\d[\d.]*|beta|proto|sample|demo|unl|diff|alt\s*\w*)$"""
            .toRegex(RegexOption.IGNORE_CASE)

    private val DISC_PATTERN =
        """^disc\s*\d+""".toRegex(RegexOption.IGNORE_CASE)

    private val LANGUAGE_PATTERN =
        """^(?:en|ja|fr|de|es|it|nl|pt|sv|ko|zh|ru|da|fi|no|pl)(?:\s*,\s*(?:en|ja|fr|de|es|it|nl|pt|sv|ko|zh|ru|da|fi|no|pl))*$"""
            .toRegex(RegexOption.IGNORE_CASE)

    data class ParsedInfo(
        val region: String?,
        val version: String?
    )

    fun parse(fileName: String): ParsedInfo {
        val groups = PAREN_GROUP.findAll(fileName).map { it.groupValues[1] }.toList()

        val regionParts = mutableListOf<String>()
        var version: String? = null

        for (group in groups) {
            val trimmed = group.trim()
            val lower = trimmed.lowercase()

            if (DISC_PATTERN.matches(lower)) continue
            if (LANGUAGE_PATTERN.matches(lower)) continue

            if (REVISION_PATTERN.matches(lower)) {
                version = trimmed
                continue
            }

            val tokens = lower.split(",").map { it.trim() }
            if (tokens.all { it in KNOWN_REGIONS }) {
                val sorted = tokens
                    .map { it.replaceFirstChar { c -> c.uppercase() } }
                    .sorted()
                    .joinToString(", ")
                regionParts.add(sorted)
                continue
            }
        }

        val region = regionParts.joinToString(", ").ifEmpty { null }
        return ParsedInfo(region, version)
    }
}
