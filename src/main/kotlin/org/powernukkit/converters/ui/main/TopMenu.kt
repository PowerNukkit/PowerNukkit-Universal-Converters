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

package org.powernukkit.converters.ui.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import org.powernukkit.converters.ui.ProjectResourceBundle
import org.powernukkit.converters.ui.WorldConverterGUI
import org.powernukkit.converters.ui.extensions.action
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem

/**
 * @author joserobjr
 * @since 2020-10-23
 */
internal class TopMenu(private val gui: WorldConverterGUI, private val lang: ProjectResourceBundle) {
    val about = JMenuItem(action(
        lang["window.main.menu.top.help.about"],
        gui.logo?.getScaledInstance(16, 16, Image.SCALE_FAST)?.let(::ImageIcon)
    ) {
        AboutDialog(gui)
    })

    val component = JMenuBar().apply {
        add(JMenu(lang["window.main.menu.top.help"]).apply {
            add(about)
        })
    }

    suspend fun loadIcons() = withContext(Dispatchers.Swing) {

    }
}
