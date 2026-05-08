package cz.martim12.aitestassistant.prompt

object TestPromptBuilder {
    fun buildPrompt(fileName: String, language: String, selectedCode: String) : String {
        return """
    You are an AI assistant helping a developer write high-quality tests.

    Analyze the selected code carefully and suggest tests that cover important behaviors, edge cases, and potential failure points.

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
    Do NOT include code snippets in this section.

    ### Edge cases
    List unusual or potentially problematic situations worth testing.
    Do NOT include code snippets in this section.

    ### Generated JUnit 5 test class
    Generate one complete JUnit 5 test class if possible.

    Important formatting rules:
    - Generate ALL code only in the final section.
    - The final section must contain exactly ONE markdown code block.
    - Do not generate code blocks anywhere else in the response.
    - The code block must start with ```_language_.
    - The code block must end with ```.
    - Do not generate multiple code blocks.
    - Do not include explanations inside the code block.
    - Prefer concrete tests over TODO placeholders.
    - If exact assertions are impossible because context is missing, explain assumptions using comments inside the generated test class.
    - The generated code block should contain a complete, copyable test class whenever possible.
""".trimIndent()
    }
}