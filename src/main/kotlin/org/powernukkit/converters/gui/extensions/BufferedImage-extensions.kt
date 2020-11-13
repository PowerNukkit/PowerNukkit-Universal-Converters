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

package org.powernukkit.converters.gui.extensions

import java.awt.Image
import java.awt.image.BufferedImage
import kotlin.math.min

/**
 * @author joserobjr
 * @since 2020-11-12
 */
fun BufferedImage.scaleDown(percentage: Double, hints: Int = Image.SCALE_SMOOTH) =
    getScaledInstance((width * percentage).toInt(), (height * percentage).toInt(), hints)

fun BufferedImage.scaleDownKeepingAspect(targetWidth: Int, targetHeight: Int): Image {
    val scalex = targetWidth.toDouble() / width.toDouble()
    val scaley = targetHeight.toDouble() / height.toDouble()
    return scaleDown(min(scalex, scaley))
}
