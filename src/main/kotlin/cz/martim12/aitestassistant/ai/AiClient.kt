package cz.martim12.aitestassistant.ai

interface AiClient {
    fun suggestTests(prompt: String): String
}