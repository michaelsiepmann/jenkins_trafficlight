package de.misi.idea.plugins.jenkins.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StorageScheme
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.Tag
import de.misi.idea.plugins.jenkins.ChangeListener

@State(
        name = "JenkinsState",
        storages = arrayOf(
                Storage(file = "\$PROJECT_FILE$"),
                Storage(id = "main", file = "\$PROJECT_CONFIG_DIR$/jenkinsState.xml", scheme = StorageScheme.DIRECTORY_BASED)
        )
)
internal data class WorkspaceSettings(
        @Attribute("enabled")
        var enableJenkinsCheck: Boolean = DEFAULT_ENABLE_JENKINS,
        @OptionTag(nameAttribute = "analyzeBeforeCheckin")
        var analyzeBeforeCheckIn: Boolean = false,
        @Tag
        var urlSettings: URLSettings = URLSettings(),
        @Attribute("waiting")
        var waitingSeconds: Int = DEFAULT_WAITING_SECONDS,
        @Attribute("blueIsGreen")
        var blueIsGreen: Boolean = DEFAULT_BLUE_IS_GREEN,
        var showBalloonOnBlue: Boolean = false,
        var showBalloonOnYellow: Boolean = true,
        var showBalloonOnRed: Boolean = true,
        var audioSettings: AudioSettings = AudioSettings()
) : PersistentStateComponent<WorkspaceSettings> {

    private val changeListeners = mutableListOf<ChangeListener>()

    fun addChangeListener(changeListener: ChangeListener) {
        changeListeners.add(changeListener)
    }

    fun removeChangeListener(changeListener: ChangeListener) {
        changeListeners.remove(changeListener)
    }

    override fun loadState(state: WorkspaceSettings) {
        XmlSerializerUtil.copyBean(state, this)
        changeListeners.forEach {
            it.onChange(state)
        }
    }

    override fun getState() = this

    companion object {
        const val DEFAULT_ENABLE_JENKINS = false
        const val DEFAULT_WAITING_SECONDS = 10
        const val DEFAULT_BLUE_IS_GREEN = false

        fun getInstance(project: Project): WorkspaceSettings =
                ServiceManager.getService(project, WorkspaceSettings::class.java)
    }
}