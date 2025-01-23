package com.id22.dicodingevent.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.id22.dicodingevent.R
import com.id22.dicodingevent.data.domain.model.EventModel
import com.id22.dicodingevent.databinding.FragmentFavoriteBinding
import com.id22.dicodingevent.ui.adapter.EventListAdapter
import com.id22.dicodingevent.ui.base.BaseFragment
import com.id22.dicodingevent.ui.detail.DetailActivity
import com.id22.dicodingevent.util.ViewModelFactory
import com.id22.dicodingevent.viewmodel.EventsViewModel

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {

    private val eventAdapter: EventListAdapter by lazy { EventListAdapter() }
    private val viewModel: EventsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun createLayout(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteBinding =
        FragmentFavoriteBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity !== null) {
            setRecyclerViews()
            observeEvents()
        }
    }

    override fun onDestroyView() {
        bind.rvEvent.adapter = null
        super.onDestroyView()
    }

    private fun setRecyclerViews() = with(bind) {
        rvEvent.apply {
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

    private fun observeEvents() {
        viewModel.getBookmarkedEvents().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                showContent(result)
            }
        }
    }

    private fun showContent(data: List<EventModel>?) = with(bind) {
        eventAdapter.submitList(data)

        if (data.isNullOrEmpty()) {
            layoutNotFound.tvMessage.text = getString(R.string.data_is_empty, "Favorite")
            layoutNotFound.tvMessage.isVisible = true
            rvEvent.isVisible = false
        } else {
            layoutNotFound.tvMessage.isVisible = false
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