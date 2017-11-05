package de.misi.idea.plugins.jenkins.vcs

import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory

internal class CheckinHandlerFactoryImpl : CheckinHandlerFactory() {

    override fun createHandler(panel: CheckinProjectPanel, context: CommitContext) = CheckinHandlerImpl(panel.project)
}