package com.id22.dicodingevent.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<FragmentBinding : ViewBinding> : Fragment() {

    private var _bind: FragmentBinding? = null
    protected val bind get() = _bind!!

    protected abstract fun createLayout(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _bind = createLayout(inflater, container)
        return bind.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }
}
