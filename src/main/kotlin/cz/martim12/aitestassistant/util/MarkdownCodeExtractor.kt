package cz.martim12.aitestassistant.util

object MarkdownCodeExtractor {
    fun extractFirstCodeBlock(markdown: String): String?{
        val regex = Regex(
            pattern = "```(?:[a-zA-Z0-9_+-]+)?\\s*\\n([\\s\\S]*?)```",
            options=setOf(RegexOption.MULTILINE)
        )

        return regex.find(markdown)
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
    }
}