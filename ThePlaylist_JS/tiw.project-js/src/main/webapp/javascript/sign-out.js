(function(){
    document.getElementById("sign-out").addEventListener("click", (e) => {
		e.preventDefault();
        makeCall("GET", 'SignOut', null, function (res) {
            if(res.readyState === XMLHttpRequest.DONE){
                let message = res.responseText;
                switch(res.status){
					case 200:
	                    sessionStorage.clear();
	                    window.location.href = "sign-in.html";
	                    break;
	                case 403:
						sessionStorage.clear();
						window.location.href = res.getResponseHeader("location");
						break;
                    default:
                        document.getElementById("error").textContent = message;
                }
            }
        }, null, false);
    },false);
})();