package cz.martim12.aitestassistant.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import cz.martim12.aitestassistant.util.MarkdownCodeExtractor
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.BorderLayout
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class TestSuggestionDialog(project: Project, private val suggestionText: String) : DialogWrapper(project) {


    private val generatedTestCode: String? = MarkdownCodeExtractor.extractFirstCodeBlock(suggestionText)
    private val copyText: String = generatedTestCode ?: suggestionText
    private val copyActionLabel: String = if (generatedTestCode.isNullOrBlank()) {
        "Copy Full Response"
    } else {
        "Copy Test Code"
    }
    private val copyAction = object : DialogWrapperAction(copyActionLabel) {
        override fun doAction(e: java.awt.event.ActionEvent?) {
            val selection = StringSelection(copyText)
            Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)
            Messages.showInfoMessage(
                if (generatedTestCode.isNullOrBlank()) {
                    "Full AI response copied to clipboard."
                } else {
                    "Generated test code copied to clipboard."
                },
                "AI Test Assistant"
            )
        }
    }

    init {
        title = "AI Test Suggestions"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val textArea = JTextArea(suggestionText).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            rows = 24
            columns = 90
        }
        val panel = JPanel(BorderLayout())
        panel.add(JScrollPane(textArea), BorderLayout.CENTER)
        return panel
    }

    override fun createActions(): Array<Action> {
        val defaultActions = super.createActions()

        return arrayOf(copyAction, *defaultActions)
    }
}