/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package mozilla.lockbox.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mozilla.lockbox.R
import android.webkit.WebView
import android.webkit.WebViewClient
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_fxa_login.view.*
import kotlinx.android.synthetic.main.fragment_warning.view.*
import kotlinx.android.synthetic.main.fragment_webview.*
import kotlinx.android.synthetic.main.include_backable.view.*
import mozilla.lockbox.log
import mozilla.lockbox.presenter.WebPageView
import mozilla.lockbox.presenter.AppWebPagePresenter
import mozilla.lockbox.store.NetworkStore

class AppWebPageFragment : BackableFragment(), WebPageView {
    override val networkErrorVisibility: Consumer<in NetworkStore.State>
        get() = Consumer { networkErrorVisibility(it) }

    override var webViewObserver: Consumer<String>? = null

    private var url: String? = null
    private val _isConnected: Boolean? = null

    @StringRes
    private var toolbarTitle: Int? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            url = AppWebPageFragmentArgs.fromBundle(it).url
            toolbarTitle = AppWebPageFragmentArgs.fromBundle(it).title
        }

        presenter = AppWebPagePresenter(this, url)

        var view = inflater.inflate(R.layout.fragment_webview, container, false)
        view.webView.settings.javaScriptEnabled = true
        view.toolbar.title = getString(toolbarTitle!!)

        return view
    }

    override fun loadURL(url: String) {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                webViewObserver?.accept(url)
                super.onPageStarted(view, url, favicon)
            }
        }
        webView.loadUrl(url)
    }


    private fun networkErrorVisibility(showError: NetworkStore.State) {
//        view!!.networkWarning.visibility = View.GONE
//        view!!.warningMessage.visibility = View.GONE

        log.info("ELISE - FRAGMENT -----networkErrorVisibility----")

        if(showError is NetworkStore.State.Connected){
            log.info("ELISE - FRAGMENT - no error: $showError")
            // DEBUGGING
            view!!.warningMessage.setBackgroundColor(resources.getColor(R.color.green))
            view!!.networkWarning.warningMessage.text = "SUCCESS"
            view!!.warningMessage.text = "SUCCESS"
            // DEBUGGING
        } else {
            log.info("ELISE - FRAGMENT - error: $showError")

            view!!.warningMessage.setBackgroundColor(resources.getColor(R.color.red))
            view!!.warningMessage.warningMessage.text = "FAILURE"
        }


    }

//    private fun onNetworkConnectionError(error: String?) {
//        log.error("ELISE FRAGMENT - error: $error")
//
//        val state = NetworkStore.shared.isConnectedToNetwork
//        // DEBUGGING - not hitting here
//        log.info("ELISE FRAGMENT - state = $state")
//        view!!.networkWarning.warningMessage.text = getString(R.string.no_internet_connection)
//
//        // DEBUGGING
//        view!!.warningMessage.setBackgroundColor(resources.getColor(R.color.red))
//        view!!.warningMessage.warningMessage.text = "FAILURE"
//        // DEBUGGING
//
//        view!!.networkWarning.visibility = View.VISIBLE
//    }

}
