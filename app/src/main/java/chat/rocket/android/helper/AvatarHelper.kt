package chat.rocket.android.helper

import android.util.Log
import chat.rocket.android.R

class AvatarHelper {

    // Colors
    private val backgroundColors: IntArray = intArrayOf(
        R.color.avatarColor1,
        R.color.avatarColor2,
        R.color.avatarColor3,
        R.color.avatarColor4,
        R.color.avatarColor5,
        R.color.avatarColor6,
        R.color.avatarColor7,
        R.color.avatarColor8,
        R.color.avatarColor9,
        R.color.avatarColor10,
        R.color.avatarColor11,
        R.color.avatarColor12,
        R.color.avatarColor13,
        R.color.avatarColor14,
        R.color.avatarColor15,
        R.color.avatarColor16,
        R.color.avatarColor17,
        R.color.avatarColor18
    )

    // Methods
    fun getAvatarBackground(username: CharSequence): Int {
        val username = if (username.isNotEmpty()) username else "?"
        val position = username.length % backgroundColors.size

        return backgroundColors[position]
    }

}