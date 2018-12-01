package com.example.yuichi.imageviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.view.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private lateinit var mPanGestureDetector: GestureDetectorCompat

    private var mScaleFactor = 1.0f
    private var mTranslationX = 0f
    private var mTranslationY = 0f
    private var mImageWidth = 0f
    private var mImageHeight = 0f
    private var mDefaultImageWidth = 0f
    private var mDefaultImageHeight = 0f
    private var mViewPortWidth = 0f
    private var mViewPortHeight = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mImageView.setImageDrawable(getDrawable(R.drawable.hongkong))

        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        mPanGestureDetector = GestureDetectorCompat(this, PanListener())

        val viewTreeObserver = mImageView.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val imageAspectRatio = mImageView.drawable.intrinsicHeight.toFloat() / mImageView.drawable.intrinsicWidth.toFloat()
                    val viewAspectRatio = mImageView.height.toFloat() / mImageView.width.toFloat()

                    mImageWidth = if (imageAspectRatio < viewAspectRatio) {
                        // landscape image
                        mImageView.width.toFloat()
                    } else {
                        // Portrait image
                        mImageView.height.toFloat() / imageAspectRatio
                    }

                    mImageHeight = if (imageAspectRatio < viewAspectRatio) {
                        // landscape image
                        mImageView.width.toFloat() * imageAspectRatio
                    } else {
                        // Portrait image
                        mImageView.height.toFloat()
                    }

                    mDefaultImageWidth = mImageWidth
                    mDefaultImageHeight = mImageHeight

                    mViewPortWidth = mImageView.width.toFloat()
                    mViewPortHeight = mImageView.height.toFloat()
                }
            })
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleGestureDetector.onTouchEvent(event)
        mPanGestureDetector.onTouchEvent(event)

        return true
    }

    private inner class PanListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent, e2: MotionEvent,
            distanceX: Float, distanceY: Float
        ): Boolean {
            val translationX = mTranslationX - distanceX
            val translationY = mTranslationY - distanceY

            adjustTranslation(translationX, translationY)

            return true
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= mScaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f))
            mImageView.scaleX = mScaleFactor
            mImageView.scaleY = mScaleFactor
            mImageWidth = mDefaultImageWidth * mScaleFactor
            mImageHeight = mDefaultImageHeight * mScaleFactor

            adjustTranslation(mTranslationX, mTranslationY)

            return true
        }
    }

    private fun adjustTranslation(translationX: Float, translationY: Float) {
        val translationXMargin = Math.abs((mImageWidth - mViewPortWidth) / 2)
        val translationYMargin = Math.abs((mImageHeight - mViewPortHeight) / 2)

        if (translationX < 0) {
            mTranslationX = Math.max(translationX, -translationXMargin)
        } else {
            mTranslationX = Math.min(translationX, translationXMargin)
        }

        if (mTranslationY < 0) {
            mTranslationY = Math.max(translationY, -translationYMargin)
        } else {
            mTranslationY = Math.min(translationY, translationYMargin)
        }

        mImageView.translationX = mTranslationX
        mImageView.translationY = mTranslationY
    }
}
