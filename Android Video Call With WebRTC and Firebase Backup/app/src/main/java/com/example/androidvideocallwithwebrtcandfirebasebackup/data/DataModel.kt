package com.example.androidvideocallwithwebrtcandfirebasebackup.data

data class DataModel(
    val sender: String ?= null,
    val target: String,
    val type: DataModelType,
    val data: String ?= null,
    val timeStamp: Long = System.currentTimeMillis()
) {
}

fun DataModel.isValid(): Boolean {
    return System.currentTimeMillis() - this.timeStamp < 60000
}