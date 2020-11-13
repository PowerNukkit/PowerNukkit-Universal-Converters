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

import org.powernukkit.converters.gui.extensions.*
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.awt.CardLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagLayout
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.swing.*

/**
 * @author joserobjr
 * @since 2020-11-12
 */
class WorldValidationPanel(val chooser: JFileChooser, val cache: LevelDataCache) : PropertyChangeListener {
    private val cards = CardLayout()
    private val empty = JPanel()

    private var selections = emptyList<File>()
    private var data = mutableMapOf<File, Optional<LevelData>>()

    init {
        chooser.addPropertyChangeListener(this)
    }

    private fun updateDetails() {
        if (selections.isEmpty()) {
            //cards.show(panel, "empty")
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
                //cards.show(panel, "empty")
                visible = false
                return
            }
            it.get()
        }

        if (levelData.icon != null) {
            icon.icon = levelData.icon.scaleDownKeepingAspect(128, 96).icon
            icon.isVisible = true
        } else {
            icon.isVisible = false
        }

        updateValue(levelNameLabel, levelName, levelData.levelName)
        updateValue(editionLabel, edition, levelData.versionData?.minecraftEdition?.name?.capitalize())
        updateValue(dialectLabel, dialect, levelData.dialect?.name?.capitalize())
        updateValue(
            versionLabel,
            version,
            levelData.versionData?.run { lastOpenedWithVersion ?: baseGameVersion }?.toString()
        )
        updateValue(lastPlayedLabel, lastPlayed, levelData.lastPlayed?.let {
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(it.atZone(ZoneId.systemDefault()))
        })

        visible = true
        //cards.show(panel, "details")
    }

    private val icon = JLabel().apply { minimumSize = Dimension(250, 20) }

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

    private fun updateValue(labelComponent: JLabel, valueComponent: JLabel, value: String?, limit: Int = 16) {
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

        var line = 0
        add(icon, gridBagData(0, line++, width = 2, anchor = GBAnchor.NORTH))

        add(levelNameLabel, gridBagData(0, line))
        add(levelName, gridBagData(1, line++, 1, weightX = 1.0, fill = GBFill.HORIZONTAL))


        add(editionLabel, gridBagData(0, line))
        add(edition, gridBagData(1, line++))

        add(dialectLabel, gridBagData(0, line))
        add(dialect, gridBagData(1, line++))

        add(versionLabel, gridBagData(0, line))
        add(version, gridBagData(1, line++))

        add(lastPlayedLabel, gridBagData(0, line))
        add(lastPlayed, gridBagData(1, line++))

        add(
            Box.createRigidArea(Dimension(0, 0)),
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
