(function() {
	if(sessionStorage.getItem("userName") !== null){
		window.location.href = "ThePlaylist.html";
		return;
	}
    document.getElementById("sign-in").addEventListener("submit", (e) =>{
		e.preventDefault();
        let form = e.target;
        console.log(form);

        if(form.checkValidity() === true){
            document.getElementById("error").textContent = "";
            
            makeCall("POST", 'SignIn', form, function (res) {
                if(res.readyState === XMLHttpRequest.DONE){
                    let message = res.responseText;
                    switch(res.status){
						case 200: 
	                        sessionStorage.setItem("userName", message);
	                        window.location.href = "ThePlaylist.html";
	                        break;
	                    case 403:
							sessionStorage.setItem("userName",request.getResponseHeader("userName"));
							window.location.href = res.getResponseHeader("location");
							break;
                    	default:
                        	document.getElementById("error").textContent = message;
                	}
            	}
            }, null, false);
            
        }else{  
            form.reportValidity();
        }
    }, false);
})();