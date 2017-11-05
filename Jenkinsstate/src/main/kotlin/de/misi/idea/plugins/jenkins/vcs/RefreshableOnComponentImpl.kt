package de.misi.idea.plugins.jenkins.vcs

import com.intellij.openapi.vcs.ui.RefreshableOnComponent
import com.intellij.util.ui.JBUI
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings
import de.misi.idea.plugins.jenkins.resources.getString
import javax.swing.JCheckBox

internal class RefreshableOnComponentImpl(
        private val settings: WorkspaceSettings
) : RefreshableOnComponent {
    private val checkBox: JCheckBox = JCheckBox(getString("checkin.text"))

    override fun getComponent() = JBUI.Panels.simplePanel().addToLeft(checkBox)

    override fun restoreState() {
        val disabled = settings.enableJenkinsCheck && settings.urlSettings.url.isNotEmpty()
        checkBox.isSelected = if (disabled) {
            false
        } else {
            settings.analyzeBeforeCheckIn
        }
        checkBox.isEnabled = !disabled
    }

    override fun saveState() {
        settings.analyzeBeforeCheckIn = checkBox.isSelected
    }

    override fun refresh() {
    }
}