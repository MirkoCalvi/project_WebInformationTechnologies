package it.polimi.tiw.playlist.beans;

public enum MusicGenre {
	
    BLUES("BLUES"),
    COUNTRY("COUNTRY"),
    ELECTRONIC("ELECTRONIC"),
    FOLK("FOLK"),
    HIP_HOP("HIP_HOP"),
    JAZZ("JAZZ"),
    METAL("METAL"),
    POP("POP"),
    R_AND_B("R_AND_B"),
    REGGAE("REGGAE"),
    ROCK("ROCK"),
    SALSA("SALSA"),
    SOUL("SOUL"),
    TECHNO("TECHNO"),
    CLASSICAL("CLASSICAL"),
    INDIE("INDIE"),
    PUNK("PUNK"),
    NOT_AVAILABLE("NOT_AVAILABLE");
	
	private String genre;
    
    public static MusicGenre getGenre(String gen) {
    	
    	switch(gen) {
    	
    		case "BLUES": return MusicGenre.BLUES;
    		case "COUNTRY": return MusicGenre.COUNTRY;
    		case "ELECTRONIC": return MusicGenre.ELECTRONIC;
    		case "FOLK": return MusicGenre.FOLK;
    		case "HIP_HOP": return MusicGenre.HIP_HOP;
    		case "JAZZ": return MusicGenre.JAZZ;
    		case "METAL": return MusicGenre.METAL;
    		case "POP": return MusicGenre.POP;
    		case "R_AND_B": return MusicGenre.R_AND_B;
    		case "REGGAE": return MusicGenre.REGGAE;
    		case "ROCK": return MusicGenre.ROCK;
    		case "SALSA": return MusicGenre.SALSA;
    		case "SOUL": return MusicGenre.SOUL;
    		case "TECHNO": return MusicGenre.TECHNO;
    		case "CLASSICAL": return MusicGenre.CLASSICAL;
    		case "INDIE": return MusicGenre.INDIE;
    		case "PUNK": return MusicGenre.PUNK;
    	
    		default: return MusicGenre.NOT_AVAILABLE;
    	}
    }
    
    private MusicGenre(String genre ) {
    	this.genre=genre;
    }
    
    
    
    @Override
    public String toString() {
    	return genre;
    }
    
    
    
}







