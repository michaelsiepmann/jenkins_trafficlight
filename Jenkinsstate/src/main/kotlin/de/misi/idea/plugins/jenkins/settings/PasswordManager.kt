package de.misi.idea.plugins.jenkins.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe

internal object PasswordManager {

    fun storePassword(username: String, password: String) {
        val attributes = CredentialAttributes(javaClass, username)
        val credentials = Credentials(username, password)
        PasswordSafe.getInstance().set(attributes, credentials)
    }

    fun readPassword(username: String) =
            PasswordSafe.getInstance().getPassword(CredentialAttributes(javaClass, username))
}