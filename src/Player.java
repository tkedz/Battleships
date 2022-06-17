import java.util.Scanner;

public class Player {
	
	private boolean isLogged;
	private String name;
	private int lives;
	private int wins;
	private int losses;
	private Grid grid;
	private Scanner scanner;
	
	public Player() 
	{
		isLogged = false;
		scanner=new Scanner(System.in);
	}
		
	public void register() 
	{

		String login, passwd, passwdConfirm;
		
		System.out.print("Podaj login: ");
		login=scanner.next();
		
		//Sprawdzenie loginu
		boolean isTaken = Utils.isLoginTaken(login);
		while(isTaken) 
		{
			System.out.print("Login zajêty, spróbuj ponownie: ");
			login=scanner.next();
			isTaken=Utils.isLoginTaken(login);
		}
		
		while(true) 
		{
			System.out.print("Podaj has³o: ");
			passwd=scanner.next();
			System.out.print("Powtórz has³o: ");
			passwdConfirm=scanner.next();
			
			if(passwd.equals(passwdConfirm))
				break;
			else 
			{
				System.out.println("Has³a s¹ ró¿ne, powtórz procedurê");
			}
		}
		
		//Utils.saveUserToFile(login, passwd);
		Utils.saveToFile("users.txt", login+" 0 0 "+passwd+"\n", true);
		
		//jesli udalo sie zarejestrowac, od razu logujemy
		this.isLogged=true;
		this.name=login;
		this.wins = this.losses = 0;
		System.out.println("Konto utworzone, zalogowano");
	}
	
	public void login() 
	{
		String login, passwd;
		
		while(true) 
		{
			System.out.print("Podaj login: ");
			login=scanner.next();
			
			System.out.print("Podaj has³o: ");
			passwd=scanner.next();
			
			int[] validationResult = Utils.validateUser(login, passwd);
			if(validationResult[0] == 1) 
			{
				this.isLogged=true;
				this.name = login;
				this.wins = validationResult[1];
				this.losses = validationResult[2];
				break;
			} 
			else 
			{
				System.out.println("B³¹d, spróbuj ponownie");
			}
		}
	}
	
	public boolean isLoggedIn() 
	{
		return isLogged;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void showStats() {
		System.out.print("Witaj " + name + "!\nRozegrane gry: " + (wins + losses) + "\n"
				+ "Wygrane: " + wins + "\nPrzegrane: " + losses + "\n");
	}
	
	public void saveStats(boolean ifWin) {
		String usersFileText = Utils.readFile("users.txt");
		
		if(ifWin) {
			usersFileText = usersFileText.replace(name+" "+(wins-1)+ " "+losses, name+ " "+wins+" "+losses);			
		} else {
			usersFileText = usersFileText.replace(name+" "+wins+ " "+(losses-1), name+ " "+wins+" "+losses);	
		}
		
		Utils.saveToFile("users.txt", usersFileText, false);
	}
	
	public void increaseWins() {
		wins++;
	}
	
	public void increaseLosses() {
		losses++;
	}
	
	public Grid getGrid() {
		return grid;
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
	public void displayGrid() {
		grid.display();
	}
	
	public void placeShips() {
		grid.placeShips();
	}
	
	public void markGrid(int x, int y, char c) {
		grid.markGrid(x, y, c);
	}
	
	public void prepareToGame() {
		grid = new Grid();
		lives = 20;
	}
	
	public int getLives() 
	{
		return lives;
	}
	
	public void setLives(int lives) {
		this.lives = lives;
	}
	
	public void decreaseLives()
	{
		lives--;
	}
	
	public boolean checkEnemyShoot(int x, int y)
	{
		Boolean result;
		result = grid.checkShoot(x, y);
		
		if(result) decreaseLives();
		
		return result;
	}
	
}
