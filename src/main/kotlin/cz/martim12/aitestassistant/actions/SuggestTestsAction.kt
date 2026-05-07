package cz.martim12.aitestassistant.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.ui.Messages
import cz.martim12.aitestassistant.ai.MockAiClient
import cz.martim12.aitestassistant.ai.OllamaAiClient
import cz.martim12.aitestassistant.prompt.TestPromptBuilder
import cz.martim12.aitestassistant.ui.TestSuggestionDialog
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task

class SuggestTestsAction : AnAction() {
    private val aiClient = OllamaAiClient()

    override fun actionPerformed(event: AnActionEvent){
        val project = event.project

        if (project == null){
            Messages.showErrorDialog(
                "No active project found.",
                "AI Test Assistant"
            )
            return
        }

        val editor = event.getData(CommonDataKeys.EDITOR)

        if (editor == null) {
            Messages.showErrorDialog(
                "No editor is currently active.",
                "AI Test Assistant"
            )
            return
        }

        val selectedCode = editor.selectionModel.selectedText
        if (selectedCode.isNullOrBlank()) {
            Messages.showErrorDialog(
                "Please select a method, class, or code block first.",
                "AI Test Assistant"
            )
            return
        }

        val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
        val fileName = virtualFile?.name ?: "Unknown file"
        val language = virtualFile?.extension ?: "text"

        val prompt = TestPromptBuilder.buildPrompt(
            fileName=fileName,
            language=language,
            selectedCode=selectedCode
        )

        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "Generating test suggestions...", true) {
                private var suggestion: String? = null
                private var errorMessage: String? = null

                override fun run(indicator: ProgressIndicator) {
                    try {
                        indicator.isIndeterminate = true
                        indicator.text = "Calling local LLM..."
                        suggestion = aiClient.suggestTests(prompt)
                    } catch (e: Exception) {
                        errorMessage = userError(e)
                    }
                }
                override fun onSuccess() {
                    val error = errorMessage
                    val result = suggestion
                    if (error != null) {
                        Messages.showErrorDialog(project, error, "AI Test Assistant")
                        return
                    }

                    if (result.isNullOrBlank()) {
                        Messages.showErrorDialog(
                            project,
                            "The AI provider returned an empty response.",
                            "AI Test Assistant"
                        )
                        return
                    }
                    TestSuggestionDialog(project, result).show()
                }
                override fun onCancel(){
                    Messages.showInfoMessage(project, "Test suggestion generation was cancelled.", "AI Test Assistant")
                }
            }
        )
    }




    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(event: AnActionEvent) {
        val project = event.project
        event.presentation.isEnabledAndVisible = project != null
    }

    private fun userError(e: Exception): String {
        val details = errorDetails(e)

        return when {
            details.contains("Connection refused", ignoreCase = true) ||
            details.contains("ConnectException", ignoreCase = true) -> """
                Could not connect to Ollama.
                
                Please make sure Ollama is running on localhost:11434.
            """.trimIndent()

            details.contains("timeout", ignoreCase = true) ||
            details.contains("timed out", ignoreCase = true) -> """
                The request to Ollama timed out.
    
                Try selecting a smaller code block or using a smaller/faster model.
            """.trimIndent()

            details.contains("status 404", ignoreCase = true) ||
            details.contains("not found", ignoreCase = true) -> """
                The selected Ollama model was not found.
    
                Please make sure the model is installed.
                Example:
                ollama pull qwen2.5-coder:1.5b
            """.trimIndent()

            details.contains("status 400", ignoreCase = true) -> """
                Ollama rejected the request.
    
                This usually means the request format was invalid.
                Please check the prompt/request body generation.
            """.trimIndent()

            else -> """
                Failed to generate test suggestions.
    
                Details:
                ${details.ifBlank { "Unknown error." }}
            """.trimIndent()
        }
    }


    private fun errorDetails(e: Exception): String {
        return generateSequence(e as Throwable?) { it.cause }
            .joinToString(separator = "\nCaused by: ") { throwable ->
                val message = throwable.message
                if (message.isNullOrBlank()) {
                    throwable::class.qualifiedName ?: throwable::class.simpleName ?: "Unknown error"
                } else {
                    "${throwable::class.qualifiedName}: $message"
                }
            }
    }
}