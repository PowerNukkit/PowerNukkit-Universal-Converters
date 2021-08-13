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

package org.powernukkit.converters

import com.github.michaelbull.logging.InlineLogger
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.powernukkit.converters.gui.WorldConverterGUI

/**
 * @author joserobjr
 * @since 2021-08-13
 */
object WorldConverterLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            return WorldConverterGUI.main(args)
        }

        try {
            val logContext = LogManager.getContext(false) as LoggerContext
            logContext.configuration.apply {
                rootLogger.removeAppender("Console")
                getLoggerConfig("org.powernukkit.converters").removeAppender("Console")
            }
        } catch (e: Exception) {
            val log = InlineLogger()
            log.debug(e) {
                "Failed to adjust the logger for the best CLI output!"
            }
        }

        return WorldConverterCLI.main(args)
    }
}
