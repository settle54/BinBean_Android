package com.binbean.user.dto

data class Position(val x: Float, val y: Float)

data class DetectRequest(
    val borderPosition: List<Position>,
    val seatPosition: List<Position>,
    val doorPosition: List<Position>,
    val counterPosition: List<Position>,
    val toiletPosition: List<Position>,
    val windowPosition: List<Position>
)
