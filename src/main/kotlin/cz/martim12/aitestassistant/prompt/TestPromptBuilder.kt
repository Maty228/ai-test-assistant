package cz.martim12.aitestassistant.prompt

object TestPromptBuilder {
    fun buildPrompt(fileName: String, language: String, selectedCode: String) : String {
        return """
            You are an AI assistant helping a developer write tests.

            File name:
            $fileName

            Language:
            $language

            Selected code:
            ```$language
            $selectedCode
            ```

            Please suggest useful tests for this code.
            
            Return the answer in this structure:
            - Summary
            - Suggested test cases
            - Edge cases
            - Example JUnit 5 test skeleton if applicable
        """.trimIndent()
    }
}