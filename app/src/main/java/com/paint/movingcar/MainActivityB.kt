package com.paint.movingcar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Path
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityB : AppCompatActivity() {
    private lateinit var viewModel: CarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        viewModel = ViewModelProvider(this@MainActivityB).get(CarViewModel::class.java)

        setListener()
        initObserveAngle()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener() {
        iv_car.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            viewModel.setParamCar(
                view.x, view.y, view.pivotX, view.pivotY, view.rotation
            )
        }

        cl_main.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (!viewModel.car.inAction) {
                    viewModel.changeState()

                    viewModel.initNewTouch(motionEvent.rawX, motionEvent.rawY)
                    viewModel.updateControlPoint()

                    startMoveAndRotate()
                }
            }

            true
        }
    }

    private fun initObserveAngle() {
        viewModel.replaceAngle.observe(this@MainActivityB, Observer {
            iv_car.rotation = it
        })
    }

    private fun startMoveAndRotate() {
        val animatorSet = AnimatorSet()

        val path = Path().apply {
            reset()
            moveTo(viewModel.car.current.x.toFloat(), viewModel.car.current.y.toFloat())
            cubicTo(
                viewModel.car.control.x.toFloat(),
                viewModel.car.control.y.toFloat(),
                viewModel.car.control.x.toFloat(),
                viewModel.car.control.y.toFloat(),
                viewModel.car.destination.x.toFloat(),
                viewModel.car.destination.y.toFloat()
            )
        }

        val moveAnimator = ObjectAnimator.ofFloat(iv_car, View.X, View.Y, path).apply {
            duration = viewModel.getDurationMove()
            interpolator = AccelerateDecelerateInterpolator()
        }

        val rotationAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
            iv_car,
            "rotation",
            viewModel.car.currentAngle,
            viewModel.car.destinationAngle
        ).apply {
            duration = viewModel.getDurationMove() / 3
            interpolator = LinearInterpolator()
        }
        animatorSet.play(moveAnimator).with(rotationAnimator)
        animatorSet.doOnEnd { viewModel.changeState() }
        animatorSet.start()
    }

}