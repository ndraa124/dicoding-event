package com.id22.dicodingevent.ui.home

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
import com.id22.dicodingevent.databinding.FragmentHomeBinding
import com.id22.dicodingevent.ui.adapter.EventCarouselAdapter
import com.id22.dicodingevent.ui.adapter.EventListAdapter
import com.id22.dicodingevent.ui.base.BaseFragment
import com.id22.dicodingevent.ui.detail.DetailActivity
import com.id22.dicodingevent.util.ViewModelFactory
import com.id22.dicodingevent.viewmodel.EventsViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val eventCarouselAdapter: EventCarouselAdapter by lazy { EventCarouselAdapter() }
    private val eventFinishedAdapter: EventListAdapter by lazy { EventListAdapter() }
    private val viewModel: EventsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun createLayout(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity !== null) {
            setupViews()
            observeViewModels()
        }
    }

    override fun onDestroyView() {
        bind.rvEventActive.adapter = null
        bind.rvEventFinished.adapter = null
        super.onDestroyView()
    }

    private fun setupViews() {
        setupListeners()
        setupRecyclerViews()
    }

    private fun observeViewModels() {
        observeActiveEvents(false)
        observeFinishedEvents(false)
    }

    private fun setupListeners() {
        bind.layoutErrorActive.btnTryAgain.setOnClickListener {
            observeActiveEvents(true)
        }

        bind.layoutErrorFinished.btnTryAgain.setOnClickListener {
            observeFinishedEvents(true)
        }
    }

    private fun setupRecyclerViews() = with(bind) {
        rvEventActive.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
            adapter = eventCarouselAdapter.apply {
                setOnItemClickCallback(object : EventCarouselAdapter.ActionAdapter {
                    override fun onItemClick(id: Int) {
                        toDetailEvent(id)
                    }
                })
            }
        }

        rvEventFinished.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = eventFinishedAdapter.apply {
                setOnItemClickCallback(object : EventListAdapter.ActionAdapter {
                    override fun onItemClick(id: Int) {
                        toDetailEvent(id, 1)
                    }
                })
            }
        }
    }

    private fun observeActiveEvents(isRefresh: Boolean) {
        val activeEvent = if (!isRefresh) {
            viewModel.getAllEvents(active = 1, limit = 5)
        } else {
            viewModel.refreshAllEventData(active = 1, limit = 5)
        }

        activeEvent.observe(viewLifecycleOwner) { event ->
            handleEventResult(event, true)
        }
    }

    private fun observeFinishedEvents(isRefresh: Boolean) {
        val finishedEvent = if (!isRefresh) {
            viewModel.getAllEvents(active = 0, limit = 5)
        } else {
            viewModel.refreshAllEventData(active = 0, limit = 5)
        }

        finishedEvent.observe(viewLifecycleOwner) { event ->
            handleEventResult(event, false)
        }
    }

    private fun handleEventResult(result: Result<List<EventModel>>?, isActive: Boolean) {
        if (result != null) {
            when (result) {
                is Result.Loading -> showLoading(result.isLoading, isActive)
                is Result.Success -> showContent(result.data, isActive)
                is Result.Error -> showError(result.error, isActive)
            }
        }
    }

    private fun showLoading(isLoading: Boolean, isEventActive: Boolean) = with(bind) {
        val errorLayout = if (isEventActive) layoutErrorActive else layoutErrorFinished
        val progressBar = if (isEventActive) progressBarActive else progressBarFinished
        val recyclerView = if (isEventActive) rvEventActive else rvEventFinished

        progressBar.isVisible = isLoading

        if (isLoading) {
            errorLayout.btnTryAgain.isVisible = false
            recyclerView.isVisible = false
        }
    }

    private fun showError(message: String?, isEventActive: Boolean) = with(bind) {
        val errorLayout = if (isEventActive) layoutErrorActive else layoutErrorFinished
        val recyclerView = if (isEventActive) rvEventActive else rvEventFinished
        val progressBar = if (isEventActive) progressBarActive else progressBarFinished

        errorLayout.btnTryAgain.isVisible = true
        progressBar.isVisible = false
        recyclerView.isVisible = false

        message?.let { Toast.makeText(activity, it, Toast.LENGTH_SHORT).show() }
    }

    private fun showContent(data: List<EventModel>?, isEventActive: Boolean) = with(bind) {
        if (isEventActive) {
            eventCarouselAdapter.submitList(data)
            bind.rvEventActive.requestLayout()
        } else {
            eventFinishedAdapter.submitList(data)
            bind.rvEventFinished.requestLayout()
        }

        val errorLayout = if (isEventActive) layoutErrorActive else layoutErrorFinished
        val recyclerView = if (isEventActive) rvEventActive else rvEventFinished

        if (data.isNullOrEmpty()) {
            errorLayout.btnTryAgain.isVisible = true
            recyclerView.isVisible = false
        } else {
            errorLayout.btnTryAgain.isVisible = false
            recyclerView.isVisible = true
        }
    }

    private fun toDetailEvent(id: Int, isFinished: Int = 0) {
        Intent(activity, DetailActivity::class.java).apply {
            putExtra(EXTRA_ID, id)
            putExtra(EXTRA_IS_FINISHED, isFinished)
            startActivity(this)
        }
    }

    companion object {
        private const val EXTRA_ID = "id"
        private const val EXTRA_IS_FINISHED = "isFinished"
    }
}