package de.misi.idea.plugins.jenkins.dialog

import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.Insets

private val defaultInsets = Insets(3, 3, 3, 3)

fun constraints(x: Int, y: Int, gridWidth: Int = 1) =
        GridBagConstraints().apply {
            gridx = x
            gridy = y
            gridwidth = gridWidth
            gridheight = 1
            weightx = 1.0
            weighty = 1.0
            insets = defaultInsets
            fill = HORIZONTAL
        }
