package com.id22.dicodingevent.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<ActivityBinding : ViewBinding> : AppCompatActivity() {

    private var _bind: ActivityBinding? = null
    protected val bind get() = _bind!!

    protected abstract fun onCreateBind(): ActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = onCreateBind()
        setContentView(bind.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
    }
}
