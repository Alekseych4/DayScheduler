package com.open.day.dayscheduler.model

import androidx.annotation.StringRes
import com.open.day.dayscheduler.R

enum class Tag(val stringResId: Int) {
    WORK(R.string.work_tag),
    STUDY(R.string.study_tag),
    WORKOUT(R.string.workout_tag),
    MEAL(R.string.meal_tag),
    LEISURE(R.string.leisure_tag);

    companion object {
        fun getTagByResString(@StringRes id: Int): Tag? {
            return Tag.values().asList().stream()
                .filter { it.stringResId == id }
                .findFirst()
                .orElseGet{ null }
        }
    }
}