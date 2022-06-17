import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Utils {
	
	public static String alpha = "ABCDEFGHIJ";
	public static int GRID_SIZE = 10;
	
	//Sprawdzenie czy login nie jest zajety
	public static boolean isLoginTaken(String login) 
	{
		boolean isTaken = false;
		try 
		{
			
			File file=new File("users.txt");
			if(!file.exists()) file.createNewFile();
			Scanner fs=new Scanner(file);
			
			while(fs.hasNextLine()) 
			{
				String tmpString = fs.nextLine();
				if(tmpString.split(" ")[0].equals(login)) 
				{
					isTaken = true;
					break;
				}
			}
			fs.close();

		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		
		return isTaken;
	}	
	
	public static int[] validateUser(String login, String password) 
	{
		//[status walidacji (0 - false, 1 - true), wygrane, przegrane]
		int[] isValidated = {0,0,0};
		
		try 
		{
			File file=new File("users.txt");
			if(!file.exists()) file.createNewFile();
			Scanner fs=new Scanner(file);
			
			while(fs.hasNextLine()) 
			{
				String[] arr = fs.nextLine().split(" ");
				if(arr[0].equals(login) && arr[3].equals(password)) 
				{
					isValidated[0]=1;
					isValidated[1]=Integer.parseInt(arr[1]);
					isValidated[2]=Integer.parseInt(arr[2]);
					break;
				}
			}
			fs.close();	
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		return isValidated;
	}
	
	public static String readFile(String filename) {
		String entireFileText = "";
		try {
			entireFileText = new Scanner(new File(filename)).useDelimiter("\\Z").next();
		} catch(FileNotFoundException e) {
			entireFileText="";
		} catch(NoSuchElementException e) {
			entireFileText="";
		}
		return entireFileText;
	}
	
	public static void saveToFile(String filename, String stringToSave, boolean ifAppend) {
		try (FileWriter writer = new FileWriter(filename, ifAppend)) {
			writer.write(stringToSave);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createFile(String filename) {
		try {
			File file=new File(filename);
			file.createNewFile();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
