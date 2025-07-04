!!! note "Default app links when building from source"

    If you built the app from source instead of using the APK published on GitHub, you'll need to first allow the app
    to open links by default so that you can be redirected back to the app after logging in and complete the OAuth flow.

    1. Long-press the app icon on your home screen, go to **App Info**
    2. Tap on **Open by default**
    3. Tap on **Add link** , check the `monica.teobaranga.com` link then **Add**

On the first app launch, you'll need to sign in. The app can connect to either the official website
or a self-hosted instance.

You'll need to first create an OAuth client at `{serverUrl}/settings/api`. For example, on the
official instance, you'd go to https://app.monicahq.com/settings/api. Tap on **Create New Client** and enter:

**Name**: anything that identifies the app, eg. `Android`  
**Redirect URL**: `https://monica.teobaranga.com/callback` - This is used to redirect back to the app after OAuth login
and doesn't depend on whether monica is self-hosted or official.

Once you create the client, you'll be given a Client ID and Client Secret. You'll need to enter these into the app:

**Server Address**: the URL of your Monica instance without the `api` part, e.g. https://app.monicahq.com  
**Client ID**: the OAuth Client ID, eg. `2`  
**Client Secret**: the OAuth Client Secret, a sequence of letters and numbers

Once you've entered these, tap on **Sign in** and a browser window will open where you can sign in to your
Monica account. Once you've signed in, you'll be redirected back to the app and you'll be logged in.
