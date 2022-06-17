import java.awt.Point;
import java.util.Scanner;

public class Grid {
	private Ship[] ships; 
	private char[][] grid;
	private boolean[][] allowedFileds; 
	
	public Grid() 
	{
		ships = new Ship[] {
			new Ship(4), new Ship(3), new Ship(3), new Ship(2), new Ship(2), new Ship(2), new Ship(1), new Ship(1), new Ship(1), new Ship(1)
		};
		
		grid = new char[Utils.GRID_SIZE][Utils.GRID_SIZE];
		allowedFileds = new boolean[Utils.GRID_SIZE][Utils.GRID_SIZE];
		for(int x=0;x<grid.length;x++) 
		{
			for(int y=0;y<grid.length;y++) 
			{
				grid[x][y]='.';
				allowedFileds[x][y]=true;
			}
		}
	}
	
	public void placeShips()
	{
		Scanner scanner = new Scanner(System.in);
		String tmp;
		
		System.out.println("\nUstaw swoje statki");
		display();
		for (Ship ship : ships) {
			boolean isPlacementValid = false;
			boolean increment = true;
			
			while(!isPlacementValid)
			{
				System.out.print(ship.getSize()+"-masztowiec, podaj pozycjê startow¹: ");
				tmp=scanner.next();
				
				int ch0=Utils.alpha.indexOf(Character.toUpperCase(tmp.charAt(0)));
				int ch1=Character.getNumericValue(tmp.charAt(1));
				
				if(ch0 == -1 || ch1 < 0 || ch1 > 9) {
					System.out.println("Z³e pozycje, spróbuj ponownie");
					continue;
				}
				
				Point from = new Point(ch1, ch0);
				
				if(ship.getSize() > 1) {
					System.out.print(ship.getSize()+"-masztowiec, podaj pozycjê koñcow¹: ");
					tmp=scanner.next();
					
					ch0=Utils.alpha.indexOf(Character.toUpperCase(tmp.charAt(0)));
					ch1=Character.getNumericValue(tmp.charAt(1));
					
					if(ch0 == -1 || ch1 < 0 || ch1 > 9) {
						System.out.println("Z³e pozycje, spróbuj ponownie");
						continue;
					}
					
				}
				
				Point to=new Point(ch1, ch0);					
								

				if(from.getX() == to.getX() && Math.abs(from.getY() - to.getY()) == ship.getSize() - 1)
				{
					if(from.getY() > to.getY()) {
						increment = false;
					} else {
						increment = true;
					}
					
					
					int x = (int) from.getX();
					int y = (int) from.getY();
					
					//Sprawdzenie czy wszystkie pola s¹ dostêpne
					for(int i = 0; i<ship.getSize(); i++)
					{
						if(allowedFileds[x][y] == false)
						{
							System.out.println("Z³e pozycje, spróbuj ponownie");
							break;
						}
						
						if(increment) y++;
						else y--;
						
						//Jeœli pêtla sprawdzi³a pozycjê koñcow¹ i nie przerwa³a dzia³ania, zmieniamy flage
						if(i == ship.getSize()-1)
							isPlacementValid=true;
					}
					
					//Zajêcie dostêpnych pól, jeœli pozycja jest prawid³owa
					if(isPlacementValid)
					{
						y = (int) from.getY();
						for(int i = 0; i<ship.getSize(); i++)
						{
							allowedFileds[x][y] = false;
							grid[x][y]=(char)(ship.getSize() + '0');
							ship.setPosition(i, new Point(x,y));
							if(increment) y++;
							else y--;
						}		
						
						forbidFieldsAround((int) from.getX(), (int) from.getY());
						forbidFieldsAround((int) to.getX(), (int) to.getY());
						
						display();
					}
				}
				else if(from.getY() == to.getY() && Math.abs(from.getX() - to.getX()) == ship.getSize() - 1)
				{
					
					if(from.getX() > to.getX()) {
						increment = false;
					} else {
						increment = true;
					}
					
					
					int x = (int) from.getX();
					int y = (int) from.getY();
					
					//Sprawdzenie czy wszystkie pola s¹ dostêpne
					for(int i = 0; i<ship.getSize(); i++)
					{
						if(allowedFileds[x][y] == false)
						{
							System.out.println("Z³e pozycje, spróbuj ponownie");
							break;
						}
						
						if(increment) x++;
						else x--;
						
						//Jeœli pêtla sprawdzi³a pozycjê koñcow¹ i nie przerwa³a dzia³ania, zmieniamy flage
						if(i == ship.getSize()-1)
							isPlacementValid=true;
					}
					
					//Zajêcie dostêpnych pól, jeœli pozycja jest prawid³owa
					if(isPlacementValid)
					{
						x = (int) from.getX();
						for(int i = 0; i<ship.getSize(); i++)
						{
							allowedFileds[x][y] = false;
							grid[x][y]=(char)(ship.getSize() + '0');
							ship.setPosition(i, new Point(x,y));
							if(increment) x++;
							else x--;
						}		
						
						forbidFieldsAround((int) from.getX(), (int) from.getY());
						forbidFieldsAround((int) to.getX(), (int) to.getY());
						
						display();
					}
				}
				else 
				{
					System.out.println("Z³e pozycje, spróbuj ponownie");
				}
			}
		}
	}
	
	public String display()
	{
		String board = "";
		
		System.out.print("   A B C D E F G H I J\n");
		board += "   A B C D E F G H I J\n";
		for (int x=0;x<grid.length;x++) {
			System.out.print(x+"  ");
			board+=x+" ";
			for(int y=0;y<grid.length;y++) 
			{
				System.out.print(grid[x][y] + " ");
				board+=grid[x][y] + " ";
			}
			System.out.print("\n");
			board += "\n";
		}
		
		return board;
	}
	
	public void markGrid(int x, int y, char tmp)
	{
		grid[x][y]=tmp;
	}
	
	public boolean checkShoot(int x, int y) {
		boolean isHit = false;
		for(Ship ship : ships) {
			Point[] points = ship.getPosition();
			if(!ship.status()) {
				
				for (Point point : points) {
					
					if(point.getX() == y && point.getY() == x) {
						ship.decreaseLives();
						markGrid(y, x, 'T');
						
						if(ship.getLives() == 0) {
							ship.sunk();
							for(Point p : points) {
								markGrid((int)p.getX(), (int)p.getY(), 'Z');
							}
						}
						
						isHit = true;
						break;
					}
				}
			}
			if(isHit) return true;
		}
		markGrid(y, x, 'N');
		return false;
	}
	
	private void forbidFieldsAround(int i, int j)
	{
		for(int x=Math.max(0, i-1);x<=Math.min(i+1, 9);x++)
		{
			for(int y=Math.max(0, j-1);y<=Math.min(j+1, 9);y++)
			{
				if(x!=i || y!=j || allowedFileds[x][y] == true )
				{
					allowedFileds[x][y]=false;
				}
			}
		}
	}
}
