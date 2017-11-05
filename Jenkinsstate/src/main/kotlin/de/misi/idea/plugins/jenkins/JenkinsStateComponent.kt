package de.misi.idea.plugins.jenkins

import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.wm.WindowManager
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings
import de.misi.idea.plugins.jenkins.widget.CurrentStateWidget

class JenkinsStateComponent : ApplicationComponent {
    private lateinit var currentStateWidget: CurrentStateWidget

    override fun initComponent() {
        ProjectManager.getInstance().addProjectManagerListener(object : ProjectManagerListener {
            override fun projectOpened(project: Project) {
                val settings = WorkspaceSettings.getInstance(project)
                currentStateWidget = CurrentStateWidget(project)
                currentStateWidget.initialize(settings)
                statusBar(project).addWidget(currentStateWidget, "before Position", project)
                settings.addChangeListener(currentStateWidget)
            }
        })
    }

    private fun statusBar(project: Project) =
            WindowManager.getInstance().getStatusBar(project) ?: WindowManager.getInstance().getStatusBar(null)

    override fun getComponentName() = "JenkinsState"

    override fun disposeComponent() {
        currentStateWidget.stopTimer()
    }
}