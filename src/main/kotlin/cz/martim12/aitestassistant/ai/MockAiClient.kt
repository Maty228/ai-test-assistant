package cz.martim12.aitestassistant.ai

class MockAiClient : AiClient {
    override fun suggestTests(prompt: String): String {
        return """
           Summary:
            The selected code should be tested with both normal and edge-case inputs.
            
            Suggested test cases:
            - Test normal valid input.
            - Test empty input.
            - Test null input if the method accepts nullable values.
            - Test boundary values.
            - Test invalid or unexpected input.
            - Test repeated or duplicate values if relevant.

            Edge cases:
            - Very large input.
            - Minimal input.
            - Unexpected state.
            - Exceptions or error handling paths.

            Example JUnit 5 skeleton:
            ```java
            @Test
            void shouldHandleValidInput() {
                // Arrange
                
                // Act

                // Assert
            }
            ``` 
        """.trimIndent()
    }
}