package com.nendo.argosy.data.scanner

import com.nendo.argosy.data.model.VariantCategory

object FilenameTagParser {

    private val HACK_PATTERNS = listOf(
        Regex("""\[h\d*\]""", RegexOption.IGNORE_CASE),
        Regex("""\[.*\bhack\b.*\]""", RegexOption.IGNORE_CASE),
        Regex("""\(.*\bhack\b.*\)""", RegexOption.IGNORE_CASE)
    )
    private val TRANSLATION_PATTERNS = listOf(
        Regex("""\[T[-+][A-Za-z]+.*?\]"""),
        Regex("""\(T[-+][A-Za-z]+.*?\)"""),
        Regex("""\(.*\btranslat(ion|ed)\b.*\)""", RegexOption.IGNORE_CASE)
    )
    private val MOD_PATTERNS = listOf(
        Regex("""\(mod\)""", RegexOption.IGNORE_CASE),
        Regex("""\[mod\]""", RegexOption.IGNORE_CASE)
    )
    private val DEMO_PATTERNS = listOf(
        Regex("""\(demo\)""", RegexOption.IGNORE_CASE),
        Regex("""\(sample\)""", RegexOption.IGNORE_CASE),
        Regex("""\[demo\]""", RegexOption.IGNORE_CASE)
    )
    private val PROTOTYPE_PATTERNS = listOf(
        Regex("""\(proto(type)?\)""", RegexOption.IGNORE_CASE),
        Regex("""\[proto(type)?\]""", RegexOption.IGNORE_CASE)
    )
    private val PATCH_PATTERNS = listOf(
        Regex("""\(v\d+\.\d+.*?\)""", RegexOption.IGNORE_CASE),
        Regex("""\(Rev [A-Z0-9]+\)"""),
        Regex("""\[!\]""")
    )

    fun inferCategory(fileName: String): VariantCategory {
        if (HACK_PATTERNS.any { it.containsMatchIn(fileName) }) return VariantCategory.HACK
        if (TRANSLATION_PATTERNS.any { it.containsMatchIn(fileName) }) return VariantCategory.TRANSLATION
        if (MOD_PATTERNS.any { it.containsMatchIn(fileName) }) return VariantCategory.MOD
        if (DEMO_PATTERNS.any { it.containsMatchIn(fileName) }) return VariantCategory.DEMO
        if (PROTOTYPE_PATTERNS.any { it.containsMatchIn(fileName) }) return VariantCategory.PROTOTYPE
        if (PATCH_PATTERNS.any { it.containsMatchIn(fileName) }) return VariantCategory.PATCH
        return VariantCategory.UNKNOWN
    }

    private val TAG_STRIP_REGEX = Regex("""\s*[\[\(].*?[\]\)]""")
    private val EXTENSION_REGEX = Regex("""\.[^.]+$""")

    fun extractDisplayLabel(fileName: String, baseGameTitle: String): String {
        val noExt = fileName.replace(EXTENSION_REGEX, "")
        val stripped = noExt.removePrefix(baseGameTitle).trim()
        return stripped.ifEmpty { noExt }
    }
}
