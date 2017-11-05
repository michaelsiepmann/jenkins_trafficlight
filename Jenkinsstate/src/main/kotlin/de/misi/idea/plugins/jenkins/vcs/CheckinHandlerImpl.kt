package de.misi.idea.plugins.jenkins.vcs

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.util.ui.UIUtil
import de.misi.idea.plugins.jenkins.parser.JenkinsStateHandler
import de.misi.idea.plugins.jenkins.parser.createJenkinsStateHandler
import de.misi.idea.plugins.jenkins.resources.getString
import de.misi.idea.plugins.jenkins.settings.URLSettings
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings

internal class CheckinHandlerImpl(private val project: Project) : CheckinHandler() {

    override fun getBeforeCheckinConfigurationPanel() = RefreshableOnComponentImpl(workspaceSettings())

    override fun beforeCheckin(): ReturnResult {
        val settings = workspaceSettings()
        var returnResult = ReturnResult.COMMIT
        if (settings.analyzeBeforeCheckIn) {
            StateCheckTask(project) {
                run {
                    val handler = asyncRead(it, settings.urlSettings)
                    val worstState = handler.findWorstState()
                    if (worstState.isNotifyableState(settings)) {
                        val jobs = handler.getNotificationMessage()
                        val title = handler.getText(settings)
                        val message = getString("message.invalid", jobs)
                        val answer = Messages.showYesNoDialog(project, message, title, UIUtil.getWarningIcon())
                        if (answer == Messages.NO) {
                            returnResult = ReturnResult.CANCEL
                        }
                    }
                }
            }.queue()
        }
        return returnResult
    }

    private fun workspaceSettings() = WorkspaceSettings.getInstance(project)

    private fun asyncRead(indicator: ProgressIndicator, urlSettings: URLSettings): JenkinsStateHandler {
        indicator.isIndeterminate = true
        indicator.text = "Reading URL ${urlSettings.url}"
        indicator.fraction = 0.0
        return createJenkinsStateHandler(urlSettings)
    }

    private class StateCheckTask(project: Project, val doRun: (ProgressIndicator) -> Unit) : Task.Modal(project, getString("jenkins.task.title"), false) {

        override fun run(indicator: ProgressIndicator) {
            doRun(indicator)
        }
    }
}