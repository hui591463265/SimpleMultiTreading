package project1;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class train extends Thread{
	public static Vector<Object> waitingA = new Vector<Object>();
	public static Vector<Object> waitingB = new Vector<Object>();
	public static long time = System.currentTimeMillis();
	private String line;
	private static int count1=0;
public train(String a) {
		this.line=a;
	}

public void msg(String m) {
System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
}

public void run() {
	while(count1<15){
		try {
			//train arrived and stay in the station
			Thread.sleep((long)(Math.random() * 15000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (line=="A") boardingA();
		else 		   boardingB();
	}
	msg("all commuters left");
}
//make a boarding call for all passagers waiting for line B
private void boardingB() {
	if(waitingB.size()>0){
	//capacity of 10 so tell the first 10 in line(even it is nyc)
		for(int i=0; i<10; i++){
			synchronized(waitingB.elementAt(0)){
				waitingB.elementAt(0).notify();
				count1++;
			}
			waitingB.removeElementAt(0);
			//all passengers boarded 
			if(waitingB.size()==0) break;
		}
	}
	
}
//make a boarding call for all passagers waiting for line B
private void boardingA() {
	//capacity of 10 so tell the first 10 in line
	if(waitingA.size()>0){
		for(int i=0; i<10; i++){
			synchronized(waitingA.elementAt(0)){
				waitingA.elementAt(0).notify();
				count1++;
			}
			waitingA.removeElementAt(0);
			//all passengers boarded
			if(waitingA.size()==0) break;
		}
	}
}
}
