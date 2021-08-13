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
import org.powernukkit.converters.gui.extensions.*
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.image.BufferedImage
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-11-12
 */
class WorldValidationPanel(
    private val chooser: JFileChooser,
    private val cache: LevelDataCache,
    parent: Job
) : PropertyChangeListener, CoroutineScope {
    private val job = Job(parent)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Swing

    private var selections = emptyList<File>()
    private var data = mutableMapOf<File, Optional<LevelData>>()

    private val defaultImage = cache.defaultSquareIcon
        ?.scaleDownKeepingAspect(128, 64)
        ?: BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB)

    init {
        chooser.addPropertyChangeListener(this)
    }

    private fun updateDetails() {
        job.cancelChildren()
        if (selections.isEmpty()) {
            visible = false
            return
        }

        val last = selections
            .asSequence()
            .filter { it.isFile || it.isDirectory && it.resolve("level.dat").isFile }
            .map { if (it.isFile) it else it.resolve("level.dat") }
            .lastOrNull()
            ?: return

        val levelData = data.computeIfAbsent(last, cache::getOpt).let {
            if (!it.isPresent) {
                visible = false
                return
            }
            it.get()
        }

        val deferredIcon = cache.getIcon(levelData)
        suspend fun updateIcon() {
            val loadedIcon = deferredIcon.await()
                ?.scaleDownKeepingAspect(128, 96)
                ?: return
            icon.icon = loadedIcon.icon
        }

        if (deferredIcon.isCompleted) {
            runBlocking { updateIcon() }
        } else {
            icon.icon = ImageIcon(defaultImage)
            launch { updateIcon() }
        }

        updateValue(levelNameLabel, levelName, levelData.levelName)
        updateValue(editionLabel, edition, levelData.versionData?.minecraftEdition?.name?.capitalize())
        updateValue(dialectLabel, dialect, levelData.dialect?.toString()?.capitalize())
        updateValue(
            versionLabel,
            version,
            levelData.versionData?.run { lastOpenedWithVersion ?: baseGameVersion }?.toString()
        )
        updateValue(lastPlayedLabel, lastPlayed, levelData.lastPlayed?.let {
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(it.atZone(ZoneId.systemDefault()))
        })

        visible = true
    }

    private val icon = JLabel()

    private val levelName = JLabel()
    private val levelNameLabel = createLabel("Name:", levelName)

    private val edition = JLabel()
    private val editionLabel = createLabel("Edition:", edition)

    private val dialect = JLabel()
    private val dialectLabel = createLabel("Dialect:", dialect)

    private val version = JLabel()
    private val versionLabel = createLabel("Version:", version)

    private val lastPlayed = JLabel()
    private val lastPlayedLabel = createLabel("Last Played:", lastPlayed)

    private fun createLabel(name: String, owner: Component) = JLabel(name).bold().apply { labelFor = owner }

    private fun updateValue(labelComponent: JLabel, valueComponent: JLabel, value: String?, limit: Int = 20) {
        if (value != null) {
            valueComponent.text = value.run { if (length > limit) take(limit - 3) + "..." else this }
            labelComponent.isVisible = true
            valueComponent.isVisible = true
        } else {
            labelComponent.isVisible = false
            valueComponent.isVisible = false
        }
    }

    private val detailsPanel = JPanel(GridBagLayout()).apply {
        isVisible = false

        border = EmptyBorder(0, 10, 0, 0)

        var line = 0
        add(icon, gridBagData(0, line++))

        add(levelName.bold(), gridBagData(0, line++, paddingY = 5))

        add(edition, gridBagData(0, line++))

        add(dialect, gridBagData(0, line++))

        add(version, gridBagData(0, line++))

        add(lastPlayedLabel.bold(), gridBagData(0, line++, insets = Insets(5, 0, 0, 0)))
        add(lastPlayed, gridBagData(0, line++))

        add(
            Box.createRigidArea(Dimension(128, 0)),
            gridBagData(0, line, width = 2, weightY = 1.0, fill = GBFill.VERTICAL)
        )
    }

    override fun propertyChange(ev: PropertyChangeEvent) {
        selections = when (ev.propertyName) {
            JFileChooser.DIRECTORY_CHANGED_PROPERTY -> emptyList()
            JFileChooser.SELECTED_FILE_CHANGED_PROPERTY -> listOf(ev.newValue as? File ?: return)
            JFileChooser.SELECTED_FILES_CHANGED_PROPERTY -> chooser.selectedFiles.toList()
            else -> return
        }

        data = mutableMapOf()
        updateDetails()
    }

    private var visible: Boolean = false
        set(value) {
            if (field != value) {
                detailsPanel.isVisible = value
            }
            field = value
        }

    val component: JComponent = detailsPanel
}
