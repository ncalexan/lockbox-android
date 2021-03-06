package mozilla.lockbox.view

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_autofill_onboarding.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mozilla.lockbox.R
import mozilla.lockbox.presenter.AutofillOnboardingPresenter
import mozilla.lockbox.presenter.AutofillOnboardingView

@TargetApi(Build.VERSION_CODES.O)
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalCoroutinesApi
class AutofillOnboardingFragment : Fragment(), AutofillOnboardingView {

    override val onSkipClick: Observable<Unit>
        get() = view!!.skipButton.clicks()

    override val onGoToSettingsClick: Observable<Unit>
        get() = view!!.goToSettings.clicks()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = AutofillOnboardingPresenter(this)
        return inflater.inflate(R.layout.fragment_autofill_onboarding, container, false)
    }
}