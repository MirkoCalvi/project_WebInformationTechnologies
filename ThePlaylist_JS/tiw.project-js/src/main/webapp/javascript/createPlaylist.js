var createPlaylist = function(e) {
	e.preventDefault();
    let form = e.target;
    
    if (form.checkValidity() === true) {
		
		document.getElementById("home-page").querySelectorAll(".message").forEach(element => {
            element.textContent = "";
        });
        document.getElementById("home-page").querySelectorAll(".error").forEach(element => {
            element.textContent = "";
        });
		
		let oneSelected = false;
        let checkboxes = form.querySelectorAll('input[type="checkbox"]');
        
        for (let i = 0; i < checkboxes.length; i++) {
		  if (checkboxes[i].checked) {
		    oneSelected = true;
		    break;
		  }
		}
		
		if(!oneSelected){
			document.getElementById("home-page").querySelector("#crp-error").textContent = "Select at least one song";
			return;
		} 
		
		let playlistName = form.querySelector('#playlistName').value;
		let alreadyExist = false;
		for (let i = 0; i < listPlaylist.length; i++) {
		  if (listPlaylist[i].name == playlistName) {
		    alreadyExist = true;
		    break;
		  }
		}
		
		if(alreadyExist){
			document.getElementById("home-page").querySelector("#crp-error").textContent = "This playlist already exists";
			return;
		} 

        makeCall("POST", "CreatePlaylist", form, function(res) {
            if (res.readyState === XMLHttpRequest.DONE) {
                let message = res.responseText;
                switch (res.status) {
					case 200:
						form.reset();
                    	document.getElementById("home-page").querySelector("#crp-message").textContent = "Playlist succesfully Created";
                    	let date = new Date();
                    	date.setMonth(date.getMonth()+1); // by default is zero based
                    	date = (date.getDate() < 10 ? '0' : '') + date.getDate() + '-' + (date.getMonth() < 10 ? '0' : '') + date.getMonth() + '-' + date.getFullYear();
                        listPlaylist.push(new Playlist(playlistName, date));
                    	render.showAllPlaylistList();
                    	break;
                    case 403:
						sessionStorage.clear();
						window.location.href = res.getResponseHeader("location");
						break;
                	default:
						form.name.value = "";
                    	document.getElementById("home-page").querySelector("#crp-error").textContent = message;
                }
            }
        }, null, false);

    } else {
        form.reportValidity();
    }
}

function Playlist(name, date) {
    this.name = name;
    this.creationDate = date;
}