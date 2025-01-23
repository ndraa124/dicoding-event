package com.id22.dicodingevent.ui.event_active

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
import com.id22.dicodingevent.data.Result
import com.id22.dicodingevent.data.domain.model.EventModel
import com.id22.dicodingevent.databinding.FragmentEventsActiveBinding
import com.id22.dicodingevent.ui.adapter.EventListAdapter
import com.id22.dicodingevent.ui.base.BaseFragment
import com.id22.dicodingevent.ui.detail.DetailActivity
import com.id22.dicodingevent.util.ViewModelFactory
import com.id22.dicodingevent.viewmodel.EventsViewModel

class EventActiveFragment : BaseFragment<FragmentEventsActiveBinding>() {

    private val eventAdapter: EventListAdapter by lazy { EventListAdapter() }
    private val viewModel: EventsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun createLayout(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEventsActiveBinding =
        FragmentEventsActiveBinding.inflate(inflater, container, false)

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
        setupRecyclerViews()
    }

    private fun setupListeners() {
        bind.layoutError.btnTryAgain.setOnClickListener { observeEvents(true) }
    }

    private fun setupRecyclerViews() {
        bind.rvEvent.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = eventAdapter.apply {
                setOnItemClickCallback(object : EventListAdapter.ActionAdapter {
                    override fun onItemClick(id: Int) {
                        toDetailEvent(id)
                    }
                })
            }
        }
    }

    private fun observeEvents(isRefresh: Boolean) {
        val events = if (!isRefresh) {
            viewModel.getAllEvents(1)
        } else {
            viewModel.refreshAllEventData(1)
        }

        events.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> showLoading(result.isLoading)
                    is Result.Success -> showContent(result.data)
                    is Result.Error -> showError(result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) = with(bind) {
        progressBar.isVisible = isLoading

        if (isLoading) {
            layoutError.btnTryAgain.isVisible = false
            rvEvent.isVisible = false
        }
    }

    private fun showError(message: String?) = with(bind) {
        layoutError.btnTryAgain.isVisible = true
        progressBar.isVisible = false
        rvEvent.isVisible = false

        if (message != null) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showContent(data: List<EventModel>?) = with(bind) {
        eventAdapter.submitList(data)

        if (data.isNullOrEmpty()) {
            layoutError.btnTryAgain.isVisible = true
            rvEvent.isVisible = false
        } else {
            layoutError.btnTryAgain.isVisible = false
            rvEvent.isVisible = true
        }
    }

    private fun toDetailEvent(id: Int) {
        Intent(activity, DetailActivity::class.java).apply {
            putExtra(EXTRA_ID, id)
            startActivity(this)
        }
    }

    companion object {
        private const val EXTRA_ID = "id"
    }
}