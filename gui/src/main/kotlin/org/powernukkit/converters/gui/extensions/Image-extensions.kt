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

package org.powernukkit.converters.gui.extensions

import java.awt.Image
import java.awt.image.BufferedImage
import javax.swing.ImageIcon

/**
 * @author joserobjr
 * @since 2020-11-13
 */
val Image.icon get() = ImageIcon(this)

fun Image.buffered(): BufferedImage {
    (this as? BufferedImage)?.let { return it }
    
    // Create a buffered image with transparency
    val bimage = BufferedImage(getWidth(null), getHeight(null), BufferedImage.TYPE_INT_ARGB)

    // Draw the image on to the buffered image
    val bGr = bimage.createGraphics()
    bGr.drawImage(this, 0, 0, null)
    bGr.dispose()

    // Return the buffered image
    return bimage
}
