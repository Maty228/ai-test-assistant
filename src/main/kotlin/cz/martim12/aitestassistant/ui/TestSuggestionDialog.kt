package cz.martim12.aitestassistant.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class TestSuggestionDialog(project: Project, private val suggestionText: String) : DialogWrapper(project) {
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
}