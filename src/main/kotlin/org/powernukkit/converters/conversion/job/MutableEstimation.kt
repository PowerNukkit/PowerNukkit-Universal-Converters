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

package org.powernukkit.converters.conversion.job

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-11-15
 */
data class MutableEstimation(
    val current: MutableStateFlow<Int>,
    val total: MutableStateFlow<Int>,
    val countingJob: Job,
) : CoroutineScope {
    private val job = Job(countingJob)
    private val _isCounting = MutableStateFlow(false)
    val isCounting = _isCounting.asStateFlow()

    override val coroutineContext: CoroutineContext
        get() = countingJob

    val estimation = Estimation(current.asStateFlow(), total.asStateFlow(), isCounting, countingJob)

    constructor(current: Int, total: Int, countingJob: Job) :
            this(MutableStateFlow(current), MutableStateFlow(total), countingJob)

    fun startCounting(counter: Flow<Int>) = launch {
        _isCounting.value = true
        try {
            var count = 0
            counter.collect {
                count += it
                total.value = count
            }
            total.value = count
        } finally {
            _isCounting.value = false
        }
    }

    fun stopCounting() {
        job.cancelChildren()
    }
}
