/*
 *  PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.gui.window.main

import kotlinx.coroutines.*
import kotlinx.coroutines.swing.Swing
import org.powernukkit.converters.gui.WorldConverterGUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.WindowConstants

/**
 * @author joserobjr
 * @since 2020-10-23
 */
internal class MainFrameWindow(private val gui: WorldConverterGUI, parentJob: Job) : CoroutineScope {
    private val job = Job(parentJob)
    override val coroutineContext = job + Dispatchers.Swing + CoroutineName("Main Frame")

    val lang = gui.loadBundle("gui.window.main")

    private val topMenu = TopMenu(gui, lang)
    private val tabs = ConversionProcessTabPanel(job)

    val frame = JFrame(gui.lang["project.title"]).apply {
        launch {
            try {
                job.join()
            } finally {
                dispose()
            }
        }


        launch {
            topMenu.loadIcons()
        }

        iconImage = gui.logo
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        minimumSize = Dimension(750, 500)
        layout = BorderLayout()

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                onAttemptToClose()
            }
        })
    }

    init {
        frame.apply {
            add(topMenu.component, BorderLayout.NORTH)
            add(tabs.component, BorderLayout.CENTER)

            size = minimumSize
            setLocationRelativeTo(null)
        }
    }

    fun show() {
        frame.isVisible = true
    }

    fun close() {
        job.cancel()
    }

    private fun onAttemptToClose() {
        gui.close()
    }
}
