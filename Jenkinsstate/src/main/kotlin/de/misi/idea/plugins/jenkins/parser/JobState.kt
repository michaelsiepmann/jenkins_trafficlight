package de.misi.idea.plugins.jenkins.parser

import com.intellij.notification.NotificationType.ERROR
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.notification.NotificationType.WARNING
import de.misi.idea.plugins.jenkins.resources.IconResources.BLACK_ICON
import de.misi.idea.plugins.jenkins.resources.IconResources.BLUE_ICON
import de.misi.idea.plugins.jenkins.resources.IconResources.CROSS_ICON
import de.misi.idea.plugins.jenkins.resources.IconResources.ERROR_ICON
import de.misi.idea.plugins.jenkins.resources.IconResources.GREEN_ICON
import de.misi.idea.plugins.jenkins.resources.IconResources.QUESTION_ICON
import de.misi.idea.plugins.jenkins.resources.IconResources.RED_ICON
import de.misi.idea.plugins.jenkins.resources.IconResources.YELLOW_ICON
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings
import javax.swing.ImageIcon

internal enum class JobState {

    blue {
        override fun isWorser(other: JobState) = other != blue

        override fun isNotifyableState(settings: WorkspaceSettings) = settings.showBalloonOnBlue

        override fun getNotificationType() = INFORMATION

        override fun getStateName(settings: WorkspaceSettings) =
                if (settings.blueIsGreen) {
                    "green"
                } else {
                    "blue"
                }

        override fun getStateIcon(settings: WorkspaceSettings) =
                if (settings.blueIsGreen) {
                    GREEN_ICON
                } else {
                    BLUE_ICON
                }
    },
    yellow {
        override fun isWorser(other: JobState) = other == red

        override fun isNotifyableState(settings: WorkspaceSettings) = settings.showBalloonOnYellow

        override fun getNotificationType() = WARNING

        override fun getStateIcon(settings: WorkspaceSettings) = YELLOW_ICON
    },
    red {
        override fun isWorser(other: JobState) = false

        override fun isNotifyableState(settings: WorkspaceSettings) = settings.showBalloonOnRed

        override fun getStateIcon(settings: WorkspaceSettings) = RED_ICON
    },
    aborted {
        override fun isWorser(other: JobState) = true

        override fun isNotifyableState(settings: WorkspaceSettings) = false

        override fun getStateIcon(settings: WorkspaceSettings) = CROSS_ICON
    },
    disabled {
        override fun isWorser(other: JobState) = true

        override fun isNotifyableState(settings: WorkspaceSettings) = false

        override fun getStateIcon(settings: WorkspaceSettings) = BLACK_ICON
    },
    error {
        override fun isWorser(other: JobState) = true

        override fun isNotifyableState(settings: WorkspaceSettings) = true

        override fun getStateIcon(settings: WorkspaceSettings) = ERROR_ICON

        override fun getNotificationTitle() = "An error occured"
    },
    unknown {
        override fun isWorser(other: JobState) = true

        override fun isNotifyableState(settings: WorkspaceSettings) = false

        override fun getStateIcon(settings: WorkspaceSettings) = QUESTION_ICON
    };

    abstract fun isWorser(other: JobState): Boolean

    abstract fun isNotifyableState(settings: WorkspaceSettings): Boolean

    open fun getStateName(settings: WorkspaceSettings) = name

    abstract fun getStateIcon(settings: WorkspaceSettings): ImageIcon

    open fun getNotificationType() = ERROR

    open fun getNotificationTitle() = "State " + name

}