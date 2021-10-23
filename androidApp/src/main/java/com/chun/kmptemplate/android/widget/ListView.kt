package com.chun.kmptemplate.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.chun.kmptemplate.android.R
import com.chun.kmptemplate.android.binding.bindPaddingBottom
import com.chun.kmptemplate.android.binding.defaultVerList
import com.chun.kmptemplate.android.databinding.ListBinding
import com.chun.kmptemplate.android.extension.fadeIn
import com.chun.kmptemplate.android.extension.fadeOut
import com.chun.kmptemplate.android.extension.getColorAttr
import com.chun.kmptemplate.android.extension.inflater
import com.chun.kmptemplate.log
import com.chun.kmptemplate.viewmodel.LoadingScreen

private val TAG = ListView::class.simpleName

class ListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private var useSwipe: Boolean = false
    val binding by lazy {
        ListBinding.bind(this)
    }

    init {
        context.inflater.inflate(R.layout.list, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.ListView).use { a ->
            val enableSwipe = a.getBoolean(R.styleable.ListView_lv_enableSwipe, false)
            useSwipe = enableSwipe
            binding.swipeRefresh.isEnabled = enableSwipe
            val isVerticalList = a.getBoolean(R.styleable.ListView_lv_isVerticalList, true)
            binding.rcv.apply {
                if (isVerticalList) {
                    defaultVerList()
                }
                bindPaddingBottom()
                hasFixedSize()
            }
        }

        binding.swipeRefresh.apply {
            val arrayOfInt = IntArray(1)
            arrayOfInt[0] = context.getColorAttr(R.attr.colorAccent)
            setColorSchemeColors(*arrayOfInt)
        }
    }

    fun setScreen(screen: LoadingScreen) {
        log.e { "$TAG screenxxx $screen" }
        var isListEmpty = false
        var isLoading = false
        var isError = false
        var isRefresh = false
        // disable swipe when the screen is error/no data/loading
        if (screen is LoadingScreen.Data<*>) {
            binding.swipeRefresh.isEnabled = useSwipe
        } else {
            binding.swipeRefresh.isEnabled = false
        }
        when (screen) {
            is LoadingScreen.Data<*> -> {
                isRefresh = screen.isRefresh
            }
            is LoadingScreen.Error -> {
                isError = true
                binding.loading.errorTv.text = screen.errorMessage
            }
            LoadingScreen.Loading -> {
                isLoading = true
            }
            is LoadingScreen.NoData -> {
                isListEmpty = true
            }
        }
        // show empty list
        showEmptyList(isListEmpty)
        // Show loading spinner during initial load or refresh.
        binding.loading.loadingContainer.apply {
            if (isLoading) {
                fadeIn()
            } else {
                fadeOut()
            }
        }
        // refresh
        binding.swipeRefresh.isRefreshing = isRefresh
        // Show the retry state if initial load or refresh fails and there are no items.
        binding.loading.retryBtn.isVisible = isError
        binding.loading.errorTv.isVisible = isError
    }

    private fun showEmptyList(show: Boolean) {
        binding.emptyListContainer.isVisible = show
        binding.rcv.isVisible = show
    }

    fun setOnRetryClick(onRetry: () -> Unit) {
        binding.loading.retryBtn.setOnClickListener {
            onRetry()
        }
    }
}
