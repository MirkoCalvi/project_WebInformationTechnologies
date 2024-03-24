var createSong = function(e) {
	e.preventDefault();
    let form = e.target;
    if (form.checkValidity() === true) {

        document.getElementById("home-page").querySelectorAll(".message").forEach(element => {
            element.textContent = "";
        });
        document.getElementById("home-page").querySelectorAll(".error").forEach(element => {
            element.textContent = "";
        });

        makeCall("POST", "CreateSong", form, function(res) {
            if (res.readyState === XMLHttpRequest.DONE) {
                let message = res.responseText;
                switch (res.status) {
					case 200:
						document.getElementById("home-page").querySelector("#song-message").textContent = "Song succesfully uploaded";
						listSong.push(new Song(message,form.querySelector("#songTitle").value));
						form.reset();
                    	render.showCheckBoxSongs();
                    	break;
                    case 403:
						sessionStorage.clear();
						window.location.href = res.getResponseHeader("location");
						break;
                	default:
                    	document.getElementById("home-page").querySelector("#song-error").textContent = message;
                }
            }
        }, null, false);

    } else {
        form.reportValidity();
    }
}

function Song (id,title){
	this.id = id;
	this.title = title;
}