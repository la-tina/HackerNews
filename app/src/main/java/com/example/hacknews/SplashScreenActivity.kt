package com.example.hacknews

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FrameMetricsAggregator.ANIMATION_DURATION
import android.support.v7.app.AppCompatActivity
import android.view.animation.BounceInterpolator
import kotlinx.android.synthetic.main.activity_splash_screen.*

private const val SPLASH_DELAY: Long = 3000 //3 seconds

class SplashScreenActivity : AppCompatActivity() {

    private var delayHandler: Handler? = null
    private val runnable: Runnable = Runnable {
        if (!isFinishing) {

            startAnimation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Initialize the Handler
        delayHandler = Handler()

        //Navigate with delay
        delayHandler!!.postDelayed(runnable, SPLASH_DELAY)
    }

    public override fun onDestroy() {

        if (delayHandler != null) {
            delayHandler!!.removeCallbacks(runnable)
        }
        super.onDestroy()
    }

    private fun startAnimation() {
        // Intro animation configuration.
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            text.scaleX = value
            text.scaleY = value
            image.scaleX = value
            image.scaleX = value
        }
        valueAnimator.interpolator = BounceInterpolator()
        valueAnimator.duration = ANIMATION_DURATION.toLong()

        // Set animator listener.
        val intent = Intent(this, MainActivity::class.java)
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                // Navigate to main activity on navigation end.
                startActivity(intent)
                finish()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}
        })
        // Start animation.
        valueAnimator.start()
    }
}

