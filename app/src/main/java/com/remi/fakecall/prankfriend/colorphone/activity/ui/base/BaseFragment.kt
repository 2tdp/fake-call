package com.remi.fakecall.prankfriend.colorphone.activity.ui.base

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.remi.fakecall.prankfriend.colorphone.R
import com.remi.fakecall.prankfriend.colorphone.extensions.setStatusBarTransparent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<B: ViewBinding>(val bindingFactory: (LayoutInflater) -> B): Fragment(), FragmentView, CoroutineScope {

    val binding: B by lazy { bindingFactory(layoutInflater) }

    private var baseActivity: WeakReference<BaseActivity<*>>? = null
    lateinit var job: Job
    var w = 0f

    override val coroutineContext: CoroutineContext
        get() = getDispatchers() + job

    protected abstract fun setUp()

    protected open fun getDispatchers(): CoroutineContext{
        return Dispatchers.IO + job
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        job = Job()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = true
        w = resources.displayMetrics.widthPixels / 100F
        setUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*>) baseActivity = WeakReference(context)
    }

    override fun onDetach() {
        super.onDetach()
        baseActivity?.let {
            it.get()?.onFragmentDetach(this)
            it.clear()
        }
    }

    override fun refresh() {
        for (fragment in childFragmentManager.fragments) {
            if (fragment is BaseFragment<*>) fragment.refresh()
        }
    }

    fun openSettingPermission(action: String) {
        val intent = Intent(action)
        val uri = Uri.fromParts("package", baseActivity?.get()?.packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun startIntent(nameActivity: String, isFinish: Boolean) {
        baseActivity?.get()?.startIntent(nameActivity, isFinish)
    }

    override fun showLoading() {
        baseActivity?.get()?.showLoading()
    }

    override fun showLoading(cancelable: Boolean) {
        baseActivity?.get()?.showLoading(cancelable)
    }

    override fun hideLoading() {
        baseActivity?.get()?.hideLoading()
    }

    override fun showMessage(@StringRes stringId: Int) {
        baseActivity?.get()?.showMessage(stringId)
    }

    override fun showMessage(msg: String) {
        baseActivity?.get()?.showMessage(msg)
    }

    override fun showPopupMessage(msg: String, title: String) {
        baseActivity?.get()?.showPopupMessage(msg, title)
    }

    override fun showPopupMessage(msg: String) {
        baseActivity?.get()?.showPopupMessage(msg)
    }

    open fun getBaseActivity(): BaseActivity<*>? {
        return baseActivity?.get()
    }

    override fun hideKeyboard() {
        baseActivity?.get()?.hideKeyboard()
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        isAdd: Boolean,
        addBackStack: Boolean
    ) {
        try {
            (activity as BaseActivity<*>).replaceFragment(manager, fragment, isAdd, addBackStack)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(
        fragment: Fragment,
        layout: Int,
        isAdd: Boolean,
        addBackStack: Boolean
    ) {
        try {
            (activity as BaseActivity<*>).replaceFragment(fragment, layout, isAdd, addBackStack)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(fragment: Fragment, isAdd: Boolean, addBackStack: Boolean) {
        try {
            (activity as BaseActivity<*>).replaceFragment(fragment, isAdd, addBackStack)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(fragment: Fragment, layout: Int, addBackStack: Boolean) {
        try {
            (activity as BaseActivity<*>).replaceFragment(fragment, layout, addBackStack)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(fragment: Fragment, addBackStack: Boolean) {
        try {
            (activity as BaseActivity<*>).replaceFragment(fragment, addBackStack)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        layout: Int,
        isAdd: Boolean,
        addBackStack: Boolean
    ) {
        try {
            (activity as BaseActivity<*>).replaceFragment(
                manager,
                fragment,
                layout,
                isAdd,
                addBackStack
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        isAdd: Boolean,
        addBackStack: Boolean,
        addAnimation: Boolean
    ) {
        try {
            (activity as BaseActivity<*>).replaceFragment(
                manager,
                fragment,
                isAdd,
                addBackStack,
                enter,
                exit,
                popEnter,
                popExit
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        res: Int,
        isAdd: Boolean,
        addBackStack: Boolean,
        addAnimation: Boolean
    ) {
        try {
            (activity as BaseActivity<*>).replaceFragment(
                manager,
                fragment,
                res,
                isAdd,
                addBackStack,
                enter,
                exit,
                popEnter,
                popExit
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun replaceFragment(
        manager: FragmentManager,
        fragment: Fragment,
        res: Int,
        isAdd: Boolean,
        addBackStack: Boolean,
        shareElement: View,
        transitionName: String
    ) {
        try {
            (activity as BaseActivity<*>).replaceFragment(
                manager,
                fragment,
                res,
                isAdd,
                addBackStack,
                shareElement,
                transitionName
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun popBackStack(manager: FragmentManager) {
        try {
            (activity as BaseActivity<*>).popBackStack(manager)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun popBackStack() {
        try {
            (activity as BaseActivity<*>).popBackStack()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun popBackStack(manager: FragmentManager, tag: String) {
        try {
            (activity as BaseActivity<*>).popBackStack(
                manager,
                tag,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun popBackStack(tag: String) {
        try {
            (activity as BaseActivity<*>).popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun clearBackStack() {
        try {
            (activity as BaseActivity<*>).clearBackStack()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        //animation
        const val res = android.R.id.content
        val enter: Int = R.anim.slide_in_right
        val exit: Int = R.anim.slide_out_left
        val popEnter: Int = R.anim.slide_in_left_small
        val popExit: Int = R.anim.slide_out_right

        val enterBottom = R.anim.slide_up_in
        val exitBottom = R.anim.slide_down_out
        val popEnterBottom = R.anim.slide_up_in_small
        val popExitBottom = R.anim.slide_down_out_small
    }
}