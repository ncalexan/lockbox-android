package mozilla.lockbox.store

import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import mozilla.lockbox.action.NetworkAction
import mozilla.lockbox.extensions.filterByType
import mozilla.lockbox.flux.Dispatcher
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject
import mozilla.lockbox.R
import mozilla.lockbox.log

open class NetworkStore(
    val dispatcher: Dispatcher = Dispatcher.shared
) : ContextStore {

    open lateinit var connectivityManager: ConnectivityManager
    open lateinit var NETWORK_WARNING_MESSAGE: String
    internal val compositeDisposable = CompositeDisposable()

    private val stateSubject: ReplaySubject<NetworkStore.State> = ReplaySubject.createWithSize(1)
    open val networkAvailable: Observable<NetworkStore.State> get() = stateSubject

    // MAKE ME PRIVATE
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
        dispatcher.register
            .filterByType(NetworkAction::class.java)
            .subscribe {
                when (it) {
                    is NetworkAction.CheckConnectivity -> {
                        checkConnectivity()
                    }
                }
            }
            .addTo(compositeDisposable)

//        networkAvailable.subscribe {
//            state ->
//            when(state) {
//                is State.Connected ->
//                is State.ConnectionError ->
//                else -> log.error ("Unexpected error.")
//            }
//        }.addTo(compositeDisposable)
    }

    override fun injectContext(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        NETWORK_WARNING_MESSAGE = context.getString(R.string.networkWarningMessage)
    }

    private fun checkConnectivity() {
        if (isConnectedToNetwork){
            log.info("ELISE - STORE - SUCCESS: connected to network")
            stateSubject.onNext(State.Connected)
        }
        else {
            log.info("ELISE - STORE - ERROR: not connected to network")

            // error
            stateSubject.onNext(State.ConnectionError(NETWORK_WARNING_MESSAGE))
        }
    }


}