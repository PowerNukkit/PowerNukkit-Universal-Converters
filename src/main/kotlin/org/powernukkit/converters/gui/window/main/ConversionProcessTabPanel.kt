/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.gui.window.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTabbedPane
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-11-12
 */
class ConversionProcessTabPanel(parent: Job) : CoroutineScope {
    private val job = Job(parent)
    override val coroutineContext: CoroutineContext
        get() = job

    private val tabs = JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT)
    private var currentWorldSelection: SelectWorldPanel? = null

    init {
        with(tabs) {
            addMouseWheelListener {
                val pane = it.source as JTabbedPane
                val units: Int = it.wheelRotation
                val oldIndex = pane.selectedIndex
                val newIndex = oldIndex + units
                when {
                    newIndex < 0 ->
                        pane.selectedIndex = 0

                    newIndex >= pane.tabCount ->
                        pane.selectedIndex = pane.tabCount - 1

                    else ->
                        pane.selectedIndex = newIndex
                }
            }
        }

        addNewPlusTab()
        this += ConversionProcessTab()
    }

    operator fun plusAssign(tab: ConversionProcessTab) {
        with(tabs) {
            val index = tabCount - 1
            removeTabAt(index)

            addTab(null, tab.content)
            setTabComponentAt(index, tab.component)

            addNewPlusTab()
        }
    }

    private fun addNewPlusTab() {
        with(tabs) {
            currentWorldSelection?.cancel()
            SelectWorldPanel(job).let {
                currentWorldSelection = it
                addTab(null, it.component)
            }
            setTabComponentAt(tabCount - 1, JLabel("<html><div width=150><center>+</center></div></html>"))
        }
    }

    val component: Component get() = tabs
}
