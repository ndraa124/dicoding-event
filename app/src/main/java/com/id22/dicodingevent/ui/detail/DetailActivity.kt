package com.id22.dicodingevent.ui.detail

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.id22.dicodingevent.R
import com.id22.dicodingevent.data.Result
import com.id22.dicodingevent.data.domain.model.EventDetailModel
import com.id22.dicodingevent.databinding.ActivityDetailBinding
import com.id22.dicodingevent.ui.base.BaseActivity
import com.id22.dicodingevent.util.Helper.Companion.convertDateString
import com.id22.dicodingevent.util.ViewModelFactory
import com.id22.dicodingevent.util.onScrollListener
import com.id22.dicodingevent.viewmodel.EventsViewModel

class DetailActivity : BaseActivity<ActivityDetailBinding>() {

    private val viewModel: EventsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var registerUrl: String? = null

    override fun onCreateBind(): ActivityDetailBinding =
        ActivityDetailBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupListeners()
        observeEventDetail(false)
        observeBookmarkStatus()
    }

    private fun setupToolbar() = with(bind) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { finish() }
        }
        appBar.onScrollListener(toolbar)
    }

    private fun setupListeners() = with(bind) {
        layoutError.btnTryAgain.setOnClickListener { observeEventDetail(true) }

        btnRegister.setOnClickListener {
            registerUrl?.let { url ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }

    private fun observeEventDetail(isRefresh: Boolean) {
        val eventId = intent.getIntExtra(EXTRA_ID, 0)
        val eventDetail = if (!isRefresh) {
            viewModel.getDetailEvent(eventId)
        } else {
            viewModel.refreshDetailEventData(eventId)
        }

        eventDetail.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(result.isLoading)
                is Result.Success -> showEventDetail(result.data)
                is Result.Error -> showError(result.error)
            }
        }
    }

    private fun observeBookmarkStatus() {
        viewModel.isBookmarked.observe(this) { isBookmarked ->
            setupFavoriteButton(isBookmarked)
        }
    }

    private fun setupFavoriteButton(isBookmarked: Boolean) = with(bind) {
        btnFavorite.apply {
            imageTintList = ColorStateList.valueOf(if (isBookmarked) Color.RED else Color.GRAY)
            setOnClickListener {
                intent.getIntExtra(EXTRA_ID, 0).let {
                    viewModel.saveEvent(it, isBookmarked)
                }
            }
        }
    }

    private fun showEventDetail(data: EventDetailModel) = with(bind) {
        Glide.with(this@DetailActivity).load(data.mediaCover).into(ivMedia)

        tvCategory.text = data.category
        tvName.text = data.name
        tvOwnerName.text = data.ownerName
        tvBeginTime.text = convertDateString(data.beginTime)
        tvQuota.text = getResources().getString(R.string.remaining_quota, data.quota - data.registrants)
        tvDescription.text = HtmlCompat.fromHtml(
            data.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        val isFinishedEvent = data.active == 0
        btnRegister.isVisible = !isFinishedEvent
        registerUrl = data.link

        setupFavoriteButton(data.isBookmarked)
        showContent()
    }

    private fun showLoading(isLoading: Boolean) = with(bind) {
        progressBar.isVisible = isLoading

        if (isLoading) {
            layoutError.btnTryAgain.isVisible = false
            scrollView.isVisible = false
            btnFavorite.isVisible = false
        }
    }

    private fun showError(message: String?) = with(bind) {
        layoutError.btnTryAgain.isVisible = true
        scrollView.isVisible = false
        btnFavorite.isVisible = false

        if (message != null) {
            Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showContent() = with(bind) {
        layoutError.btnTryAgain.isVisible = false
        scrollView.isVisible = true
        btnFavorite.isVisible = true
    }

    companion object {
        private const val EXTRA_ID = "id"
    }
}