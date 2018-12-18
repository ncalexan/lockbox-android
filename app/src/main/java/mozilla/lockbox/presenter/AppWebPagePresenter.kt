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
import mozilla.lockbox.store.NetworkStore

interface WebPageView {
    var webViewObserver: Consumer<String>?
    var networkObserver: Consumer<Boolean>
    fun loadURL(url: String)
    fun onSucceeded()
    fun onError(error: String?)
}

class AppWebPagePresenter(
    val view: WebPageView,
    val url: String?,
    private val networkStore: NetworkStore = NetworkStore.shared,
    private val dispatcher: Dispatcher = Dispatcher.shared
) : Presenter() {


    private val networkConnection: Consumer<Boolean>
        get() = Consumer { connected ->
            if (connected) {
                dispatcher.dispatch(NetworkAction.CheckConnectivity)
            } else {
                // dispatcher.dispatch(NetworkAction.UnlockWithFingerprint(false))
                // throw specific error
            }
        }



    override fun onViewReady() {
        view.networkObserver = networkConnection
        networkStore.networkState
            .subscribe(this::updateState)
            .addTo(compositeDisposable)

        view.loadURL(url!!)
    }

    private fun updateState(state: NetworkStore.State) {
        when (state) {
            is NetworkStore.State.Connected -> view.onSucceeded()
            is NetworkStore.State.ConnectionError -> view.onError(state.error)
        }
    }
}