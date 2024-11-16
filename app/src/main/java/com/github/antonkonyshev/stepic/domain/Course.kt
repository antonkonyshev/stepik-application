package com.github.antonkonyshev.stepic.domain

import java.util.Date

data class Course(
    val id: Long,
    val title: String,
    val summary: String,
    val description: String,
    val cover: String,
    val canonical_url: String,
    val continue_url: String,
    val readiness: Float,
    val is_paid: Boolean,
    val display_price: String,
    val create_date: Date?,
    var is_favorite: Boolean = false,
) {
    fun absoluteContinueUrl(): String {
        return "https://stepik.org${continue_url}"
    }
}