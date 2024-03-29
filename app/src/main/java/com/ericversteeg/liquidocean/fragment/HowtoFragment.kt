package com.ericversteeg.liquidocean.fragment

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ericversteeg.liquidocean.R
import com.ericversteeg.liquidocean.helper.Animator
import com.ericversteeg.liquidocean.helper.Utils
import com.ericversteeg.liquidocean.listener.HowtoFragmentListener
import com.ericversteeg.liquidocean.listener.StatsFragmentListener
import com.ericversteeg.liquidocean.model.SessionSettings
import com.ericversteeg.liquidocean.model.StatTracker
import com.ericversteeg.liquidocean.view.ActionButtonView
import kotlinx.android.synthetic.main.fragment_howto.*
import kotlinx.android.synthetic.main.fragment_interactive_canvas.menu_action
import kotlinx.android.synthetic.main.fragment_interactive_canvas.menu_button
import java.util.*

class HowtoFragment: Fragment() {

    var listener: HowtoFragmentListener? = null

    var paintEventTimer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_howto, container, false)

        // setup views here

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_button.actionBtnView = back_action
        back_action.type = ActionButtonView.Type.BACK_SOLID

        paint_action.isStatic = true
        share_action.isStatic = true
        howto_background_action.isStatic = true
        howto_grid_line_action.isStatic = true
        howto_summary_action.isStatic = true
        howto_dot_action_1.isStatic = true
        howto_dot_action_2.isStatic = true
        howto_dot_action_3.isStatic = true
        howto_frame_action.isStatic = true
        howto_export_move_action.isStatic = true

        paint_action.type = ActionButtonView.Type.PAINT
        share_action.type = ActionButtonView.Type.EXPORT
        howto_background_action.type = ActionButtonView.Type.CHANGE_BACKGROUND
        howto_grid_line_action.type = ActionButtonView.Type.GRID_LINES
        howto_summary_action.type = ActionButtonView.Type.CANVAS_SUMMARY
        howto_dot_action_1.type = ActionButtonView.Type.DOT
        howto_dot_action_2.type = ActionButtonView.Type.DOT
        howto_dot_action_3.type = ActionButtonView.Type.DOT
        howto_frame_action.type = ActionButtonView.Type.FRAME
        howto_export_move_action.type = ActionButtonView.Type.EXPORT

        val colorStrs = arrayOf("#000000", "#222034", "#45283C", "#663931", "#8F563B", "#DF7126", "#D9A066", "#EEC39A",
                                "#FBF236", "#99E550", "#6ABE30", "#37946E", "#4B692F", "#524B24", "#323C39", "#3F3F74")

        var i = 0
        for (v in recent_colors_container.children) {
            (v as ActionButtonView).type = ActionButtonView.Type.RECENT_COLOR
            v.representingColor = Color.parseColor(colorStrs[i])
            v.semiGloss = true
            v.isStatic = true

            i++
        }

        back_button.setOnClickListener {
            listener?.onHowtoBack()
        }

        if (!SessionSettings.instance.tablet) {
            Animator.animateTitleFromTop(title_text)

            Animator.animateHorizontalViewEnter(step1_text, true)
            Animator.animateHorizontalViewEnter(paint_action, true)

            Animator.animateHorizontalViewEnter(step2_text, false)
            Animator.animateHorizontalViewEnter(share_action, false)

            //Animator.animateHorizontalViewEnter(static_image_1, true)
        }

        //getPaintTimerInfo()

        Utils.setViewLayoutListener(view, object: Utils.ViewLayoutListener {
            override fun onViewLayout(view: View) {
                if (step1_text.height < 250) {
                    val layoutParams = step1_text.layoutParams
                    layoutParams.height = 250
                    step1_text.layoutParams = layoutParams
                }
            }
        })

        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            scroll_view.setOnScrollChangeListener(object: View.OnScrollChangeListener {
                override fun onScrollChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int) {
                    scroll_view.scrollY
                }
            })
        }*/
    }

    override fun onPause() {
        super.onPause()

        paintEventTimer?.cancel()
    }

    /*private fun getPaintTimerInfo() {
        val requestQueue = Volley.newRequestQueue(context)

        val request = object: JsonObjectRequest(
            Request.Method.GET,
            Utils.baseUrlApi + "/api/v1/paint/time/sync",
            null,
            { response ->
                (context as Activity?)?.runOnUiThread {
                    val timeUntil = response.getInt("s").toLong()

                    if (timeUntil < 0) {
                        paint_time_info_howto.text = "???"
                    } else {
                        SessionSettings.instance.timeSync = timeUntil
                        setupPaintEventTimer()
                    }
                }
            },
            { error ->
                (context as Activity?)?.runOnUiThread {

                }
            }) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["key1"] = Utils.key1
                return headers
            }
        }

        requestQueue.add(request)
    }*/

    /*private fun setupPaintEventTimer() {
        paintEventTimer = Timer()
        paintEventTimer?.schedule(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (System.currentTimeMillis() > SessionSettings.instance.nextPaintTime) {
                        SessionSettings.instance.nextPaintTime =
                            System.currentTimeMillis() + 300 * 1000
                    }

                    val m =
                        (SessionSettings.instance.nextPaintTime - System.currentTimeMillis()) / 1000 / 60
                    val s =
                        ((SessionSettings.instance.nextPaintTime - System.currentTimeMillis()) / 1000) % 60

                    if (m == 0L) {
                        try {
                            paint_time_info_howto.text = s.toString()
                        } catch (ex: IllegalStateException) {

                        }
                    } else {
                        try {
                            paint_time_info_howto.text = String.format("%02d:%02d", m, s)
                        } catch (ex: IllegalStateException) {

                        }
                    }
                }
            }
        }, 0, 1000)
    }*/
}