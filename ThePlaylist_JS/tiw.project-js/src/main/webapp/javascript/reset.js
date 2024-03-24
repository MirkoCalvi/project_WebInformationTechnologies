var reset = new Reset();

function Reset() {
    this.resetHomePage = function() {
        let homePage = document.getElementById("home-page");
        
        //reset the title
        homePage.querySelector("#title").textContent = "";

        //reset errors and messages
        homePage.querySelectorAll(".message").forEach(element => {
            element.textContent = "";
        });
        homePage.querySelectorAll(".error").forEach(element => {
            element.textContent = "";
        });

        //reset the playlist table
        homePage.querySelector("#playlist-table").innerHTML = "";


        //reset the checkbox fields
        homePage.querySelector("#song-checkbox").innerHTML = "";
        
        //disable the forms
        homePage.querySelector("#create-playlist-form").onsubmit = null;
		homePage.querySelector("#song-form").onsubmit = null;
    };

    this.resetPlaylistPage = function() {
        lowerBound = 0;

        let playlistPage = document.getElementById("playlist-page");
        
        //reset the title
        playlistPage.querySelector("#title").textContent = "";
        
        //reset errors and messages
        playlistPage.querySelectorAll(".message").forEach(element => {
            element.textContent = "";
        });
        playlistPage.querySelectorAll(".error").forEach(element => {
            element.textContent = "";
        });
        
        //reset the table containing the songs
        playlistPage.querySelector("#song-table").innerHTML = "";
        
        //remove the buttons
        let prec = playlistPage.querySelector("#precButton").querySelector("button");
        let next = playlistPage.querySelector("#nextButton").querySelector("button");
        let edit = playlistPage.querySelector("#editSortingButton");
        prec.className = "off";
        next.className = "off";
        edit.className = "off";
        prec.onclick = null;
        next.onclick = null;
        edit.onclick = null;
        
        //reset the title in the edit section
        playlistPage.querySelector("#modifying").textContent = "";
        
        //reset the form that adds songs in the playlist
        playlistPage.querySelector("#playlistName").value = "";
        playlistPage.querySelector("#song-roundbox").innerHTML = "";
        
        //disable the form
        playlistPage.querySelector("#add-song-form").onsubmit = null;
        
    };

    this.resetPlayerPage = function() {
		let playerPage = document.getElementById("player-page");
		
		//reset the song details
		playerPage.querySelector("#info").innerHTML = "";
		
		//reset the player
		playerPage.removeChild(playerPage.querySelector("audio"));
    };

    this.resetSortingPage = function() {
		let sortingPage = document.getElementById("sorting-page");
		
		//reset the title
        sortingPage.querySelector("#title").textContent = "";
        
        //reset the error
        sortingPage.querySelector("#error").textContent = "";
        
        //reset the list of songs
        sortingPage.querySelector("#sorting-ul").innerHTML = "";
        
        //reset the update button
        sortingPage.querySelector("#update").onclick = null;
        
        draggedElement = null;
    };
};