package de.misi.idea.plugins.jenkins.resources

import org.jetbrains.annotations.PropertyKey
import java.text.MessageFormat
import java.util.*

const private val BUNDLE = "de.misi.idea.plugins.jenkins.resources.messages"

private var _bundle: ResourceBundle? = null

internal fun getResourceBundle(): ResourceBundle {
    if (_bundle == null) {
        try {
            _bundle = ResourceBundle.getBundle(BUNDLE, Locale.getDefault())
        } catch (e: MissingResourceException) {
            throw MissingResourceException("Missing Resource bundle: " + Locale.getDefault() + ' ', BUNDLE, "")
        }
    }
    return _bundle!!
}

fun getString(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
    try {
        val bundle = getResourceBundle()
        if (params.isNotEmpty() && bundle.getString(key).indexOf('{') >= 0) {
            return MessageFormat.format(bundle.getString(key), *params)
        }
        return bundle.getString(key)
    } catch (e: MissingResourceException) {
        throw MissingResourceException("Missing Resource: " + Locale.getDefault() + " - key: " + key + "  - resources: " + BUNDLE, BUNDLE, key)
    }
}
