package project2;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import project2.Main;
public class judge extends Thread {
	Semaphore mutex2;
	static Semaphore waitracers= new Semaphore(0);
	static Semaphore waitgroup= new Semaphore(0);
	public judge(Semaphore SignalJudge) {
		this.mutex2=SignalJudge;
	}
	public static Vector<String> racereport = new Vector<String>();
	public static Vector<String> forestreport = new Vector<String>();
	public static Vector<String> mountainreport = new Vector<String>();
	public static Vector<String> riverreport = new Vector<String>();
	public static long time = System.currentTimeMillis();
	public void msg(String m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+":"+m);
		}
	public void run() {
		
		try {
			waitracers.acquire();										//judge will lock itself to wait for last racer to finish race
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		msg("show report");												//after last racer unlock the waitracer semaphore
		showreport();													//judge will give the report
		
		for(int i=0;i<10;i++){											//after the report judge will unlock all racers waiting for the report
			Main.finish.release();
		}
		
		
		try {
			msg("waiting for all racers to leave");
			Main.finish1.acquire();										//will wait for all racers to go home
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg("all racers left");
		
	}
	public void showreport() {
		for (int j=0;j<racereport.size();j++){
			System.out.println(racereport.elementAt(j));
		}
		/*for (int k=0;k<racereport.size();k++){
			System.out.println(forestreport.elementAt(k));
			System.out.println(mountainreport.elementAt(k));
			System.out.println(riverreport.elementAt(k));
		}*/
		
	}
	public static void recordentire(String racer, double time) {
		racereport.add(racer+" used "+(time/1000)+" seconds to finish the whole race");
	}
	public static void recordforest(String racer, double time) {
		forestreport.add(racer+" used "+(time/1000)+" seconds to finish the forest race");
	}
	public static void recordmountain(String racer, double time) {
		mountainreport.add(racer+" used "+(time/1000)+" seconds to finish the mountain race");
	}
	public static void recordriver(String racer, double time) {
		riverreport.add(racer+" used "+(time/1000)+" seconds to finish the river race");
	}

}
