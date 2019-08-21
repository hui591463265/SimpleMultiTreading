package project1;
import java.util.Vector;
import java.util.concurrent.Semaphore;
public class garage extends Thread{
	static int count=2;
	private static int carparked=0;
	public static Vector<Object> Garage = new Vector<Object>();
	public static Vector<Object> Att = new Vector<Object>();
	Semaphore s = new Semaphore(1);
	public static long time = System.currentTimeMillis();
	public void msg(String m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
		}
	public void run() {
		Object attendent=new Object();
		while(carparked<15){
			//wait for commuters to arrive
			synchronized(attendent){
				if(NoCommuters(attendent)){
					while(true){
						try {
							msg("waiting for commuters");
							//no cars here so wait for cars
							attendent.wait();
							break;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							continue;
						}
					}
				}
			}
			
			try {
				//count need to be synchronzied so i used mutex
				s.acquire();
				count--;
				//park the car for the commuter
				Thread.sleep((long)(Math.random() * 8000));
				parkcar();
				carparked++;
				count++;
				s.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//after came back from parking, tell the next in line that attendant is ready for service
			if(count>0&&commuters.waitingGarage.size()>0){
				synchronized(commuters.waitingGarage.elementAt(0)){
					commuters.waitingGarage.elementAt(0).notify();
				}
				commuters.waitingGarage.removeElementAt(0);
			}
				
		}

	}
	//signal the commuter that his car is parked
	private void parkcar() {
		if(commuters.waitingToLeaveGarage.size()>0){
			synchronized(commuters.waitingToLeaveGarage.elementAt(0)){
				commuters.waitingToLeaveGarage.elementAt(0).notify();
			}
			commuters.waitingToLeaveGarage.removeElementAt(0);
			Garage.removeElementAt(0);
		}
	}
	
	private synchronized boolean NoCommuters(Object attendent) {
		boolean a;
		if(Garage.size()>0){
			a=false;
		}
		else{
			Att.addElement(attendent);
			a=true;
		}
		return a;
	}
}
