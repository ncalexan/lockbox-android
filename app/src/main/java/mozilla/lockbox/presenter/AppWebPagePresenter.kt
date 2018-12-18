/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package mozilla.lockbox.presenter

import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo
import mozilla.lockbox.action.NetworkAction
import mozilla.lockbox.flux.Dispatcher
import mozilla.lockbox.flux.Presenter
import mozilla.lockbox.log
import mozilla.lockbox.store.NetworkStore

interface WebPageView {
    var webViewObserver: Consumer<String>?
    fun loadURL(url: String)
    fun onSuccess()
    fun onError(error: String?)
}

class AppWebPagePresenter(
    val view: WebPageView,
    val url: String?,
    private val networkStore: NetworkStore = NetworkStore.shared,
    private val dispatcher: Dispatcher = Dispatcher.shared
) : Presenter() {

    override fun onViewReady() {
        checkNetworkConnection()
//        networkStore.networkState
//            .map{
//                NetworkAction.CheckConnectivity
//            }
//            .subscribe(dispatcher::dispatch)
//            .addTo(compositeDisposable)

        networkStore.networkState
            .subscribe(this::updateState)
            .addTo(compositeDisposable)

        // DEBUGGING - CONFIRMED hitting this point, but not updating view
        val state = NetworkStore.shared.isConnectedToNetwork
        log.info("ELISE WEB VIEW READY. Connected = $state")
        // DEBUGGING

        view.loadURL(url!!)
    }

    private fun updateState(state: NetworkStore.State) {
        // DEBUGGING - CONFIRMED hitting this point, but not calling view.onSucceed
        log.info("ELISE UPDATE STATE. Connected = $state")
        when (state) {
            is NetworkStore.State.Connected -> view.onSuccess()
            is NetworkStore.State.ConnectionError -> view.onError(state.error)
        }
    }

    private fun checkNetworkConnection() {
        dispatcher.dispatch(NetworkAction.CheckConnectivity)
    }
}