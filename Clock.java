import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
/**
 * File: Clock.java <br>
 * Project: CS150 Project 4, Fall 2022 <br>
 * Date: 12/09/2022 <br>
 * E-mail: geladzel@lafayette.edu <br>
 * Class Description: Creates a map and a file to write logs in. Stores a hour int, ticks it off. During each while loop iteration, it makes all the
 * active trucks do action() (action defined in truck class). It stores an active and passive truck list.
 * Clock creates and writes the logs to the txt file .
 *
 * @author Lasha Geladze
 * @version 12/09/2022
 */
public class Clock
{   
    /**
     * Active truck list.
     */
    private ArrayList<Truck> activeTruckList;    //getting this from map intitally, then manipulating it here. At the end of each while loop, iterates through the list and passes inactive trucks to passiveTruck. When activeTruck is empty, terminates
    
    /**
     * Passive truck list.
     */
    private ArrayList<Truck> passiveTruckList;
    
    /**
     * Warehouse list.
     */
    private ArrayList<Warehouse> warehouseList;
    
    /**
     * The amount of warehouses.
     */
    private int warehouseAmount;
    
    /**
     * The amount of trucks.
     */
    private int truckAmount; 
    
    /**
     * The maximum shipment amount.
     */
    private int maxShipmentAmount; 
    
    /**
     * Map x size.
     */
    private int x_size; 
    
    /**
     * Map y size.
     */
    private int y_size; 
    
    /**
     * Variable keeping track of time.
     */
    private int time = 0;
    
    /**
     * The map object.
     */
    private Map map;
    
    public Clock(int warehouseAmount, int truckAmount, int shipmentAmount, int x_size, int y_size){
        map = new Map(x_size, y_size, warehouseAmount, truckAmount, shipmentAmount);
        activeTruckList = map.getTruckList();
        warehouseList = map.getWarehouseList();
        passiveTruckList = new ArrayList();
    }
    
    /**
     * This method starts the actions involving the simulation. It prints out warehouses and shipments at the start, and goes through each of the active 
     * trucks with a while loop. The method checks whether the truck is active or not after each action cycle, and moves them to a passive truck list. When
     * the active list is empty, the program terminates.
     * 
     */
    public void start(){
        try{
            FileWriter writer = new FileWriter("log_info.txt");
            BufferedWriter buffWriter = new BufferedWriter(writer);
            
            for (Warehouse w : warehouseList){
                buffWriter.write(w.toString());
                buffWriter.newLine();
                for (Shipment s : w.getToBeDelivered()){
                    buffWriter.write(s.toString());
                    buffWriter.newLine();//printing warehouse location and ID initially with all the shipments
                }
            }
            buffWriter.write("===========================================================================");
            buffWriter.newLine();
            
            while(!activeTruckList.isEmpty()){ //trucks printed, action happens, warehouses printed.
                buffWriter.write("TIME: " + time); 
                buffWriter.newLine();
                for (Truck t : activeTruckList){
                    buffWriter.write(t.log_status());
                    buffWriter.newLine();
                    t.action();
                    if (t.isDone()){   //if truck is done, add it to list.
                        passiveTruckList.add(t);
                    }
                }
                for(Truck t: passiveTruckList){
                    activeTruckList.remove(t);  //remove passive trucks from the active list
                }
                for (Warehouse w : warehouseList){
                    buffWriter.write(w.log_status()); //print warehouse info
                    buffWriter.newLine();
                } 
                time++;
                buffWriter.write("------------------------------------------------------------------------------"); //print warehouse info
                buffWriter.newLine();
            }
            buffWriter.write("===========================================================================");
            
            buffWriter.close();
            writer.close();
            
        }catch (IOException e){
            System.err.println("EXCEPTION");
        }
    }
}
