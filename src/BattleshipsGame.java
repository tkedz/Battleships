import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BattleshipsGame {

	public static Player player=new Player();
	public static Player enemy=new Player();
	public static History history = History.getInstance();
	public static boolean exit = false;
	public static Scanner s = new Scanner(System.in);
	
	public static void main(String[] args) 
	{
		System.out.print("1. Zaloguj siê\n2. Zarejestruj siê\n");
		int x=s.nextInt();
		
		if(x == 1) 
		{
			player.login();
		}
		else if(x == 2) 
		{
			player.register();
		}
		
		if(player.isLoggedIn())
		{
			do {
				System.out.print("1. Nowa Gra\n2. Wznów grê \n3. Historia gier\n4. Statystyki\n5. Zakoñcz\n");
				x=s.nextInt();
				
				switch (x) {
				case 1:
					startGame();
					break;
					
				case 2:
					resumeGame();
					break;
					
				case 3:
					history.show(player.getName());
					break;
					
				case 4:
					player.showStats();
					break;
					
				case 5:
					exit=true;
					break;
					
				default:
					break;
				}				
			} while(!exit);
		}

	}
	
	private static void startGame() {
		s.nextLine();
		String enemyName;
		System.out.print("Podaj nazwe przeciwnika: ");
		enemyName = s.nextLine();
		enemy.setName(enemyName);
		
		player.prepareToGame();
		enemy.prepareToGame();
		player.placeShips();
		enemy.displayGrid();
		
		boolean playerMove = new Random().nextBoolean();
		JsonArray movesArrayJson = new JsonArray();
		
		System.out.print("Statki ustawione, zaczynamy gre\n");
		gameLoop(playerMove, movesArrayJson);	
	}
	
	private static void resumeGame() {
		Gson gson = new Gson();
		String fileContent = Utils.readFile(player.getName()+"-pausedGame.json");
		
		if(!fileContent.isEmpty()) {
			JsonObject obj = JsonParser.parseString(fileContent).getAsJsonObject();
			Grid playerGrid = gson.fromJson(obj.get("playerGrid"), Grid.class);
			player.setGrid(playerGrid);
			int playerLives = obj.get("playerLives").getAsInt();
			player.setLives(playerLives);
			
			Grid enemyGrid = gson.fromJson(obj.get("enemyGrid"), Grid.class);
			enemy.setGrid(enemyGrid);
			int enemyLives = obj.get("enemyLives").getAsInt();
			enemy.setLives(enemyLives);
			
			gameLoop(obj.get("playerMove").getAsBoolean(), obj.get("moves").getAsJsonArray());				
		} else {
			System.out.print("Brak niedokoñczonej gry\n");
		}
	}
	
	private static void gameLoop(boolean playerMove, JsonArray movesArrayJson) {
		String shoot;
		boolean pauseGame = false;
		while(player.getLives()>0 && enemy.getLives()>0)
		{
			System.out.println("GRACZ");
			player.displayGrid();
			System.out.println("\nPRZECIWNIK");
			enemy.displayGrid();
			
			JsonObject moveJson = new JsonObject();
			
			
			if(playerMove)
			{
				System.out.print("(exit - zapis gry) Strzelaj: ");
				shoot=s.next();
				
				if(shoot.equals("exit")) {
					pauseGame=true;
					break;
				}
				
				
				//int ch0=Utils.alpha.indexOf(shoot.charAt(0));
				int ch0=Utils.alpha.indexOf(Character.toUpperCase(shoot.charAt(0)));
				int ch1=Character.getNumericValue(shoot.charAt(1));
				if(ch0 == -1 || ch1 < 0 || ch1 > 9) {
					System.out.println("Z³e pozycje, spróbuj ponownie");
					continue;
				}
				
				System.out.print("Trafiony? (t/n): ");
				String result;
				result = s.next();
				
				if(result.equals("t"))
				{
					moveJson.addProperty("hit", true);
					enemy.decreaseLives();
					enemy.markGrid(ch1, ch0, 'T');
					playerMove = true;
				}
				else
				{
					moveJson.addProperty("hit", false);
					enemy.markGrid(ch1, ch0, 'N');
					playerMove = false;
				}
				moveJson.addProperty("player", player.getName());
				moveJson.addProperty("field", shoot);
				
				
				
			}
			else {
				System.out.print("(exit - zapis gry) Ruch przeciwnika: ");
				shoot=s.next();
				
				if(shoot.equals("exit")) {
					pauseGame=true;
					break;
				}
				
				
				int ch0=Utils.alpha.indexOf(Character.toUpperCase(shoot.charAt(0)));
				int ch1=Character.getNumericValue(shoot.charAt(1));
				if(ch0 == -1 || ch1 < 0 || ch1 > 9) {
					System.out.println("Z³e pozycje, spróbuj ponownie");
					continue;
				}
				
				playerMove = !player.checkEnemyShoot(ch0, ch1);
				
				if(playerMove) {
					moveJson.addProperty("hit", false);
				} else {
					moveJson.addProperty("hit", true);
				}
				moveJson.addProperty("player", enemy.getName());
				moveJson.addProperty("field", shoot);
				
			}
			movesArrayJson.add(moveJson);
			s.nextLine();
			
			
		}
		
		//Jeœli gra zosta³a zapauzowana zapisuje stan gry, w przeciwnym wypadku usuwam zawartoœæ pliku z zapisan¹ gr¹
		if(pauseGame) {
			Gson gson = new GsonBuilder().create();
			JsonObject pausedGame = new JsonObject();
			
			pausedGame.add("playerGrid",gson.toJsonTree(player.getGrid(), Grid.class));
			pausedGame.addProperty("playerLives", player.getLives());
			pausedGame.add("enemyGrid",gson.toJsonTree(enemy.getGrid(), Grid.class));
			pausedGame.addProperty("enemyLives", enemy.getLives());
			pausedGame.addProperty("enemyName", enemy.getName());
			pausedGame.addProperty("playerMove", playerMove);
			pausedGame.add("moves", movesArrayJson);
			
			Utils.saveToFile(player.getName()+"-pausedGame.json", pausedGame.toString(), false);
		} else {
			
			System.out.println("Koniec gry");	
			//Koniec gry, zapisujemy do historii
			Utils.saveToFile(player.getName()+"-pausedGame.json", "", false);
			
			JsonObject gameJson = new JsonObject();
			gameJson.addProperty("player", player.getName());
			gameJson.addProperty("enemy", enemy.getName());
			gameJson.addProperty("when", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
			
			if(enemy.getLives() == 0) {
				gameJson.addProperty("winner", player.getName());
				player.increaseWins();
				player.saveStats(true);
			} else {
				gameJson.addProperty("winner", enemy.getName());
				player.increaseLosses();
				player.saveStats(false);
			}
			
			gameJson.add("moves", movesArrayJson);
			history.save(gameJson);
		}
		
	}

}
	