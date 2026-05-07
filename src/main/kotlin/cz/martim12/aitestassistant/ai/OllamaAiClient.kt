package cz.martim12.aitestassistant.ai

import java.net.http.HttpClient
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class OllamaAiClient(
    private val model: String = "qwen2.5-coder:1.5b",
    private val endpoint: String = "http://localhost:11434/api/generate"
) : AiClient{
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    override fun suggestTests(prompt: String): String {
        val requestBody = buildRequestBody(prompt)

        val request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint))
            .timeout(Duration.ofSeconds(120))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        if(response.statusCode() !in 200..299) {
            throw RuntimeException("Ollama request failed with status ${response.statusCode()}: ${response.body()}")
        }

        return extractResponse(response.body())
    }

    private fun buildRequestBody(prompt: String): String {
        return """
            {
                "model": "$model",
                "prompt": ${jsonString(prompt)},
                "stream": false
            }
        """.trimIndent()
    }

    private fun extractResponse(responseBody: String): String {
        val marker = """"response":"""
        val start = responseBody.indexOf(marker)

        if (start == -1) {
            return responseBody
        }

        val valueStart = start + marker.length
        val parsed = parseJsonString(responseBody, valueStart)

        return parsed.ifBlank { responseBody }
    }

    private fun jsonString(value: String): String {
        return buildString {
            append('"')
            for (char in value) {
                when (char){
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(char)
                }
            }
            append('"')
        }
    }

    private fun parseJsonString(json: String, startIndex: Int): String {
        var index = startIndex

        while (index < json.length && json[index].isWhitespace()) {
            index++
        }

        if (index >= json.length || json[index] != '"') {
            return ""
        }
        index ++

        val result = StringBuilder()
        var escaping = false

        while (index < json.length) {
            val char = json[index]

            if (escaping) {
                when (char) {
                    'n' -> result.append('\n')
                    'r' -> result.append('\r')
                    't' -> result.append('\t')
                    '"' -> result.append('"')
                    '\\' -> result.append('\\')
                    'u' -> {
                        if (index + 4 < json.length) {
                            val hex = json.substring(index + 1, index + 5)
                            result.append(hex.toInt(16).toChar())
                            index += 4
                        }
                    }
                    else -> result.append(char)
                }
                escaping = false
            } else {
                when (char){
                    '\\' -> escaping = true
                    '"' -> break
                    else -> result.append(char)
                }
            }
            index++
        }

        return result.toString()
    }
}