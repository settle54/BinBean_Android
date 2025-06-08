package com.binbean.domain.cafe

import java.io.Serializable

data class FloorPlanResponse(
    val floorList: FloorListDto,
    val floorNumber: Int,
    val currentSeats: CurrentSeats
): Serializable

data class FloorListDto(
    val borderPosition: List<PositionDto>,
    val seatPosition: List<PositionDto>,
    val doorPosition: List<PositionDto>,
    val counterPosition: List<PositionDto>,
    val toiletPosition: List<PositionDto>,
    val windowPosition: List<PositionDto>
): Serializable

data class CurrentSeats(
    val currentPosition: List<PositionDto>
): Serializable

data class PositionDto(
    val x: Float,
    val y: Float
): Serializable