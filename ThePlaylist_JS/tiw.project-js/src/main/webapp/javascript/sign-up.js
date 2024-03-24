(function() {
	if(sessionStorage.getItem("userName") !== null){
		window.location.href = "ThePlaylist.html";
		return;
	}
    document.getElementById("sign-up").addEventListener("submit", (e) =>{
        e.preventDefault();
        let form = e.target;

        if(form.checkValidity() === true){
            document.getElementById("error").textContent = null;

            let userName = form.userName.value;
            let password = form.password.value;

            if(userName.length > 50 || password.length > 50){
                document.getElementById("error").textContent = "Username or password too long";
                return;
            }else{
                makeCall("POST", "SignUp", form, function (res) {
                    if(res.readyState === XMLHttpRequest.DONE){
                        let message = res.responseText;
                        switch(res.status){
						case 200: 
	                        sessionStorage.setItem("userName", message);
	                        window.location.href = "ThePlaylist.html";
	                        break;
	                    case 403:
							sessionStorage.setItem("userName", request.getResponseHeader("userName"));
							window.location.href = res.getResponseHeader("location");
							break;
                    	default:
                        	document.getElementById("error").textContent = message;
                		}
                    }
                }, null, false);
            }
        }else{  
            form.reportValidity();
        }
    }, false);
})();