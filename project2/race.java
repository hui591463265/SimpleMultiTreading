package project2;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.*;
import project2.Main;
import project2.judge;

//race thread contains all obstacles, rest and gohome
public class race extends Thread{
	boolean readytogo=false;
	Semaphore mutex1;
	Semaphore mutex2;
	Semaphore mutex3=new Semaphore(1);
	Semaphore mutex4=new Semaphore(1);
	Semaphore mutex5=new Semaphore(1);
	int numLines;
	public static int racerstotal=10;
	public static int Numracers=0;
	public static int racers=0;
	public static Vector<Thread> finishr = new Vector<Thread>();
	public race(Semaphore mutex1, Semaphore SignalJudge, int numLine) {
		this.mutex1=mutex1;
		this.mutex2=SignalJudge;
		this.numLines=numLine;
	}
	public static long time = System.currentTimeMillis();
	public void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+":"+m);
	}
	//public static Vector<Thread> v = new Vector<Thread>();
	
	@Override
	public void run() {
		final long startTime = System.currentTimeMillis();
		rest();
		forest();
		rest();
		mountain();
		rest();
		river();
		final long endTime = System.currentTimeMillis();
		judge.recordentire(Thread.currentThread().getName(), endTime-startTime);
		GoHome();
	}
	
	public void rest(){									  //sleep for random time
		try {
				Thread.sleep((long)(Math.random() * 10000));
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
	}
	
	public void forest(){
		final long foreststartTime = System.currentTimeMillis();
		Random random = new Random();
		int r = (int) (Math.random() * (500 - 300)) + 300;//between 300 and 500 tries to find word
        StringBuilder word=new StringBuilder(4);
			for(int j=0; j<r; j++){						  //randomly search for word
				for(int i=0; i<4; i++){
					char d = (char)('a' + random.nextInt(4));
					word.append(d);
				}
				if(word.toString().equals(Main.magicword.toString())){ // word found then move on
					 msg("found "+word);
					final long forestendTime = System.currentTimeMillis();
					judge.recordforest(Thread.currentThread().getName(), forestendTime-foreststartTime);
					return;
				}
				word.delete(0, word.length());
			}
		msg("can't found word");									   // after for loop can't find word 
		final long forestendTime = System.currentTimeMillis();
		judge.recordforest(Thread.currentThread().getName(), forestendTime-foreststartTime);
	}
	
	public void mountain() {
		// TODO Auto-generated method stub
		final long MountainstartTime = System.currentTimeMillis();
		msg("Passing Passage");
		try {
			mutex1.acquire();										  // only one racer can go through passage a time so use mutex(1)
		} catch (InterruptedException e) {							  // one racer goes in and lock the mutex
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg("crossed passage");
		mutex1.release();                                             //after he crossed passage, release the lock so next racer in queue can go in
		final long MountainendTime = System.currentTimeMillis();
		judge.recordmountain(Thread.currentThread().getName(), MountainendTime-MountainstartTime);
	}
		
	
	public void river() {                                             
		final long riverstartTime = System.currentTimeMillis();		
		try {
			mutex4.acquire();										  //mutex(1) to enforce mutual exclusion of numracer++ and if statements	      
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			Numracers++;											  //record number of racers at river
			if((Numracers%numLines!=0)&&(Numracers!=racerstotal)){	  //if group size of numlines not formed and if it is not the last racer thread
				mutex1.release();									  //release the mutex lock
				try {
					judge.waitgroup.acquire();						  //use waitgroup semaphore from judge to block all racers until group is formed
					msg("at river waiting to form group");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{                                                     
				mutex4.release();									  //release the mutex lock
				for(int k=0;k<(numLines-1);k++){					  //judge will release number of racers that are in queue that formed a group
					judge.waitgroup.release();	
					final long riverendTime = System.currentTimeMillis();
					judge.recordriver(Thread.currentThread().getName(), riverendTime-riverstartTime);
				}
			}
			msg("group formed and the group crossing river");
			
	}
	
	public void GoHome(){
		finishr.addElement(Thread.currentThread());
		msg("finished race");							
		try {
			mutex3.acquire();										  //after racer arrive it acquires a mutex(1) lock
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		racers++;													  //record number of racers finished the race
		if(racers==10){												  //if it is the last racer
			msg("got here as the last racer");
			mutex3.release();										  //release mutex(1)
			judge.waitracers.release();								  //let judge know all racers are here and judge can give its report
			try {
				Main.finish.acquire();								  //last racer wait for judge's report
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			mutex3.release();										 //release mutex(1)
			try {
				msg("waiting for last racer");						 //if not all racers are here, wait for all racers
				Main.finish.acquire();								 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		while(readytogo!=true){
			if(Thread.currentThread()!=Main.lastracer()){            //if the thread is not the lastracer in racer friend vector, continue
				if(Main.getfriend(Thread.currentThread(), finishr.size()).isAlive()){
					try {											 //check if currentthread's friend is alive in race
						msg("wait for friend");
						readytogo=false;
						Main.getfriend(Thread.currentThread(), finishr.size()).join();
																     //if friend in race, he will join/waitfor his friend
					} catch (InterruptedException e) {
						msg("friend left already");
						e.printStackTrace();
					}
				}
																	 //msg("friend left already");
				readytogo=true;										 //his friend not in the race so he can leave
			}
			readytogo=true;											 //if the thread is the lastracer in racer friend vector, just go home
		}
		msg("Going home");
		//Thread.currentThread().interrupt();
		//finishr.remove(Thread.currentThread());						 //remove the racer from finishr vector
		Main.friend.remove(Main.friend.lastElement());				 //remove the racer from friend vector
		if(Main.friend.size()==0){									 //if no more races in the race, tell the judge to go home
			Main.finish1.release();
		}
	}
	
}