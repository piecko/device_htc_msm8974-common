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
import android.util.AttributeSet
import android.util.Log
import android.widget.SeekBar
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class BacklightDimmerPreference(context: Context?, attrs: AttributeSet?) : Preference(context, attrs), SeekBar.OnSeekBarChangeListener {
    private var mSeekBar: SeekBar? = null
    private var mOldStrength = 0
    private val mMinValue = 0
    private val mMaxValue = 255
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        mOldStrength = getValue(context)!!.toInt()
        mSeekBar = holder.findViewById(R.id.seekbar) as SeekBar
        mSeekBar!!.max = mMaxValue - mMinValue
        mSeekBar!!.progress = mOldStrength - mMinValue
        mSeekBar!!.setOnSeekBarChangeListener(this)
    }

    private fun setValue(newValue: String, withFeedback: Boolean) {
        Utils.writeValue(FILE_LEVEL, newValue)
        Settings.System.putString(context.contentResolver, SETTINGS_KEY, newValue)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int,
                          fromTouch: Boolean) {
        setValue((progress + mMinValue).toString(), true)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // NA
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // NA
    }

    companion object {
        private const val DEBUG = false
        private const val TAG = "BacklightDimmerPreference"
        private const val FILE_LEVEL = "/sys/devices/mdp.0/qcom,mdss_fb_primary.174/leds/lcd-backlight/max_brightness"
        const val SETTINGS_KEY: String = DeviceSettings.Companion.KEY_SETTINGS_PREFIX + DeviceSettings.Companion.KEY_BACKLIGHT_DIMMER
        const val DEFAULT_VALUE = "200"
        val isSupported: Boolean
            get() = Utils.fileWritable(FILE_LEVEL)

        fun getValue(context: Context?): String? {
            Log.i(TAG, "reading sysfs file: $FILE_LEVEL")
            return Utils.getFileValue(FILE_LEVEL, DEFAULT_VALUE)
        }

        fun restore(context: Context) {
            if (!isSupported) {
                return
            }
            var storedValue: String = Settings.System.getString(context.contentResolver, SETTINGS_KEY)
            if (DEBUG) Log.d(TAG, "restore file:$FILE_LEVEL value:$storedValue")
            Utils.writeValue(FILE_LEVEL, storedValue)
        }
    }

    init {
        // from drivers/video/msm/mdss/mdss_fb.c
        layoutResource = R.layout.preference_seek_bar
    }
}
