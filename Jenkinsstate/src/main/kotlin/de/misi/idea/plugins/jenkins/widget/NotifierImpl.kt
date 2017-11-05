package de.misi.idea.plugins.jenkins.widget

import com.intellij.notification.Notification
import com.intellij.notification.Notifications
import de.misi.idea.plugins.jenkins.parser.JenkinsStateHandler
import de.misi.idea.plugins.jenkins.parser.JobState
import de.misi.idea.plugins.jenkins.playMediaFile
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings

internal class NotifierImpl : Notifier {

    private var notification: Notification? = null

    override fun notify(state: JobState, notificationMessage: String, settings: WorkspaceSettings, handler: JenkinsStateHandler) {
        notification?.hideBalloon()
        notification = null
        if (state.isNotifyableState(settings)) {
            notification = Notification(CurrentStateWidget.NOTIFICATION_ID, state.getNotificationTitle(), notificationMessage, state.getNotificationType())
            Notifications.Bus.notify(notification!!)
            settings.audioSettings.apply {
                if (playAudioFile) {
                    playMediaFile(audioFile)
                }
            }
        }
    }
}
