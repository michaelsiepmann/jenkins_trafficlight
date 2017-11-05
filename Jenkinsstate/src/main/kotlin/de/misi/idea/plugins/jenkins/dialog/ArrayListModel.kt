package de.misi.idea.plugins.jenkins.dialog

import javax.swing.AbstractListModel

internal class ArrayListModel<T>(private val values : List<T>) : AbstractListModel<T>() {

    override fun getElementAt(index: Int) = values[index]

    override fun getSize() = values.size
}