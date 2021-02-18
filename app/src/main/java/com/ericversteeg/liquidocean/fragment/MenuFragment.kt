package com.ericversteeg.liquidocean.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.ericversteeg.liquidocean.R
import com.ericversteeg.liquidocean.model.SessionSettings
import com.ericversteeg.liquidocean.listener.MenuButtonListener
import com.ericversteeg.liquidocean.model.InteractiveCanvas
import com.ericversteeg.liquidocean.view.ActionButtonView
import kotlinx.android.synthetic.main.fragment_art_export.*
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_menu.back_action
import kotlinx.android.synthetic.main.fragment_menu.back_button
import kotlinx.android.synthetic.main.fragment_options.*
import java.util.*

class MenuFragment: Fragment() {

    var menuButtonListener: MenuButtonListener? = null

    var backCount = 0

    var showcaseTimer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        // setup views here

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_button.actionBtnView = back_action
        back_action.type = ActionButtonView.Type.BACK

        back_button.setOnClickListener {
            if (backCount == 1) {
                resetMenu()
                animateMenuButtons(0)
            }
            else if (backCount == 2) {
                resetToPlayMode()
            }

            backCount--
        }

        back_button.visibility = View.GONE

        play_button.type = ActionButtonView.Type.PLAY
        options_button.type = ActionButtonView.Type.OPTIONS
        stats_button.type = ActionButtonView.Type.STATS
        exit_button.type = ActionButtonView.Type.EXIT
        single_button.type = ActionButtonView.Type.SINGLE
        world_button.type = ActionButtonView.Type.WORLD

        /*val artShowcase = SessionSettings.instance.artShowcase
        if (artShowcase != null && artShowcase.size > 0) {
            art_showcase.showBackground = false
            art_showcase.art = artShowcase[0]
        }*/

        // backgrounds
        background_option_black.type = ActionButtonView.Type.BACKGROUND_BLACK
        background_option_white.type = ActionButtonView.Type.BACKGROUND_WHITE
        background_option_gray_thirds.type = ActionButtonView.Type.BACKGROUND_GRAY_THIRDS
        background_option_photoshop.type = ActionButtonView.Type.BACKGROUND_PHOTOSHOP
        background_option_classic.type = ActionButtonView.Type.BACKGROUND_CLASSIC
        background_option_chess.type = ActionButtonView.Type.BACKGROUND_CHESS

        play_button.setOnClickListener {
            // menuButtonListener?.onMenuButtonSelected(playMenuIndex)

            play_button.visibility = View.GONE
            options_button.visibility = View.GONE
            stats_button.visibility = View.GONE
            exit_button.visibility = View.GONE

            single_button.visibility = View.VISIBLE
            world_button.visibility = View.VISIBLE
            empty_button_1.visibility = View.VISIBLE
            empty_button_2.visibility = View.VISIBLE

            back_button.visibility = View.VISIBLE
            backCount++

            animateMenuButtons(1)
        }

        options_button.setOnClickListener {
            menuButtonListener?.onMenuButtonSelected(optionsMenuIndex)
        }

        stats_button.setOnClickListener {
            menuButtonListener?.onMenuButtonSelected(statsMenuIndex)
        }

        exit_button.setOnClickListener {
            menuButtonListener?.onMenuButtonSelected(exitMenuIndex)
        }

        single_button.setOnClickListener {
            context?.apply {
                menuButtonListener?.onMenuButtonSelected(singleMenuIndex)
            }
        }

        world_button.setOnClickListener {
            menuButtonListener?.onMenuButtonSelected(worldMenuIndex)
        }

        menu_button_container.setOnClickListener {

        }

        for (v1 in single_background_options.children) {
            val v = v1 as ViewGroup
            for (v2 in v.children) {
                v2.setOnClickListener {
                    val actionButton = it as ActionButtonView
                    menuButtonListener?.onSingleBackgroundOptionSelected(actionButton.type)
                }
            }
        }

        animateMenuButtons(0)
    }

    private fun animateMenuButtons(layer: Int, out: Boolean = false) {
        if (layer == 0) {
            if (!out) {
                play_button.x += 500
                play_button.alpha = 0F
                play_button.animate().setDuration(150).alphaBy(1F).translationXBy(-500F).setInterpolator(AccelerateDecelerateInterpolator())

                options_button.x += 500
                options_button.alpha = 0F
                options_button.animate().setStartDelay(50).setDuration(150).alphaBy(1F).translationXBy(-500F).setInterpolator(AccelerateDecelerateInterpolator())

                stats_button.x += 500
                stats_button.alpha = 0F
                stats_button.animate().setStartDelay(80).setDuration(150).alphaBy(1F).translationXBy(-500F).setInterpolator(AccelerateDecelerateInterpolator())

                exit_button.x += 500
                stats_button.alpha = 0F
                exit_button.animate().setStartDelay(100).setDuration(150).alphaBy(1F).translationXBy(-500F).setInterpolator(AccelerateDecelerateInterpolator())
            }
            else {
                play_button.animate().setDuration(150).translationXBy(500F).setInterpolator(AccelerateDecelerateInterpolator())
                options_button.animate().setStartDelay(50).setDuration(150).translationXBy(500F).setInterpolator(AccelerateDecelerateInterpolator())
                stats_button.animate().setStartDelay(100).setDuration(150).translationXBy(500F).setInterpolator(AccelerateDecelerateInterpolator())
                exit_button.animate().setStartDelay(150).setDuration(150).translationXBy(500F).setInterpolator(AccelerateDecelerateInterpolator())
            }
        }
        else if (layer == 1) {
            if (!out) {
                single_button.x += 500
                single_button.animate().setDuration(150).translationXBy(-500F).setInterpolator(AccelerateDecelerateInterpolator())

                world_button.x += 500
                world_button.animate().setStartDelay(50).setDuration(150).translationXBy(-500F).setInterpolator(AccelerateDecelerateInterpolator())
            }
            else {
                single_button.animate().setDuration(150).translationXBy(500F).setInterpolator(AccelerateDecelerateInterpolator())
                world_button.animate().setStartDelay(50).setDuration(150).translationXBy(500F).setInterpolator(AccelerateDecelerateInterpolator())
            }
        }
    }

    fun getNextArtShowcase(): List<InteractiveCanvas.RestorePoint>? {
        SessionSettings.instance.artShowcase?.apply {
            if (SessionSettings.instance.showcaseIndex == size) {
                SessionSettings.instance.showcaseIndex = 0
            }

            if (size > 0) {
                val nextArt = this[SessionSettings.instance.showcaseIndex]
                SessionSettings.instance.showcaseIndex += 1

                return nextArt
            }
        }

        return null
    }

    override fun onPause() {
        super.onPause()

        showcaseTimer.cancel()
    }

    override fun onResume() {
        super.onResume()

        showcaseTimer.schedule(object: TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    SessionSettings.instance.artShowcase?.apply {
                        art_showcase.alpha = 0F

                        art_showcase.showBackground = false
                        art_showcase.art = getNextArtShowcase()

                        art_showcase.animate().alpha(1F).setDuration(2500).withEndAction {
                            Timer().schedule(object: TimerTask() {
                                override fun run() {
                                    activity?.runOnUiThread {
                                        art_showcase.animate().alpha(0F).setDuration(1500).start()
                                    }
                                }

                            }, 3000)
                        }.start()
                    }
                }
            }

        }, 0, 7000)
    }

    private fun showSingleBackgroundOptions() {
        single_button.visibility = View.GONE
        world_button.visibility = View.GONE
        empty_button_1.visibility = View.GONE
        empty_button_2.visibility = View.GONE

        single_background_options.visibility = View.VISIBLE
    }

    private fun resetMenu() {
        play_button.visibility = View.VISIBLE
        options_button.visibility = View.VISIBLE
        stats_button.visibility = View.VISIBLE
        exit_button.visibility = View.VISIBLE

        single_button.visibility = View.GONE
        world_button.visibility = View.GONE
        empty_button_1.visibility = View.GONE
        empty_button_2.visibility = View.GONE

        single_background_options.visibility = View.GONE
        back_button.visibility = View.GONE
    }

    private fun resetToPlayMode() {
        single_button.visibility = View.VISIBLE
        world_button.visibility = View.VISIBLE
        empty_button_1.visibility = View.VISIBLE
        empty_button_2.visibility = View.VISIBLE

        single_background_options.visibility = View.GONE
    }

    companion object {
        val playMenuIndex = 0
        val optionsMenuIndex = 1
        val statsMenuIndex = 2
        val exitMenuIndex = 3
        val singleMenuIndex = 4
        val worldMenuIndex = 5
    }
}