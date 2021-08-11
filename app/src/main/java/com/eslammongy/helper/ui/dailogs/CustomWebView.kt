package com.eslammongy.helper.ui.dailogs

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.eslammongy.helper.databinding.FragmentCustomWebViewBinding

class CustomWebView(private var webViewUrl: String? = null) : DialogFragment() {
    private var _binding: FragmentCustomWebViewBinding? = null
    private val binding get() = _binding!!
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentCustomWebViewBinding.inflate(inflater, container, false)

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
           dialog!!.dismiss()
        }

        binding.tvShowLinkInWebView.text = webViewUrl
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        val width = (resources.displayMetrics.widthPixels)
        val height = (resources.displayMetrics.heightPixels)
        sheetContainer.layoutParams.width = width
        sheetContainer.layoutParams.height = height
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sheetContainer.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}