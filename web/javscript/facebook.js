window.fbAsyncInit = function() {
    FB.init({
        appId      : '628194800859666',
        cookie     : true,
        xfbml      : true,
        version    : 'v3.0'
    });

    FB.AppEvents.logPageView();
    // ADD ADDITIONAL FACEBOOK CODE HERE
};

// (function(d, s, id){
//     var js, fjs = d.getElementsByTagName(s)[0];
//     if (d.getElementById(id)) {return;}
//     js = d.createElement(s); js.id = id;
//     js.src = "https://connect.facebook.net/en_US/sdk.js";
//     fjs.parentNode.insertBefore(js, fjs);
// }(document, 'script', 'facebook-jssdk'));

function onLogin(response) {
    if (response.status === 'connected') {
        FB.api('/me',{fields: 'name, email, birthday, posts'},  function(data) {
            $("#login-btn").html("Hello, " + data.name);//TODO make this to be a link to logout;
            //TODO send name, email, birthday to GAE
            showPosts(data.posts.data);
        });
    }
    else {
        var welcomeBlock = document.getElementById('fb-welcome');
        welcomeBlock.innerHTML = 'Cant get data ' + response.status + '!';}
}

function checkLoginState()  {
    FB.getLoginStatus(function (response) {
        // Check login status on load, and if the user is
        // already logged in, go directly to the welcome message.
        if (response.status === 'connected') {
            onLogin(response);
        } else {
            // Otherwise, show Login dialog first.
            FB.login(function (response) {
                onLogin(response);

            }, {scope: 'public_profile, email, user_birthday'});
        }
    });
}
function showPosts(postsData){
    for (var i = 0; i < postsData.length; i++){
        retrieveMessage(postsData[i].id);
    }
}

function retrieveMessage(id){
    var message;
    FB.api('/' + id, function (data) {
        if (data && !data.error) {
            if (data.message === undefined){
                message = data.story;
            } else {
                message = data.message;
            }
            $('#my-posts').append("<p>" + message + "<br>@ " + data.created_time + "</p>");
        }
    })
}