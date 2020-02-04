package chat.rocket.android.server.domain.model

import chat.rocket.android.authentication.domain.model.DeepLinkInfo

data class AuthOptions(
    var state: String? = null,
    var facebookOauthUrl: String? = null,
    var githubOauthUrl: String? = null,
    var googleOauthUrl: String? = null,
    var linkedinOauthUrl: String? = null,
    var gitlabOauthUrl: String? = null,
    var wordpressOauthUrl: String? = null,
    var casLoginUrl: String? = null,
    var casToken: String? = null,
    var casServiceName: String? = null,
    var casServiceNameTextColor: Int = 0,
    var casServiceButtonColor: Int = 0,
    var customOauthUrl: String? = null,
    var customOauthServiceName: String? = null,
    var customOauthServiceNameTextColor: Int = 0,
    var customOauthServiceButtonColor: Int = 0,
    var samlUrl: String? = null,
    var samlToken: String? = null,
    var samlServiceName: String? = null,
    var samlServiceNameTextColor: Int = 0,
    var samlServiceButtonColor: Int = 0,
    var totalSocialAccountsEnabled: Int = 0,
    var isLoginFormEnabled: Boolean = false,
    var isNewAccountCreationEnabled: Boolean = false,
    var deepLinkInfo: DeepLinkInfo? = null
) {

    fun reset() {
        state = null
        facebookOauthUrl = null
        githubOauthUrl = null
        googleOauthUrl = null
        linkedinOauthUrl = null
        gitlabOauthUrl = null
        wordpressOauthUrl = null
        casLoginUrl = null
        casToken = null
        casServiceName = null
        casServiceNameTextColor = 0
        casServiceButtonColor = 0
        customOauthUrl = null
        customOauthServiceName = null
        customOauthServiceNameTextColor = 0
        customOauthServiceButtonColor = 0
        samlUrl = null
        samlToken = null
        samlServiceName = null
        samlServiceNameTextColor = 0
        samlServiceButtonColor = 0
        totalSocialAccountsEnabled = 0
        isLoginFormEnabled = false
        isNewAccountCreationEnabled = false
        deepLinkInfo = null
    }

}
