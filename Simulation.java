import java.util.Scanner;
import java.io.File;
import java.io.IOException;
/**
 * File: Simulation.java <br>
 * Project: CS150 Project 4, Fall 2022 <br>
 * Date: 12/07/2022 <br>
 * E-mail: geladzel@lafayette.edu <br>
 * Class Description: Simulation has the main method. It creates the clock object in the method. Calls clock.start(); Clock prints all the information to 
 * the log, so simulation does not have to track it. Simulation reads the data from a config file and passes it to the clock constructor. 
 *
 * @author Lasha Geladze
 * @version 12/07/2022
 */
public class Simulation
{
    public static void main(String args[]){
        try{
            File myFile = new File("input.txt");
           Scanner reader = new Scanner(myFile);
           int warehouseAmount = reader.nextInt();
           int truckAmount = reader.nextInt();
           int shipmentAmount = reader.nextInt(); 
           int x_size = reader.nextInt();
           int y_size = reader.nextInt();
           
           Clock myClock = new Clock(warehouseAmount, truckAmount, shipmentAmount, x_size, y_size);
           myClock.start();
        }catch(IOException e){
            System.err.println("ERROR");
        }
    }
}
