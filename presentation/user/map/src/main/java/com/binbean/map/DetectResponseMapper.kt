package com.binbean.map

import com.binbean.domain.cafe.FloorListDto
import com.binbean.domain.cafe.PositionDto
import com.binbean.user.dto.FloorList
import com.binbean.user.dto.PositionF

// PositionF → PositionDto
fun PositionF.toDto(): PositionDto = PositionDto(x, y)

// List<PositionF> → List<PositionDto>
fun List<PositionF>.toDtoList(): List<PositionDto> = map { it.toDto() }

// FloorList → FloorListDto
fun FloorList.toDto(): FloorListDto = FloorListDto(
    borderPosition = borderPosition.toDtoList(),
    seatPosition = seatPosition.toDtoList(),
    doorPosition = doorPosition.toDtoList(),
    counterPosition = counterPosition.toDtoList(),
    toiletPosition = toiletPosition.toDtoList(),
    windowPosition = windowPosition.toDtoList()
)