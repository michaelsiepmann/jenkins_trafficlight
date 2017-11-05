package de.misi.idea.plugins.jenkins.widget

import com.intellij.openapi.wm.impl.status.TextPanel
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Insets
import javax.swing.Icon

internal class StatePanel : TextPanel() {

    var icon: Icon? = null

    init {
        text = ""
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        text?.let {
            icon?.paintIcon(this, g,
                    insets.left - GAP - iconWidth(),
                    bounds.height / 2 - iconWidth() / 2
            )
        }
    }

    override fun getInsets(): Insets {
        val insets = super.getInsets()
        insets.left += iconWidth() + GAP * 2
        return insets
    }

    override fun getPreferredSize(): Dimension {
        val preferredSize = super.getPreferredSize()
        return Dimension(preferredSize.width + iconWidth(), preferredSize.height)
    }

    private fun iconWidth() = icon?.iconWidth ?: 0

    companion object {
        private const val GAP = 2
    }
}