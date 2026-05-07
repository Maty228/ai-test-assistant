package cz.martim12.aitestassistant.prompt

object TestPromptBuilder {
    fun buildPrompt(fileName: String, language: String, selectedCode: String) : String {
        return """
            You are an AI assistant helping a developer write high-quality tests.
        
            Analyze the selected code carefully and suggest tests that cover all important behaviors and potential failure points.
        
            File name:
            $fileName
        
            Language:
            $language
        
            Selected code:
            ```$language
            $selectedCode
            ```
        
            Please suggest useful and practical tests for this code.
        
            Try to cover:
            - normal expected behavior
            - edge cases
            - invalid inputs
            - error handling
            - boundary values
            - interactions between methods/classes if visible
            - assumptions or limitations in the implementation
        
            Return the answer in this exact structure:
        
            ### Summary
            Briefly explain what the selected code does.
        
            ### Suggested test cases
            List concrete and meaningful test cases.
        
            ### Edge cases
            List unusual or potentially problematic situations worth testing.
        
            ### Generated JUnit 5 test class
            If applicable, generate one complete JUnit 5 test class.
        
            Rules for generated code:
            - Put generated code in exactly one markdown code block.
            - The code block must start with ```java or ```kotlin.
            - The code block must end with ```.
            - Do not generate multiple code blocks.
            - Do not put explanations inside the code block.
            - Prefer concrete tests over TODO placeholders.
            - If exact assertions are impossible because context is missing, explain assumptions in comments.
        """.trimIndent()
    }
}