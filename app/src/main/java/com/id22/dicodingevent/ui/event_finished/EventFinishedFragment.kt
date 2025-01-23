package com.id22.dicodingevent.ui.event_finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.id22.dicodingevent.R
import com.id22.dicodingevent.data.Result
import com.id22.dicodingevent.data.domain.model.EventModel
import com.id22.dicodingevent.databinding.FragmentEventsFinishedBinding
import com.id22.dicodingevent.ui.adapter.EventListAdapter
import com.id22.dicodingevent.ui.base.BaseFragment
import com.id22.dicodingevent.ui.detail.DetailActivity
import com.id22.dicodingevent.util.ViewModelFactory
import com.id22.dicodingevent.viewmodel.EventsViewModel

class EventFinishedFragment : BaseFragment<FragmentEventsFinishedBinding>() {

    private val eventAdapter: EventListAdapter by lazy { EventListAdapter() }
    private val viewModel: EventsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun createLayout(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentEventsFinishedBinding =
        FragmentEventsFinishedBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity !== null) {
            setupViews()
            observeEvents(false)
        }
    }

    override fun onDestroyView() {
        bind.rvEvent.adapter = null
        super.onDestroyView()
    }

    private fun setupViews() {
        setupListeners()
        setupSearchView()
        setupRecyclerView()
    }

    private fun setupListeners() {
        bind.layoutError.btnTryAgain.setOnClickListener {
            observeEvents(true)
        }
    }

    private fun setupSearchView() = with(bind) {
        searchView.setupWithSearchBar(bind.searchBar)
        searchView.editText.setOnEditorActionListener { textView, _, _ ->
            searchBar.setText(textView.text)
            searchView.hide()

            observeEvents(
                isRefresh = true,
                keyword = textView.text?.toString()
            )
            false
        }
    }

    private fun setupRecyclerView() {
        bind.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            adapter = eventAdapter.apply {
                setOnItemClickCallback(object : EventListAdapter.ActionAdapter {
                    override fun onItemClick(id: Int) {
                        toDetailEvent(id)
                    }
                })
            }
            setHasFixedSize(true)
        }
    }

    private fun observeEvents(isRefresh: Boolean, keyword: String? = null) {
        val events = if (!isRefresh) {
            viewModel.getAllEvents(0, keyword)
        } else {
            viewModel.refreshAllEventData(0, keyword)
        }

        events.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> showLoading(result.isLoading)
                    is Result.Success -> showContent(result.data, keyword)
                    is Result.Error -> showError(result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) = with(bind) {
        progressBar.isVisible = isLoading

        if (isLoading) {
            layoutNotFound.tvMessage.isVisible = false
            layoutError.btnTryAgain.isVisible = false
            rvEvent.isVisible = false
        }
    }

    private fun showError(message: String?) = with(bind) {
        layoutNotFound.tvMessage.isVisible = false
        layoutError.btnTryAgain.isVisible = true
        progressBar.isVisible = false
        rvEvent.isVisible = false

        message?.let { Toast.makeText(activity, it, Toast.LENGTH_SHORT).show() }
    }

    private fun showContent(data: List<EventModel>?, keyword: String?) = with(bind) {
        eventAdapter.submitList(data)

        if (data.isNullOrEmpty()) {
            layoutNotFound.tvMessage.isVisible = false
            layoutError.btnTryAgain.isVisible = keyword.isNullOrEmpty()
            layoutNotFound.tvMessage.apply {
                isVisible = !keyword.isNullOrEmpty()
                text = if (keyword != null) {
                    getString(R.string.data_not_found, "Event")
                } else null
            }
            rvEvent.isVisible = false
        } else {
            layoutNotFound.tvMessage.isVisible = false
            layoutError.btnTryAgain.isVisible = false
            rvEvent.isVisible = true
        }
    }

    private fun toDetailEvent(id: Int) {
        Intent(activity, DetailActivity::class.java).apply {
            putExtra(EXTRA_ID, id)
            putExtra(EXTRA_IS_FINISHED, 1)
            startActivity(this)
        }
    }

    companion object {
        private const val EXTRA_ID = "id"
        private const val EXTRA_IS_FINISHED = "isFinished"
    }
}