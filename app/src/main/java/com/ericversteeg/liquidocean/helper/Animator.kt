package com.ericversteeg.liquidocean.helper

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_stats.*

class Animator {
    companion object {
        var context: Context? = null

        fun animateHorizontalViewEnter(view: View, left: Boolean) {
            var offset = 2000
            if (!left) {
                offset = -offset
            }
            view.x += offset
            view.animate().translationXBy(-offset.toFloat()).setInterpolator(
                AccelerateDecelerateInterpolator()
            ).setDuration(150)
        }

        fun animateTitleFromTop(titleView: View) {
            titleView.y -= 300
            titleView.animate().translationYBy(300F).setInterpolator(AccelerateDecelerateInterpolator()).setDuration(150)
        }

        private fun getSafePoint(view: View, parent: View, safeViews: List<View>): Point {
            context?.apply {
                val x = (Math.random() * parent.width).toInt() - view.width / 2
                val y = (Math.random() * parent.height).toInt() - view.height / 2

                val safeViewMargin = Utils.dpToPx(this, 0)

                for (safeView in safeViews) {
                    if (x > safeView.x - safeViewMargin && x < safeView.x + safeView.width + safeViewMargin &&
                        y > safeView.y - safeViewMargin && y < safeView.y + safeView.height + safeViewMargin) {
                        return getSafePoint(view, parent, safeViews)
                    }
                }

                return Point(x, y)
            }

            return Point(0, 0)
        }

        fun animatePixelColorEffect(view: View, parent: View, safeViews: List<View>) {
            context?.apply {

                val point = getSafePoint(view, parent, safeViews)
                val x = point.x
                val y = point.y

                view.x = x - view.width / 2F
                view.y = y - view.height / 2F
                view.alpha = 0F

                var rA = Math.random() / 5

                if (Math.random() < 0.15) {
                    rA = 1.0
                }

                val rR = (Math.random() * 256).toInt()
                val rG = (Math.random() * 256).toInt()
                val rB = (Math.random() * 256).toInt()

                val rD = (Math.random() * 1500).toInt() + 250L
                val rS = (Math.random() * 1500).toInt() + 250L

                view.setBackgroundColor(Color.argb(255, rR, rG, rB))
                view.animate().alphaBy(rA.toFloat()).setDuration(rD).withEndAction {
                    view.animate().setStartDelay(rS).alphaBy(-rA.toFloat()).setDuration(rD).withEndAction {
                        if (context != null) {
                            animatePixelColorEffect(view, parent, safeViews)
                        }
                    }
                }

                view.animate()
            }

        }

        fun animateMenuItems(views: List<View>, cascade: Boolean, out: Boolean = false) {
            val delays = intArrayOf(0, 50, 80, 100)
            if (!out) {
                var i = 0
                for (view in views) {
                    view.x += 500
                    view.alpha = 0F

                    if (cascade) {
                        view.animate().setStartDelay(delays[i].toLong()).setDuration(150).alphaBy(1F).translationXBy(-500F).setInterpolator(AccelerateDecelerateInterpolator())
                    }
                    else {
                        view.animate().setDuration(150).alphaBy(1F).translationXBy(-500F).setInterpolator(AccelerateDecelerateInterpolator())
                    }

                    i++
                }
            }
            else {
                var i = 0
                for (view in views) {
                    if (cascade) {
                        view.animate().setStartDelay(delays[i].toLong()).setDuration(150).alphaBy(-1F).translationXBy(500F).setInterpolator(AccelerateDecelerateInterpolator()).withEndAction {
                            view.x -= 500

                            view.visibility = View.INVISIBLE
                        }
                    }
                    else {
                        view.animate().setDuration(150).alphaBy(-1F).translationXBy(500F).setInterpolator(AccelerateDecelerateInterpolator()).withEndAction {
                            view.x -= 500

                            view.visibility = View.INVISIBLE
                        }
                    }

                    i++
                }
            }
        }
    }
}