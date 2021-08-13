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

package org.powernukkit.converters.gui

import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.swing.Swing
import org.powernukkit.converters.gui.window.main.MainFrameWindow
import java.util.*
import javax.imageio.ImageIO
import javax.swing.UIManager

/**
 * @author joserobjr
 * @since 2020-10-23
 */
class WorldConverterGUI internal constructor(private val locale: Locale) : CoroutineScope {
    private val log = InlineLogger()
    private val job = Job()
    override val coroutineContext = job + Dispatchers.Swing + CoroutineName("GUI")

    private val bundles = mutableMapOf<String, ProjectResourceBundle>()

    internal val logo =
        try {
            WorldConverterGUI::class.java.getResourceAsStream("powernukkit-logo.png")
                ?.let(ImageIO::read)
        } catch (e: Exception) {
            log.warn(e) { "Could not load the Window icon: powernukkit-logo.png" }
            null
        }

    internal val lang = ProjectResourceBundle(locale, "gui.project")
    internal val main = MainFrameWindow(this, job)

    init {
        log.info { "Showing the GUI" }
        main.show()
    }

    internal fun loadBundle(
        name: String,
        constructor: (commonLang: ProjectResourceBundle, name: String) -> ProjectResourceBundle = { lang, _ ->
            ProjectResourceBundle(locale, name, lang)
        }
    ) = bundles.computeIfAbsent(name) { constructor(lang, name) }

    internal fun close() {
        job.cancel()
    }

    companion object {
        private val log = InlineLogger()

        val VERSION = "0.1.0-SNAPSHOT"
        val SOURCE_URL = "https://github.com/PowerNukkit/PowerNukkit-Universal-Converters"

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                )
            } catch (e: Exception) {
                log.warn(e) { "Could not change the UI look and feel" }
            }
            WorldConverterGUI(Locale.getDefault())
        }
    }
}
