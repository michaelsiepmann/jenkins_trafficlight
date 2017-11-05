package de.misi.idea.plugins.jenkins.dialog.settings

import javax.swing.JCheckBox
import javax.swing.JPasswordField
import javax.swing.JTextField

@JvmOverloads
fun enableAuthenticationComponents(enabled: Boolean = true, requiredAuthentcation: JCheckBox, username: JTextField, storeAPIToken: JCheckBox, apitoken: JPasswordField) {
    requiredAuthentcation.isEnabled = enabled
    val requiredSelected = enabled && requiredAuthentcation.isSelected
    username.isEnabled = requiredSelected
    storeAPIToken.isEnabled = requiredSelected
    apitoken.isEnabled = requiredSelected && storeAPIToken.isSelected
}