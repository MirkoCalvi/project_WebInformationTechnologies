package it.polimi.tiw.playlist.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class FromJsonToArray {
	
	//Static method that reads a jSon string of numbers and converts it into an arrayList of integer
	public static ArrayList<Integer> fromJsonToArrayList(String jSon){
		
		 ArrayList<Integer> indexes = new ArrayList<>();

	        JsonArray jsonArray = JsonParser.parseString(jSon).getAsJsonArray();

	        for (JsonElement element : jsonArray) {
	            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
	                indexes.add(element.getAsInt());
	            }
	        }

	        return indexes;
	}
}