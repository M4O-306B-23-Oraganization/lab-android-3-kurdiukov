package ru.lavafrai.study.android2.models

import kotlin.uuid.Uuid

data class CounterItem(
    val name: String,
    val counter: Int,
    val id: Uuid = Uuid.random(),
)