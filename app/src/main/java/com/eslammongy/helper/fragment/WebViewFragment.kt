package com.eslammongy.helper.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.FragmentWebViewBinding

class WebViewFragment(private var webViewUrl: String? = null) : Fragment(){
    private var _binding: FragmentWebViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var endAnimation: Animation

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View{
        _binding = FragmentWebViewBinding.inflate(inflater , container , false)
        endAnimation = AnimationUtils.loadAnimation(activity!!, R.anim.ending_animation)

        binding.taskWebView.loadUrl(webViewUrl!!)
        val webViewSetting = binding.taskWebView.settings
        webViewSetting.javaScriptEnabled = true
        binding.taskWebView.webViewClient = WebViewClient()
        binding.taskWebView.canGoBack()
        binding.taskWebView.setOnKeyListener(View.OnKeyListener { _ , keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP && binding.taskWebView.canGoBack()) {

                binding.taskWebView.goBack()
                return@OnKeyListener true

            }
            return@OnKeyListener false
        })
        binding.btnExitWebView.setOnClickListener {
            binding.webViewLayout.visibility = View.GONE
            binding.webViewLayout.startAnimation(endAnimation)
        }

        binding.tvShowLinkInWebView.text = webViewUrl
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}