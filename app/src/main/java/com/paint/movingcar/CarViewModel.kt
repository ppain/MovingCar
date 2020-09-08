package com.paint.movingcar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.*

class CarViewModel : ViewModel() {
    val car: Car = Car.EMPTY

    private val _replaceAngle = MutableLiveData<Float>()
    val replaceAngle = _replaceAngle as LiveData<Float>

    fun setParamCar(x: Float, y: Float, pivotX: Float, pivotY: Float, currentAngle: Float) {
        car.current.x = x.roundToInt()
        car.current.y = y.roundToInt()
        car.pivot.x = pivotX.roundToInt()
        car.pivot.y = pivotY.roundToInt()
        car.currentAngle = currentAngle
    }

    fun changeState() {
        if (car.inAction) {
            car.current.x = car.destination.x
            car.current.y = car.destination.y
            car.currentAngle = car.destinationAngle
        }
        car.inAction = !car.inAction
    }

    fun initNewTouch(xTouch: Float, yTouch: Float) {
        car.destination.x = xTouch.roundToInt() - car.pivot.x
        car.destination.y = yTouch.roundToInt() - car.pivot.y

        updateAngle()
    }

    fun updateControlPoint() {
        val k = (getDistance() / 10).roundToInt()
        car.control.x = car.current.x +
                if (car.current.x > car.destination.x) k
                else -k

        car.control.y = car.current.y +
                if (car.current.y < car.destination.y) k
                else -k
    }

    private fun updateAngle() {
        val xDiff = (car.destination.x - car.current.x).toDouble()
        val yDiff = (car.destination.y - car.current.y).toDouble()
        var angle = Math.toDegrees(atan2(yDiff, xDiff)).toFloat()
        if (abs(car.currentAngle - angle) > 180) {
            when {
                angle > 0 -> angle = revertAngle(angle)
                car.currentAngle > 0 -> _replaceAngle.value = revertAngle(car.currentAngle)
                car.currentAngle < 180 -> _replaceAngle.value = revertAngle(car.currentAngle)
            }
        }
        car.destinationAngle = angle
    }

    private fun revertAngle(angle: Float) = when {
        angle > 0 -> angle - 360
        else -> angle + 360
    }

    fun getDurationRotate() = abs(car.currentAngle - car.destinationAngle).roundToLong() * 5

    fun getDurationMove() = (getDistance() / 1.5).roundToLong() + 500

    fun getDistance() = hypot(
        (car.current.x - car.destination.x).toDouble(),
        (car.current.y - car.destination.y).toDouble()
    )
}