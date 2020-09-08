package com.paint.movingcar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var viewModel: CarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        viewModel = ViewModelProvider(this@MainActivity).get(CarViewModel::class.java)

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
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    if (!viewModel.car.inAction) {
                        viewModel.changeState()

                        viewModel.initNewTouch(motionEvent.rawX, motionEvent.rawY)

                        startRotate()
                    }
                }
            }

            true
        }
    }

    private fun initObserveAngle() {
        viewModel.replaceAngle.observe(this@MainActivity, Observer {
            iv_car.rotation = it
        })
    }

    private fun startRotate() {
        iv_car.animate()
            .rotation(viewModel.car.destinationAngle)
            .setDuration(viewModel.getDurationRotate())
            .setInterpolator(LinearInterpolator())
            .withEndAction { startMove() }
            .start()
    }

    private fun startMove() {
        iv_car.animate()
            .x((viewModel.car.destination.x).toFloat())
            .y((viewModel.car.destination.y).toFloat())
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(viewModel.getDurationMove())
            .withEndAction { viewModel.changeState() }
            .start()
    }

}