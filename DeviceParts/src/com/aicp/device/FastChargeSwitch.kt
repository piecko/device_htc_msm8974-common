/*
* Copyright (C) 2020 The Android Ice Cold Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.aicp.device

import android.content.Context
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener

class FastChargeSwitch(context: Context) : OnPreferenceChangeListener {
    private val mContext: Context = context
    override fun onPreferenceChange(preference: Preference?, newValue: Any): Boolean {
        val enabled = newValue as Boolean
        Settings.System.putInt(mContext.contentResolver, SETTINGS_KEY, if (enabled) 1 else 0)
        Utils.writeValueSimple(file, if (enabled) "1" else "0")
        return true
    }

    companion object {
        private const val FILE = "/sys/kernel/fast_charge/force_fast_charge"
        val SETTINGS_KEY: String = DeviceSettings.Companion.KEY_SETTINGS_PREFIX + DeviceSettings.Companion.KEY_FASTCHARGE
        val file: String?
            get() = if (Utils.fileWritable(FILE)) {
                FILE
            } else null

        val isSupported: Boolean
            get() = Utils.fileWritable(file)

        fun isCurrentlyEnabled(): Boolean {
            return Utils.getFileValueAsBoolean(file, false)
        }
    }

}
