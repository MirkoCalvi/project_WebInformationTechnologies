//for home page use
var listPlaylist = [];
var listSong = [];

//for playlist and sorting page use
var lowerBound = 0;
var songsInPlaylist = [];

//for the home button
var goHome = function (e){
		e.preventDefault();
		render.showHomePage();
};

function Render () {
		
	this.showCheckBoxSongs = function () {
		let home = document.getElementById("home-page");
        let checkbox = home.querySelector("#song-checkbox");
        checkbox.innerHTML = "";
        //add the songs to the checkbox
        for (let i = 0; i < listSong.length; i++) {
            //creating the chackbox element
            let box = document.createElement("input");
            let label = document.createElement("label");
            let div = document.createElement("div");
            box.type = "checkbox";
            box.name = "song" + i;
            label.htmlFor = "song" + i;
            box.id = "song" + i;
            box.value = listSong[i].id;
            label.textContent = listSong[i].title;

            //adding the song to the checkbox
            div.appendChild(box);
            div.appendChild(label);
            checkbox.appendChild(div);
            
        }

    };
    
    this.showAllPlaylistList = function () {
		let home = document.getElementById("home-page");
        let table = home.querySelector("#playlist-table");
        table.innerHTML = "";

        for (let i = 0; i < listPlaylist.length; i++) {
            let row = document.createElement("tr");

            //add the name
            let columnName = document.createElement("td");
            let playlistName = document.createTextNode(listPlaylist[i].name);

            //add event listener to the playlist name
            columnName.onclick = function () {
                makeCall("GET", "GetSongsInPlaylist?playlistName=" + listPlaylist[i].name, null, function (res) {
                    if (res.readyState === XMLHttpRequest.DONE) {
                        let message = res.responseText;
                        switch (res.status) {
							case 200:
								reset.resetHomePage();
								songsInPlaylist = JSON.parse(message);
                                render.showPlaylistPage(listPlaylist[i].name);
                                break;
                            case 403:
								sessionStorage.clear();
								window.location.href = res.getResponseHeader("location");
								break;
                        default:
                            home.querySelector("#error").textContent = message; //error
                        }
                    }
                }, null, false);
            };
            columnName.appendChild(playlistName);
            row.appendChild(columnName);

            //add the date
            let columnDate = document.createElement("td");
            let playlistDate = document.createTextNode(listPlaylist[i].creationDate);
            columnDate.appendChild(playlistDate);
            row.appendChild(columnDate);

            table.appendChild(row);
        }
    };

    this.showHomePage = function () {

        //make the home page the only visible page
        document.getElementById("home-page").className = "on";
        document.getElementById("playlist-page").className = "off";
        document.getElementById("player-page").className = "off";
        document.getElementById("sorting-page").className = "off";

        let homeButton = document.getElementById("home-button");
        homeButton.className = "off";
        homeButton.onclick = null;

        //writing the welcome message
        let home = document.getElementById("home-page");
        let title = home.querySelector("#title");
        title.textContent = "Welcome back " + sessionStorage.getItem("userName");

        if (listPlaylist.length === 0) {
            makeCall("GET", "GetPlaylistList", null, function (res) {
                if (res.readyState === XMLHttpRequest.DONE) {
                    let message = res.responseText;
                    switch (res.status) {
						case 200:
                            listPlaylist = JSON.parse(message);
                            if(listPlaylist.length === 0){
								home.querySelector("#playlist-error").textContent = "You have no playlists saved";
							}
							else{
								render.showAllPlaylistList();
							}
                            break;
                        case 403:
							sessionStorage.clear();
							window.location.href = res.getResponseHeader("location");
							break;
                    	default:
                        home.querySelector("#playlist-error").textContent = message; //playlist list error
                    }
                }
            }, null, false);
        } else{
			this.showAllPlaylistList();
		}   
        if (listSong.length === 0) {
            makeCall("GET", "GetSongList", null, function (res) {
                if (res.readyState === XMLHttpRequest.DONE) {
                    let message = res.responseText;
                    switch (res.status) {
						case 200:
                            listSong = JSON.parse(message);
                            if(listSong.length === 0){
								home.querySelector("#get-songs-error").textContent = "You have no songs saved";
							}
							else {
								render.showCheckBoxSongs();
							}
                            break;
                        case 403:
							sessionStorage.clear();
							window.location.href = res.getResponseHeader("location");
							break;
                    	default:
                        	home.querySelector("#get-songs-error").textContent = message; //song list error
                    }
                }
            }, null, false);
        } else{
			this.showCheckBoxSongs();
		}
		
		//enable the forms
		home.querySelector("#create-playlist-form").onsubmit = createPlaylist;
		home.querySelector("#song-form").onsubmit = createSong;
    };
    
    this.updateBlock = function () {
		let playlist = document.getElementById("playlist-page");
		let table = document.getElementById("song-table");
        table.innerHTML = "";
		let row = document.createElement("tr");
		
        if (lowerBound < 0)
            lowerBound = 0;
        else lowerBound -= lowerBound % 5;
        
        for (let i = lowerBound; i < songsInPlaylist.length && i < lowerBound + 5; i++) {
            //create a column
            let column = document.createElement("td");
            let insideTable = document.createElement("table");

            //create a row for the image
            let insideImage = document.createElement("tr");
            let image = document.createElement("img");
            image.src = songsInPlaylist[i].imageContent;
            insideImage.appendChild(image);
            insideTable.appendChild(insideImage);

            //create a row for the name
            let insideTitle = document.createElement("tr");
            let title = document.createTextNode(songsInPlaylist[i].title);
            insideTitle.appendChild(title);
            insideTable.appendChild(insideTitle);

            //add event listener to the song name
            insideTitle.onclick= function () {
                makeCall("GET", "PlaySong?songId=" + songsInPlaylist[i].id, null, function (res) {
                    if (res.readyState === XMLHttpRequest.DONE) {
                        let message = res.responseText;
                        switch (res.status) {
                            case 200: 
                            	reset.resetPlaylistPage();
                                render.playSong(songsInPlaylist[i], JSON.parse(message));
                                break;
                            case 403:
								sessionStorage.clear();
                                window.location.href = res.getResponseHeader("location");
                                break;
                            default:
                                playlist.querySelector("#error").textContent = message; //player playlist table error
                        }
                    }
                }, null, false);
            };

            //insert the insideTable into the column and the column into the row
            column.appendChild(insideTable);
            row.appendChild(column);
        }
        //add the row to the table
        table.appendChild(row);

        //add the buttons
        let prec = playlist.querySelector("#precButton").querySelector("button");
        let next = playlist.querySelector("#nextButton").querySelector("button");

        //set the prec button
        if (lowerBound <= 0) {

            //if the lower bound is lower than zero then we trun off the prec button
            prec.className = "off";
            prec.onclick = null;
        } else {

            //if the lower bound is greater than zero then we turn on the prec button
            prec.className = "on";
            prec.onclick = function (e) {
								e.preventDefault();
				                lowerBound -= 5;
				                render.updateBlock();
				            };
        }

        //set the next button
        if (lowerBound + 5 >= songsInPlaylist.length) {

            //if the lower bound is greater than song In Playlist then we turn off the next button
            next.className = "off";
            next.onclick = null;
        } else {

            //if the lower bound is less than song In Playlist then we turn on the next button
            next.className = "on";
            next.onclick = function (e) {
								e.preventDefault();
					            lowerBound += 5;
					            render.updateBlock();
					        };
        }

    };
    
    this.showSongsNotInPlaylist = function () {
		//all the songs the user can add to the playlist
        let songRoundBox = document.getElementById("playlist-page").querySelector("#song-roundbox");
        songRoundBox.innerHTML = "";

        let songsNotInPlaylist = [...listSong];

        songsNotInPlaylist = songsNotInPlaylist.filter((songNot) => {
        return !songsInPlaylist.some(songIn => songIn.id === songNot.id);
        });
		
		if(songsNotInPlaylist.length === 0){
			document.getElementById("playlist-page").querySelector("#song-error").textContent = "You have no more songs to add";
			return;
		}
		
        //add the songs to the roundbox
        for (let i = 0; i < songsNotInPlaylist.length; i++) {

            //creating the radiobox element
            let box = document.createElement("input");
            let label = document.createElement("label");
            let div = document.createElement("div");
            box.type = "radio";
            box.name = "song";
            label.htmlFor = "song" + i;
            box.id = "song" + i;
            box.value = songsNotInPlaylist[i].id;
            label.textContent = songsNotInPlaylist[i].title;

            //adding the song to the roundbox
            div.appendChild(box);
            div.appendChild(label);
            songRoundBox.appendChild(div);
            
        }
	};

    this.showPlaylistPage = function (playlistName) {

        //make the playlist page the only visible page
        document.getElementById("home-page").className = "off";
        document.getElementById("playlist-page").className = "on";
        document.getElementById("player-page").className = "off";
        document.getElementById("sorting-page").className = "off";

        let homeButton = document.getElementById("home-button");
        homeButton.className = "on";
        homeButton.onclick = function(e){
								e.preventDefault();
								reset.resetPlaylistPage();
								goHome(e);
							 }

        //show the playlist name
        let playlist = document.getElementById("playlist-page");
        let title = playlist.querySelector("#title");
        title.textContent = "Playlist: " + playlistName;
        playlist.querySelector("#playlistName").value = playlistName;
        
        playlist.querySelector("#modifying").textContent = "UPDATE " + playlistName;
        
        //enable the sorting button
        playlist.querySelector("#editSortingButton").className = "on";
        playlist.querySelector("#editSortingButton").onclick = function(e) {
			e.preventDefault();
			reset.resetPlaylistPage();
			render.showSortingPage(playlistName);
		};

        
        this.updateBlock();

        this.showSongsNotInPlaylist();
        
        //enable the form
        playlist.querySelector("#add-song-form").onsubmit = addSongToPlaylist;
    };

    this.playSong = function (song, details) {

        //make the player page the only visible page
        document.getElementById("home-page").className = "off";
        document.getElementById("playlist-page").className = "off";
        document.getElementById("player-page").className = "on";
        document.getElementById("sorting-page").className = "off";

		let homeButton = document.getElementById("home-button");
        homeButton.className = "on";
        homeButton.onclick = function(e){
								 e.preventDefault();
								 reset.resetPlayerPage();
								 goHome(e);							
							 }

        let playerPage = document.getElementById("player-page");
        let infoContainer = playerPage.querySelector("#info");
        infoContainer.innerHTML = "";

        //add the image
        let div = document.createElement("div");
        let image = document.createElement("img");
        image.src = song.imageContent;
        div.appendChild(image);
        infoContainer.appendChild(div);

        //add the title
        div = document.createElement("div");
        let title = document.createElement("h1");
        title.textContent = "Title: " + song.title;
        div.appendChild(title);
        infoContainer.appendChild(div);

        //add the other info
        div = document.createElement("div");
        let singer = document.createElement("h1");
        singer.textContent = "Singer: " + details.singer;
        div.appendChild(singer);
        let album = document.createElement("h2");
        album.textContent = "Album: " + details.albumTitle;
        div.appendChild(album);
        let year = document.createElement("h3");
        year.textContent = "Publication Year: " + details.publicationYear;
        div.appendChild(year);
        let genre = document.createElement("hp");
        genre.textContent = "Genre: " + details.genre;
        div.appendChild(genre);
        infoContainer.appendChild(div);

        //add the player
        let player = document.createElement("audio");
        player.type = "audio/mpeg";
        player.controls = "controls";
        player.src = details.audioContent;
        playerPage.appendChild(player);
    };

    this.showSortingPage = function (playlistName) {

        //make the sorting page the only visible page
        document.getElementById("home-page").className = "off";
        document.getElementById("playlist-page").className = "off";
        document.getElementById("player-page").className = "off";
        document.getElementById("sorting-page").className = "on";

        let homeButton = document.getElementById("home-button");
        homeButton.className = "on";
        homeButton.onclick = function(e){
								 e.preventDefault();
								 reset.resetSortingPage();
								 goHome(e);							
							 }

        let sortingPage = document.getElementById("sorting-page");
        let title = sortingPage.querySelector("#title");
        title.textContent = "Sorting: "+ playlistName;

        sortingPage.querySelector("#update").onclick = function (e){
															e.preventDefault()
												            pushNewSorting(playlistName);
												       };
        
       let list = sortingPage.querySelector("#sorting-ul");
       for(let i = 0; i<songsInPlaylist.length; i++){
            let li = document.createElement("li");
            list.appendChild(li);
            li.textContent = songsInPlaylist[i].title;
            li.id = songsInPlaylist[i].id;
            li.draggable = true;
            li.addEventListener("dragstart", dragStart);
			li.addEventListener("dragover", dragOver);
			li.addEventListener("drop", drop);
			li.addEventListener("dragend", dragEnd);   
       }
    };

};

var render = new Render();

songsInPlaylist.getSong = function (songId) {
    for(let i = 0; i < songsInPlaylist.length; i++)
        if(songsInPlaylist[i].id === songId)
            return songsInPlaylist[i];
};