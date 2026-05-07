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

        val suggestion = aiClient.suggestTests(prompt)
        TestSuggestionDialog(project, suggestion).show()
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(event: AnActionEvent) {
        val project = event.project
        event.presentation.isEnabledAndVisible = project != null
    }
}