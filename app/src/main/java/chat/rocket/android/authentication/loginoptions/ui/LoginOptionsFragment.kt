package chat.rocket.android.authentication.loginoptions.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import chat.rocket.android.R
import chat.rocket.android.analytics.AnalyticsManager
import chat.rocket.android.analytics.event.ScreenViewEvent
import chat.rocket.android.authentication.domain.model.DeepLinkInfo
import chat.rocket.android.authentication.loginoptions.presentation.LoginOptionsPresenter
import chat.rocket.android.authentication.loginoptions.presentation.LoginOptionsView
import chat.rocket.android.authentication.ui.AuthenticationActivity
import chat.rocket.android.server.domain.model.AuthOptions
import chat.rocket.android.util.extensions.*
import chat.rocket.android.webview.oauth.ui.INTENT_OAUTH_CREDENTIAL_SECRET
import chat.rocket.android.webview.oauth.ui.INTENT_OAUTH_CREDENTIAL_TOKEN
import chat.rocket.android.webview.oauth.ui.oauthWebViewIntent
import chat.rocket.android.webview.sso.ui.INTENT_SSO_TOKEN
import chat.rocket.android.webview.sso.ui.ssoWebViewIntent
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_authentication_login_options.*
import timber.log.Timber
import javax.inject.Inject

private const val SERVER_NAME = "server_name"
private const val STATE = "state"
private const val FACEBOOK_OAUTH_URL = "facebook_oauth_url"
private const val GITHUB_OAUTH_URL = "github_oauth_url"
private const val GOOGLE_OAUTH_URL = "google_oauth_url"
private const val LINKEDIN_OAUTH_URL = "linkedin_oauth_url"
private const val GITLAB_OAUTH_URL = "gitlab_oauth_url"
private const val WORDPRESS_OAUTH_URL = "wordpress_oauth_url"
private const val CAS_LOGIN_URL = "cas_login_url"
private const val CAS_TOKEN = "cas_token"
private const val CAS_SERVICE_NAME = "cas_service_name"
private const val CAS_SERVICE_NAME_TEXT_COLOR = "cas_service_name_text_color"
private const val CAS_SERVICE_BUTTON_COLOR = "cas_service_button_color"
private const val CUSTOM_OAUTH_URL = "custom_oauth_url"
private const val CUSTOM_OAUTH_SERVICE_NAME = "custom_oauth_service_name"
private const val CUSTOM_OAUTH_SERVICE_NAME_TEXT_COLOR = "custom_oauth_service_name_text_color"
private const val CUSTOM_OAUTH_SERVICE_BUTTON_COLOR = "custom_oauth_service_button_color"
private const val SAML_URL = "saml_url"
private const val SAML_TOKEN = "saml_token"
private const val SAML_SERVICE_NAME = "saml_service_name"
private const val SAML_SERVICE_NAME_TEXT_COLOR = "saml_service_name_text_color"
private const val SAML_SERVICE_BUTTON_COLOR = "saml_service_button_color"
private const val TOTAL_SOCIAL_ACCOUNTS = "total_social_accounts"
private const val IS_LOGIN_FORM_ENABLED = "is_login_form_enabled"
private const val IS_NEW_ACCOUNT_CREATION_ENABLED = "is_new_account_creation_enabled"

internal const val REQUEST_CODE_FOR_OAUTH = 1
internal const val REQUEST_CODE_FOR_CAS = 2
internal const val REQUEST_CODE_FOR_SAML = 3

private const val DEFAULT_ANIMATION_DURATION = 400L

fun newInstance(
    serverName: String,
    authOptions: AuthOptions
): Fragment = LoginOptionsFragment().apply {
    arguments = Bundle(23).apply {
        putString(SERVER_NAME, serverName)
        putString(STATE, authOptions.state)
        putString(FACEBOOK_OAUTH_URL, authOptions.facebookOauthUrl)
        putString(GITHUB_OAUTH_URL, authOptions.githubOauthUrl)
        putString(GOOGLE_OAUTH_URL, authOptions.googleOauthUrl)
        putString(LINKEDIN_OAUTH_URL, authOptions.linkedinOauthUrl)
        putString(GITLAB_OAUTH_URL, authOptions.gitlabOauthUrl)
        putString(WORDPRESS_OAUTH_URL, authOptions.wordpressOauthUrl)
        putString(CAS_LOGIN_URL, authOptions.casLoginUrl)
        putString(CAS_TOKEN, authOptions.casToken)
        putString(CAS_SERVICE_NAME, authOptions.casServiceName)
        putInt(CAS_SERVICE_NAME_TEXT_COLOR, authOptions.casServiceNameTextColor)
        putInt(CAS_SERVICE_BUTTON_COLOR, authOptions.casServiceButtonColor)
        putString(CUSTOM_OAUTH_URL, authOptions.customOauthUrl)
        putString(CUSTOM_OAUTH_SERVICE_NAME, authOptions.customOauthServiceName)
        putInt(CUSTOM_OAUTH_SERVICE_NAME_TEXT_COLOR, authOptions.customOauthServiceNameTextColor)
        putInt(CUSTOM_OAUTH_SERVICE_BUTTON_COLOR, authOptions.customOauthServiceButtonColor)
        putString(SAML_URL, authOptions.samlUrl)
        putString(SAML_TOKEN, authOptions.samlToken)
        putString(SAML_SERVICE_NAME, authOptions.samlServiceName)
        putInt(SAML_SERVICE_NAME_TEXT_COLOR, authOptions.samlServiceNameTextColor)
        putInt(SAML_SERVICE_BUTTON_COLOR, authOptions.samlServiceButtonColor)
        putInt(TOTAL_SOCIAL_ACCOUNTS, authOptions.totalSocialAccountsEnabled)
        putBoolean(IS_LOGIN_FORM_ENABLED, authOptions.isLoginFormEnabled)
        putBoolean(IS_NEW_ACCOUNT_CREATION_ENABLED, authOptions.isNewAccountCreationEnabled)
        putParcelable(
            chat.rocket.android.authentication.domain.model.DEEP_LINK_INFO_KEY,
            authOptions.deepLinkInfo
        )
    }
}

class LoginOptionsFragment : Fragment(), LoginOptionsView {
    @Inject
    lateinit var presenter: LoginOptionsPresenter
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    private var serverName: String? = null
    private val authOptions: AuthOptions = AuthOptions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

        arguments?.run {
            serverName = getString(SERVER_NAME)
            authOptions.state = getString(STATE)
            authOptions.facebookOauthUrl = getString(FACEBOOK_OAUTH_URL)
            authOptions.githubOauthUrl = getString(GITHUB_OAUTH_URL)
            authOptions.googleOauthUrl = getString(GOOGLE_OAUTH_URL)
            authOptions.linkedinOauthUrl = getString(LINKEDIN_OAUTH_URL)
            authOptions.gitlabOauthUrl = getString(GITLAB_OAUTH_URL)
            authOptions.wordpressOauthUrl = getString(WORDPRESS_OAUTH_URL)
            authOptions.casLoginUrl = getString(CAS_LOGIN_URL)
            authOptions.casToken = getString(CAS_TOKEN)
            authOptions.casServiceName = getString(CAS_SERVICE_NAME)
            authOptions.casServiceNameTextColor = getInt(CAS_SERVICE_NAME_TEXT_COLOR)
            authOptions.casServiceButtonColor = getInt(CAS_SERVICE_BUTTON_COLOR)
            authOptions.customOauthUrl = getString(CUSTOM_OAUTH_URL)
            authOptions.customOauthServiceName = getString(CUSTOM_OAUTH_SERVICE_NAME)
            authOptions.customOauthServiceNameTextColor = getInt(CUSTOM_OAUTH_SERVICE_NAME_TEXT_COLOR)
            authOptions.customOauthServiceButtonColor = getInt(CUSTOM_OAUTH_SERVICE_BUTTON_COLOR)
            authOptions.samlUrl = getString(SAML_URL)
            authOptions.samlToken = getString(SAML_TOKEN)
            authOptions.samlServiceName = getString(SAML_SERVICE_NAME)
            authOptions.samlServiceNameTextColor = getInt(SAML_SERVICE_NAME_TEXT_COLOR)
            authOptions.samlServiceButtonColor = getInt(SAML_SERVICE_BUTTON_COLOR)
            authOptions.totalSocialAccountsEnabled = getInt(TOTAL_SOCIAL_ACCOUNTS)
            authOptions.isLoginFormEnabled = getBoolean(IS_LOGIN_FORM_ENABLED)
            authOptions.isNewAccountCreationEnabled = getBoolean(IS_NEW_ACCOUNT_CREATION_ENABLED)
            authOptions.deepLinkInfo =
                getParcelable(chat.rocket.android.authentication.domain.model.DEEP_LINK_INFO_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = container?.inflate(R.layout.fragment_authentication_login_options)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupAccounts()
        analyticsManager.logScreenView(ScreenViewEvent.LoginOptions)
        authOptions.deepLinkInfo?.let { presenter.authenticateWithDeepLink(it) }
    }

    private fun setupToolbar() {
        with(activity as AuthenticationActivity) {
            this.clearLightStatusBar()
            toolbar.isVisible = true
            toolbar.title = serverName?.replace(getString(R.string.default_protocol), "")
        }
    }

    private fun setupAccounts() {
        setupSocialAccounts()
        setupCas()
        setupCustomOauth()
        setupSaml()
        setupAccountsView()
        setupLoginWithEmailView()
        setupCreateNewAccountView()
    }

    private fun setupSocialAccounts() {
        if (authOptions.facebookOauthUrl != null && authOptions.state != null) {
            setupFacebookButtonListener(authOptions.facebookOauthUrl.toString(), authOptions.state.toString())
            enableLoginByFacebook()
        }

        if (authOptions.githubOauthUrl != null && authOptions.state != null) {
            setupGithubButtonListener(authOptions.githubOauthUrl.toString(), authOptions.state.toString())
            enableLoginByGithub()
        }

        if (authOptions.googleOauthUrl != null && authOptions.state != null) {
            setupGoogleButtonListener(authOptions.googleOauthUrl.toString(), authOptions.state.toString())
            enableLoginByGoogle()
        }

        if (authOptions.linkedinOauthUrl != null && authOptions.state != null) {
            setupLinkedinButtonListener(authOptions.linkedinOauthUrl.toString(), authOptions.state.toString())
            enableLoginByLinkedin()
        }

        if (authOptions.gitlabOauthUrl != null && authOptions.state != null) {
            setupGitlabButtonListener(authOptions.gitlabOauthUrl.toString(), authOptions.state.toString())
            enableLoginByGitlab()
        }

        if (authOptions.wordpressOauthUrl != null && authOptions.state != null) {
            setupWordpressButtonListener(authOptions.wordpressOauthUrl.toString(), authOptions.state.toString())
            enableLoginByWordpress()
        }
    }

    private fun setupCas() {
        if (authOptions.casLoginUrl != null && authOptions.casToken != null && authOptions.casServiceName != null) {
            addCasButton(
                authOptions.casLoginUrl.toString(),
                authOptions.casToken.toString(),
                authOptions.casServiceName.toString(),
                authOptions.casServiceNameTextColor,
                authOptions.casServiceButtonColor
            )
        }
    }

    private fun setupCustomOauth() {
        if (authOptions.customOauthUrl != null && authOptions.state != null && authOptions.customOauthServiceName != null) {
            addCustomOauthButton(
                authOptions.customOauthUrl.toString(),
                authOptions.state.toString(),
                authOptions.customOauthServiceName.toString(),
                authOptions.customOauthServiceNameTextColor,
                authOptions.customOauthServiceButtonColor
            )
        }
    }

    private fun setupSaml() {
        if (authOptions.samlUrl != null && authOptions.samlToken != null && authOptions.samlServiceName != null) {
            addSamlButton(
                authOptions.samlUrl.toString(),
                authOptions.samlToken.toString(),
                authOptions.samlServiceName.toString(),
                authOptions.samlServiceNameTextColor,
                authOptions.samlServiceButtonColor
            )
        }
    }

    private fun setupAccountsView() {
        if (authOptions.totalSocialAccountsEnabled > 0) {
            showAccountsView()
            if (authOptions.totalSocialAccountsEnabled > 3) {
                setupExpandAccountsView()
            }
        }
    }

    private fun setupLoginWithEmailView() {
        if (authOptions.isLoginFormEnabled) {
            showLoginWithEmailButton()
        }
    }

    private fun setupCreateNewAccountView() {
        if (authOptions.isNewAccountCreationEnabled) {
            showCreateNewAccountButton()
        }
    }

    // OAuth Accounts.
    override fun enableLoginByFacebook() = enableAccountButton(button_facebook)

    override fun setupFacebookButtonListener(facebookOauthUrl: String, state: String) =
        setupButtonListener(button_facebook, facebookOauthUrl, state, REQUEST_CODE_FOR_OAUTH)

    override fun enableLoginByGithub() = enableAccountButton(button_github)

    override fun setupGithubButtonListener(githubUrl: String, state: String) =
        setupButtonListener(button_github, githubUrl, state, REQUEST_CODE_FOR_OAUTH)

    override fun enableLoginByGoogle() = enableAccountButton(button_google)

    override fun setupGoogleButtonListener(googleUrl: String, state: String) =
        setupButtonListener(button_google, googleUrl, state, REQUEST_CODE_FOR_OAUTH)

    override fun enableLoginByLinkedin() = enableAccountButton(button_linkedin)

    override fun setupLinkedinButtonListener(linkedinUrl: String, state: String) =
        setupButtonListener(button_linkedin, linkedinUrl, state, REQUEST_CODE_FOR_OAUTH)

    override fun enableLoginByGitlab() = enableAccountButton(button_gitlab)

    override fun setupGitlabButtonListener(gitlabUrl: String, state: String) =
        setupButtonListener(button_gitlab, gitlabUrl, state, REQUEST_CODE_FOR_OAUTH)

    override fun enableLoginByWordpress() = enableAccountButton(button_wordpress)

    override fun setupWordpressButtonListener(wordpressUrl: String, state: String) =
        setupButtonListener(button_wordpress, wordpressUrl, state, REQUEST_CODE_FOR_OAUTH)

    // CAS service account.
    override fun addCasButton(
        caslUrl: String,
        casToken: String,
        serviceName: String,
        serviceNameColor: Int,
        buttonColor: Int
    ) {
        val button = getCustomServiceButton(serviceName, serviceNameColor, buttonColor)
        setupButtonListener(button, caslUrl, casToken, REQUEST_CODE_FOR_CAS)
        accounts_container.addView(button)
    }

    // Custom OAuth account.
    override fun addCustomOauthButton(
        customOauthUrl: String,
        state: String,
        serviceName: String,
        serviceNameColor: Int,
        buttonColor: Int
    ) {
        val button = getCustomServiceButton(serviceName, serviceNameColor, buttonColor)
        setupButtonListener(button, customOauthUrl, state, REQUEST_CODE_FOR_OAUTH)
        accounts_container.addView(button)
    }

    // SAML account.
    override fun addSamlButton(
        samlUrl: String,
        samlToken: String,
        serviceName: String,
        serviceNameColor: Int,
        buttonColor: Int
    ) {
        val button = getCustomServiceButton(serviceName, serviceNameColor, buttonColor)
        setupButtonListener(button, samlUrl, samlToken, REQUEST_CODE_FOR_SAML)
        accounts_container.addView(button)
    }

    override fun showAccountsView() {
        ui {
            showThreeAccountsMethods()
            accounts_container.isVisible = true
        }
    }

    override fun setupExpandAccountsView() {
        ui {
            expand_more_accounts_container.isVisible = true
            var isAccountsCollapsed = true
            button_expand_collapse_accounts.setOnClickListener {
                isAccountsCollapsed = if (isAccountsCollapsed) {
                    button_expand_collapse_accounts.rotateBy(180F, DEFAULT_ANIMATION_DURATION)
                    expandAccountsView()
                    false
                } else {
                    button_expand_collapse_accounts.rotateBy(180F, DEFAULT_ANIMATION_DURATION)
                    collapseAccountsView()
                    true
                }
            }
        }
    }

    override fun showLoginWithEmailButton() {
        ui {
            button_login_with_email.setOnClickListener { presenter.toLoginWithEmail() }
            button_login_with_email.isVisible = true
        }
    }

    override fun showCreateNewAccountButton() {
        ui {
            button_create_an_account.setOnClickListener { presenter.toCreateAccount() }
            button_create_an_account.isVisible = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_FOR_OAUTH -> {
                    presenter.authenticateWithOauth(
                        data.getStringExtra(INTENT_OAUTH_CREDENTIAL_TOKEN),
                        data.getStringExtra(INTENT_OAUTH_CREDENTIAL_SECRET)
                    )
                }
                REQUEST_CODE_FOR_CAS -> presenter.authenticateWithCas(
                    data.getStringExtra(INTENT_SSO_TOKEN)
                )
                REQUEST_CODE_FOR_SAML -> data.apply {
                    presenter.authenticateWithSaml(getStringExtra(INTENT_SSO_TOKEN))
                }
            }
        }
    }

    override fun showLoading() {
        ui {
            view_loading.isVisible = true
        }
    }

    override fun hideLoading() {
        ui {
            view_loading.isVisible = false
        }
    }

    override fun showMessage(resId: Int) {
        ui {
            showToast(resId)
        }
    }

    override fun showMessage(message: String) {
        ui {
            showToast(message)
        }
    }

    override fun showGenericErrorMessage() {
        showMessage(R.string.msg_generic_error)
    }

    private fun enableAccountButton(button: Button) {
        ui {
            button.isClickable = true
        }
    }

    private fun setupButtonListener(
        button: Button,
        accountUrl: String,
        argument: String,
        requestCode: Int
    ) {
        ui { activity ->
            button.setOnClickListener {
                when (requestCode) {
                    REQUEST_CODE_FOR_OAUTH -> startActivityForResult(
                        activity.oauthWebViewIntent(accountUrl, argument), REQUEST_CODE_FOR_OAUTH
                    )
                    REQUEST_CODE_FOR_CAS -> startActivityForResult(
                        activity.ssoWebViewIntent(accountUrl, argument), REQUEST_CODE_FOR_CAS
                    )
                    REQUEST_CODE_FOR_SAML -> startActivityForResult(
                        activity.ssoWebViewIntent(accountUrl, argument), REQUEST_CODE_FOR_SAML
                    )
                }

                activity.overridePendingTransition(R.anim.slide_up, R.anim.hold)
            }
        }
    }

    /**
     * Gets a stylized custom service button.
     */
    private fun getCustomServiceButton(
        buttonText: String,
        buttonTextColor: Int,
        buttonBgColor: Int
    ): Button {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val marginTop = resources.getDimensionPixelSize(R.dimen.button_account_margin_top)
        params.setMargins(0, marginTop, 0, 0)

        val button = Button(
            ContextThemeWrapper(context, R.style.Authentication_Button),
            null,
            R.style.Authentication_Button
        )
        button.layoutParams = params
        button.text = buttonText
        button.setTextColor(buttonTextColor)
        button.background.setColorFilter(buttonBgColor, PorterDuff.Mode.MULTIPLY)

        return button
    }

    private fun showThreeAccountsMethods() {
        (0..accounts_container.childCount)
            .mapNotNull { accounts_container.getChildAt(it) as? Button }
            .filter { it.isClickable }
            .take(3)
            .forEach { it.isVisible = true }
    }

    private fun expandAccountsView() {
        val buttons = (0..accounts_container.childCount)
            .mapNotNull { accounts_container.getChildAt(it) as? Button }
            .filter { it.isClickable && !it.isVisible }
        val optionHeight = accounts_container.getChildAt(1).height +
            accounts_container.getChildAt(1).marginTop
        val collapsedHeight = accounts_container.height
        val expandedHeight = collapsedHeight + optionHeight * buttons.size

        with(ValueAnimator.ofInt(collapsedHeight, expandedHeight)) {
            addUpdateListener {
                val params = accounts_container.layoutParams
                params.height = animatedValue as Int
                accounts_container.layoutParams = params
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    buttons.forEach {
                        it.isVisible = true
                        val anim = AlphaAnimation(0.0f, 1.0f)
                        anim.duration = DEFAULT_ANIMATION_DURATION
                        it.startAnimation(anim)
                    }
                }
            })
            setDuration(DEFAULT_ANIMATION_DURATION).start()
        }
    }

    private fun collapseAccountsView() {
        val buttons = (0..accounts_container.childCount)
            .mapNotNull { accounts_container.getChildAt(it) as? Button }
            .filter { it.isClickable && it.isVisible }
            .drop(3)
        val optionHeight = accounts_container.getChildAt(1).height +
            accounts_container.getChildAt(1).marginTop
        val expandedHeight = accounts_container.height
        val collapsedHeight = expandedHeight - optionHeight * buttons.size

        with(ValueAnimator.ofInt(expandedHeight, collapsedHeight)) {
            addUpdateListener {
                val params = accounts_container.layoutParams
                params.height = animatedValue as Int
                accounts_container.layoutParams = params
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animator: Animator) {
                    buttons.forEach {
                        val anim = AlphaAnimation(1.0f, 0.0f)
                        anim.duration = DEFAULT_ANIMATION_DURATION
                        anim.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {
                                Timber.d("Animation starts: $animation")
                            }

                            override fun onAnimationEnd(animation: Animation) {
                                it.isVisible = false
                            }

                            override fun onAnimationRepeat(animation: Animation) {
                                Timber.d("Animation repeats: $animation")
                            }
                        })
                        it.startAnimation(anim)
                    }
                }
            })
            setDuration(DEFAULT_ANIMATION_DURATION).start()
        }
    }
}
