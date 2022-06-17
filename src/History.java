import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class History {
	private static History instance;
	private JsonArray historyArray;
	private JsonArray historyFiltered;
	
	private History() {
		load();
		
		if(historyArray == null) {
			historyArray = new JsonArray();
		}
		
		historyFiltered = new JsonArray();
	}
	
	public static History getInstance() {
		if(instance == null) {
			instance = new History();
		}
		return instance;
	}
	
	public void load() {
		try(FileReader reader = new FileReader("history.json")) {
			
			historyArray =  new Gson().fromJson(reader, JsonArray.class);

		} catch(FileNotFoundException e) {
			Utils.createFile("history.json");
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void save(JsonElement jsonToSave) {
		try {
			
			historyArray.add(jsonToSave);
			Utils.saveToFile("history.json", historyArray.toString(), false);
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public void show(String username) {
		historyArray.forEach((el) -> {
			if(el.isJsonObject()) {
				JsonObject elObj = el.getAsJsonObject();
				
				//Sprawdzenie czy dana gra by³a rozgrywana przez zalogowanego uzytkownika
				if(elObj.get("player").getAsString().equals(username)) {
					historyFiltered.add(elObj);
				}
			}
		});
		
		if(historyFiltered.size() == 0) {
			System.out.println("Historia rozgrywek jest pusta");
			return;
		}
		
		Gson gson=new GsonBuilder().setPrettyPrinting().create();
		historyFiltered.forEach((el) -> {
			String output = gson.toJson(el);				
			System.out.print(output+"\n-------------------------------------\n");
		});
	}

}
