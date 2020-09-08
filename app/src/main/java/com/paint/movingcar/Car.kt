package com.paint.movingcar

import android.graphics.Point

data class Car(
    var current: Point,
    var pivot: Point,
    var currentAngle: Float,
    var destination: Point,
    var destinationAngle: Float,
    var control: Point,
    var inAction: Boolean
) {
    companion object {
        val EMPTY = Car(Point(0, 0), Point(0, 0), 0f, Point(0, 0), 0f, Point(0, 0), false)
    }
}