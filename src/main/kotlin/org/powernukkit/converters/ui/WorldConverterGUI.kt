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

package org.powernukkit.converters.ui

import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.swing.Swing
import org.powernukkit.converters.ui.main.MainFrameWindow
import javax.imageio.ImageIO

/**
 * @author joserobjr
 * @since 2020-10-23
 */
internal class WorldConverterGUI : CoroutineScope {
    private val log = InlineLogger()
    private val job = Job()
    override val coroutineContext = job + Dispatchers.Swing + CoroutineName("GUI")

    val logo =
        try {
            WorldConverterGUI::class.java.getResourceAsStream("powernukkit-logo.png")
                ?.let(ImageIO::read)
        } catch (e: Exception) {
            log.warn(e) { "Could not load the Window icon: powernukkit-logo.png" }
            null
        }

    val main = MainFrameWindow(this, job)

    init {
        main.show()
    }

    fun close() {
        job.cancel()
    }

    companion object {
        val VERSION = "0.1.0-SNAPSHOT"
        val SOURCE_URL = "https://github.com/PowerNukkit/PowerNukkit-Universal-Converters"
    }
}
