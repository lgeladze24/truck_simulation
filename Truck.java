import java.util.ArrayList;
/**
 * File: Truck.java <br>
 * Project: CS150 Project 4, Fall 2022 <br>
 * Date: 12/07/2022 <br>
 * E-mail: geladzel@lafayette.edu <br>
 * Class Description: Truck picks up shipments from their warehouses and takes them to different warehouses. Has a random load from 1 to 5, speed depends 
 * on size. Gets shipments from manifest, but gives access to shipments to warehouses as well. Once it is done with the manifest, truck does not do any
 * actions. Goes to each location on a direct path. Has 4 states: moving, loading, unloading, and waiting.
 * 
 * @see java.util.ArrayList
 *
 * @author Lasha Geladze
 * @version 12/07/2022
 */
public class Truck implements Schedule
{   
    private Location location;
    /**
     * Maximum size of truck.
     */
    final static int maxTruckSize = 5;
    
    /**
     * ID generator.
     */
    private static int IDCounter;
    
    /**
     * ID variable
     */
    private int ID;
    
    /**
     * Size variable
     */
    private int size;
    
    /**
     * Speed variable
     */
    private int speed;
    
    /**
     * Variable that keeps track of the remaining storage.
     */
    private int capacity;
    
    /**
     * Variable that keeps track of the moveAngle. Changes after each load() or unload()
     */
    private double moveAngle;
    
    /**
     * Stack based storage 
     */
    private Stack<Shipment> storage;
    
    /**
     * Manifest of the truck
     */
    private Manifest manifest;
    
    /**
     * Current state of truck.
     */
    private TruckState state;
    
    /**
     * Maximum amount of shipments (passed to manifest)
     */
    private int maxShipmentAmount;
    
    /**
     * Destination of the warehouse (changed after each load())
     */
    private Warehouse destination;
    
    /**
     * Boolean to keep track of whether truck should load or unload.
     */
    private boolean isLoading = true;
    
    /**
     * Boolean to see whether truck has completed its final delivery.
     */
    private boolean isDone = false;
    
    public Truck(){}
    
    public Truck(int size, Warehouse war, int maxShipmentAmount, int warehouseAmount, ArrayList<Warehouse> warehouseList){
        this.size = size;
        speed = 6 - size;
        ID = IDCounter;
        IDCounter++;
        location = new Location(0,0);
        this.location.setXY(war.getLocation());
        storage = new Stack<Shipment>();
        capacity = size;
        state = state.MOVING;
        this.maxShipmentAmount = maxShipmentAmount;
        manifest = new Manifest(maxShipmentAmount, size, this, warehouseList);
        destination = manifest.getShipmentList().element().getPickUp();
        moveAngle = location.calculateAngle(destination.getLocation());
    }
    
    public static int getMaxTruckSize(){
        return maxTruckSize;
    }
    
    public void action(){
        switch(state){
            case MOVING:
                move();
                break;
            case LOADING:
                load();
                break;
            case UNLOADING:
                unload();
                break;
            case WAITING:
                break;  //skip turn
            default:
                break;
        }
    }
    
    /**
     * Helper method that is called when truck is moving from one warehouse to another . Uses movementAngle and speed to advance on the coordinate plane.
     */
    private void move(){
        if (location.equals(destination.getLocation())){
            destination.getTruckIn(this);
            //condition when truck spawns at the place where it loads
        }else if (location.distance(destination.getLocation()) <= speed){
            location.setXY(destination.getLocation());
            destination.getTruckIn(this);
        }else{
            location.move(Math.cos(moveAngle) * speed, Math.sin(moveAngle) * speed);
        }
    } 
    
    /**
     * Called when truck state is LOADING. Gets shipment from manifest, puts it in storage and checks capacity. Decides whether to go to next dropOff or pickup
     * warehouse.
     */
    private void load(){
        Shipment temp = manifest.removeShipment();
        storage.push(temp);    //get it in storage
        capacity -= storage.peek().getSize();       // decrease capacity
        destination.removeShipment(storage.peek());     //remove shipment from shipmentlist in warehouse
        temp.setCarrier(this);
        
        if (manifest.peek() == null || manifest.peek().getSize() > capacity){   //if nothing else in the manifest or no more capacity
            isLoading = false;
            destination = storage.peek().getDestination();
            if (destination.getLocation().equals(location)){
                unload();
            }
            
        }else{  //go to next destination
            destination = manifest.peek().getPickUp();
            if (destination.getLocation().equals(location)){
                load();
            }
        }
        moveAngle = location.calculateAngle(destination.getLocation());
        temp.getPickUp().getTruckOut(this);
    }
    
    /**
     * Helper method that is called when truck state is set to UNLOADING. Removes the last element from the storage stack, updates the shipment list 
     * of the warehouse, and checks for truck activity moving forward. 
     */
    private void unload(){
        Shipment temp = storage.pop();
        destination.addShipment(temp);
        temp.setCarrier(null);
        temp.setDelivered();
        if (storage.isEmpty()){
            if (manifest.getShipmentList().isEmpty()){
                isDone = true;  //both manifest and storage are empty, done
            }else{
                isLoading = true;   //manifest is not empty, going to pick up
                destination = manifest.peek().getPickUp();
                if (destination.getLocation().equals(location)){
                    load();
                }
            }
        }else{
            destination = storage.peek().getDestination();  //going to next dropoff
            if (destination.getLocation().equals(location)){
                unload();
            }
        }
        moveAngle = location.calculateAngle(destination.getLocation());
        temp.getDestination().getTruckOut(this);
    }
    
    
    public Manifest getManifest(){
        return manifest;
    }
    
    public Stack<Shipment> getStorage(){
        return storage;
    }
    
    public void setState(TruckState t){
        state = t;
    }
    
    public TruckState getState(){
        return state;
    }
    
    public boolean isLoading(){
        return isLoading;
    }
    
    public boolean isDone(){
        return isDone;
    }
    
    public Warehouse getDestination(){
        return destination;
    }
    
    public void setDestination(Warehouse w){
        if(w == null) {
            throw new IllegalArgumentException("Warehouse passed is null");
        }
    
        if(location == null) {
            throw new IllegalStateException("Location in Truck is null");
        }
        destination = w;
        moveAngle = location.calculateAngle(destination.getLocation());
    }
    
    public String toString(){
        return "Truck ID " + ID + " Location: "+ location + " SPEED: " + speed; 
    }
    
    public String log_status(){
        if (isDone){
            return toString() + "DONE";
        }
        switch(state){
            case MOVING:
                if (isLoading){
                    return toString() + " moving to Warehouse " + destination.getID() + " to pick up Shipment " + manifest.peek().getID();
                }
                return toString() + " moving to Warehouse " + destination.getID() + " to drop off Shipment " + storage.peek().getID();
            case LOADING:
                return "Truck " + ID + " loading shipment";
            case UNLOADING:
                return "Truck " + ID + " unloading shipment";
            case WAITING:
                return "Truck " + ID + " waiting";
            default:
                return "";
        }
    }
    
    public void setManifest(Manifest m){
        manifest = m;
    }
    
    public Location getLocation(){
        return location;
    }
    
    public int getID(){
        return ID;
    }
}
