var addSongToPlaylist = function(e) {
	e.preventDefault();
    let form = e.target;
    if(form.checkValidity() === true){
		
		document.getElementById("playlist-page").querySelectorAll(".message").forEach(element => {
            element.textContent = "";
        });
        document.getElementById("playlist-page").querySelectorAll(".error").forEach(element => {
            element.textContent = "";
        });
        
        let selected = 0;
        let radioboxes = form.querySelectorAll('input[type="radio"]');
        
        for (let i = 0; i < radioboxes.length; i++) {
		  if (radioboxes[i].checked) {
		    selected++;
		    if(selected == 2) break;
		  }
		}
		
		if(selected == 0) {
			document.getElementById("playlist-page").querySelector("#song-error").textContent = "Select at least one song";
			return;
		} 
		if(selected != 1) {
			document.getElementById("playlist-page").querySelector("#song-error").textContent = "Select only one song";
			return;
		}
		
		makeCall("POST", "EditPlaylist", form, function(res) {
            if (res.readyState === XMLHttpRequest.DONE) {
                let message = res.responseText;
                switch (res.status) {
					case 200:
						
                    	let playlistName = form.querySelector("#playlistName").value;
                    	makeCall("GET", "GetSongsInPlaylist?playlistName=" + playlistName, null, function (res1) {
	                        if (res1.readyState === XMLHttpRequest.DONE) {
	                            let message1 = res1.responseText;
	                            switch (res1.status) {
									case 200:
                    					lowerBound = 0;
										songsInPlaylist = JSON.parse(message1);
		                                reset.resetPlaylistPage();
		                                document.getElementById("playlist-page").querySelector("#message").textContent = "Song succesfully added to the playlist";
		                                render.showPlaylistPage(playlistName);
		                                break;
		                            case 403:
										sessionStorage.clear();
										window.location.href = res1.getResponseHeader("location");
										break;
	                            default:
	                                document.getElementById("playlist-page").querySelector("#error").textContent = message1; //error
	                            }
	                        }
	                    }, null, false);
	                        	
                    	break;
                    case 403:
						sessionStorage.clear();
						window.location.href = res.getResponseHeader("location");
						break;
                	default:
                    	document.getElementById("playlist-page").querySelector("#song-error").textContent = message;
                }
            }
        }, null, false);
			
    }else{
        form.reportValidity();
    }
}