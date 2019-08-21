package project1;
import java.util.Vector;
import java.util.concurrent.Semaphore;
public class commuters extends Thread{
	String OfficeRoute;
	String EzpassSubscription;
	commuters(String randomOfficeRoute, String randomEzpassSubscription) {
		this.OfficeRoute=randomOfficeRoute;
		this.EzpassSubscription=randomEzpassSubscription;
	}
	private static boolean EZLaneOccupied=false;
	private static boolean CashLaneOccupied=false;
	private static Vector<Object> waitingEZ = new Vector<Object>();
	private static Vector<Object> waitingCash = new Vector<Object>();
	public static Vector<Object> waitingGarage = new Vector<Object>();
	public static Vector<Object> waitingToLeaveGarage = new Vector<Object>();
	public static long time = System.currentTimeMillis();
	Semaphore t = new Semaphore(1);
	public void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
	}
	Object box = new Object();
	public void run() {
		try {
			//drives from home to new york city
			Thread.sleep((long)(Math.random() * 5000));
		} catch (InterruptedException e) {	
			e.printStackTrace();
		}
		GWB(EzpassSubscription);
		ToGarage();
		ToTrain();
	}
	//arriving bridge
	private void GWB(String EzpassSubscription){
		msg("arrived GWB");
		//if has ezpass goto ezlane;
		if(EzpassSubscription=="EZpass") {
			ezlane();
		//finished paying so leave
			exitezlane();
		}
		//no ezpass goto cashlane:(
		else {
			cashlane();
		//finished paying so leave
			exitcashlane();
		}
	}
	private void ezlane() {
		Object box = new Object();
			msg("has EZ");
			synchronized (box) {
				//check if the pay station is occupied
				if(EZLaneOccupied(box)){
					while(true){
					try {
						msg("Lane occupied");
						//wait until the car infront leaves
						box.wait();break;
					} catch (InterruptedException e) {
						continue;
					}
					}
				}
				
				try {
					//pay station is not occupied or being signaled
					msg("Lane open");
					EZLaneOccupied=true;
					Thread.sleep((long)(Math.random() * 3000));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}

	private void exitezlane() {
		EZLaneOccupied=false;
		//tell the car behind that he can enter using FCFS
		if(waitingEZ.size()>0){
			synchronized(waitingEZ.elementAt(0)){
				waitingEZ.elementAt(0).notify();
			}
			waitingEZ.removeElementAt(0);
			EZLaneOccupied=true;
		}
		msg("exited");
	}
	
	private synchronized boolean EZLaneOccupied(Object box) {
		boolean status;
		if(EZLaneOccupied){
			//get in line using vector FCFS
			waitingEZ.addElement(box);
			status=true;
		}else{
			EZLaneOccupied=true;
			status=false;
		}
		return status;
	}
	private void cashlane() {
		Object box = new Object();
		msg("No EZ");
		synchronized (box) {
			//check if the pay station is occupied
			if(CashLaneOccupied(box)){
				while(true){
				try {
					msg("Lane occupied");
					//wait until the car infront leaves
					box.wait();break;
				} catch (InterruptedException e) {
					continue;
				}
				}
			}
			
			try {
				msg("Lane open");
				CashLaneOccupied=true;
				Thread.sleep((long)(Math.random() * 5000));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
			
	private synchronized boolean CashLaneOccupied(Object box) {
		boolean status;
		if(CashLaneOccupied){
			//get in line using vector FCFS
			waitingCash.addElement(box);
			status=true;
		}else{
			CashLaneOccupied=true;
			status=false;
		}
		return status;
	}

	private void exitcashlane() {
		CashLaneOccupied=false;
		if(waitingCash.size()>0){
			synchronized(waitingCash.elementAt(0)){
				//tell the car behind that he can enter using FCFS
				waitingCash.elementAt(0).notify();
			}
			waitingCash.removeElementAt(0);
			CashLaneOccupied=true;
		}
		msg("exited");
		
	}
	//arrived Parking Garage
	private void ToGarage() {
		Object box1= new Object();
		garage.Garage.addElement(box1);
		//signal attendants that are waiting for the arrival of commuters 
		if(garage.Att.size()>0){
			synchronized(garage.Att.elementAt(0)){
				garage.Att.elementAt(0).notify();
			}
			garage.Att.removeElementAt(0);
		}
		//check for available attendants
		synchronized (box1) {
			if(noAttendent(box1)){
				while(true){
					try {
						msg("No Attendent");
						box1.wait();
						msg("attendent availiable");
						break;
					} catch (InterruptedException e) {
						continue;
					}
					}
			}
		}
		//wait for attendant to park his car
		waitingToLeaveGarage.addElement(box1);
		synchronized(box1){
			try {
				msg("waitingforattendenttoparkcar");
				box1.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		msg("headtotrain");
	}
	private synchronized boolean noAttendent(Object box1) {
		if(garage.count==0){
			//FCFS get in line using vector
			waitingGarage.addElement(box1);
			return true;
		}
		return false;
	}
	//arrived train station
	private void ToTrain() {
		Object box2=new Object();
		//wait in line according to OfficeRoute
		if(this.OfficeRoute=="A"){
			synchronized (box2) {
				while(true){
					try {
						train.waitingA.addElement(box2);
						msg("waiting for Train A");
						//waiting to board the train
						box2.wait();
						msg("On train and head to work");
						break;
					} catch (InterruptedException e) {
						continue;
					}
					}
			}
		}
		else{
		//wait in line according to OfficeRoute
			synchronized (box2) {
				while(true){
					try {
						train.waitingB.addElement(box2);
						msg("waiting for Train B");
						//waiting to board the train
						box2.wait();
						msg("On train and head to work");
						break;
					} catch (InterruptedException e) {
						continue;
					}
					}
			}
		}
		
	}
	
	
}