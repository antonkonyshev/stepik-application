package com.github.antonkonyshev.stepic.domain

data class Course(
    val id: Long,
    val title: String,
    val summary: String,
    val cover: String,
    val is_paid: Boolean,
    val display_price: String,
    val create_date: String
)