package mozilla.lockbox.store

import android.accounts.NetworkErrorException
import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import mozilla.lockbox.action.NetworkAction
import mozilla.lockbox.extensions.filterByType
import mozilla.lockbox.flux.Dispatcher
import io.reactivex.disposables.CompositeDisposable
import mozilla.lockbox.R
import mozilla.lockbox.log

open class NetworkStore(
    val dispatcher: Dispatcher = Dispatcher.shared
) : ContextStore {

    open lateinit var connectivityManager: ConnectivityManager
    internal val compositeDisposable = CompositeDisposable()
    open lateinit var NETWORK_WARNING_MESSAGE: String

    private val _state: PublishSubject<NetworkStore.State> = PublishSubject.create()
    open val networkState: Observable<NetworkStore.State> get() = _state


    open val isConnectedToNetwork: Boolean
        get() = connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true


    companion object {
        val shared = NetworkStore()
    }

    sealed class State {
        object Connected : State()
        data class ConnectionError(val error: String? = null) : State()
    }

    init {

        dispatcher.register.filterByType(NetworkAction::class.java)
            .filter { isConnectedToNetwork }
            .subscribe {
                when (it) {
                    is NetworkAction.CheckConnectivity -> {
                        checkConnectivity()
                        log.info("ELISE NETWORK STORE: get network action")
                    }
                }
            }
            .addTo(compositeDisposable)
    }

    override fun injectContext(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        NETWORK_WARNING_MESSAGE = context.getString(R.string.networkWarningMessage)
    }

    private fun checkConnectivity() {
        if (isConnectedToNetwork){
            log.info("ELISE NETWORK STORE: CONNECTED TO NETWORK")
            _state.onNext(State.Connected)
        }
        else {
            log.info("ELISE NETWORK STORE ERROR: NOT CONNECTED TO NETWORK")

            // error
            _state.onNext(State.ConnectionError(NETWORK_WARNING_MESSAGE))
        }
    }


}