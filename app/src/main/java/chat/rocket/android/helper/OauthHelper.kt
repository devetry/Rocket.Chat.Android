package chat.rocket.android.helper

import chat.rocket.android.util.extensions.removeTrailingSlash

object OauthHelper {

    /**
     * Returns the Drupal Oauth URL.
     *
     * @param clientId The Drupal client ID.
     * @param state An unguessable random string used to protect against forgery attacks.
     * @return The Drupal Oauth URL.
     */
    fun getGithubOauthUrl(clientId: String, state: String): String {
        val rocketchat = "http://13.126.45.178:3000"
        val drupal = "http://13.126.45.178"
        val rocketchatClientId = "F9D848A32AA9C6552E1AB7F90C03B749FBF1300B"

        return "$drupal/en/oauth2/authorize?destination=oauth2/authorize" +
                "&client_id=$rocketchatClientId" +
                "&redirect_uri=$rocketchat/_oauth/drupal?close" +
                "&response_type=code" +
                "&scope=gender%20email%20openid%20profile%20offline_access" +
                "&state=$state"
    }

    /**
     * Returns the Google Oauth URL.
     *
     * @param clientId The Google client ID.
     * @param serverUrl The server URL.
     * @param state An unguessable random string used to protect against forgery attacks.
     * @return The Google Oauth URL.
     */
    fun getGoogleOauthUrl(clientId: String, serverUrl: String, state: String): String {
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=$clientId" +
                "&redirect_uri=${serverUrl.removeTrailingSlash()}/_oauth/google?close" +
                "&state=$state" +
                "&response_type=code" +
                "&scope=email%20profile"
    }

    /**
     * Returns the Linkedin Oauth URL.
     *
     * @param clientId The Linkedin client ID.
     * @param serverUrl The server URL.
     * @param state An unguessable random string used to protect against forgery attacks.
     * @return The Linkedin Oauth URL.
     */
    fun getLinkedinOauthUrl(clientId: String, serverUrl: String, state: String): String {
        return "https://linkedin.com/oauth/v2/authorization" +
                "?client_id=$clientId" +
                "&redirect_uri=${serverUrl.removeTrailingSlash()}/_oauth/linkedin?close" +
                "&state=$state" +
                "&response_type=code"
    }

    /**
     * Returns the Gitlab Oauth URL.
     *
     * @param clientId The Gitlab client ID.
     * @param serverUrl The server URL.
     * @param state An unguessable random string used to protect against forgery attacks.
     * @return The Gitlab Oauth URL.
     */
    fun getGitlabOauthUrl(clientId: String, serverUrl: String, state: String): String {
        return  "https://gitlab.com/oauth/authorize" +
                "?client_id=$clientId" +
                "&redirect_uri=${serverUrl.removeTrailingSlash()}/_oauth/gitlab?close" +
                "&state=$state" +
                "&response_type=code" +
                "&scope=read_user"
    }
}