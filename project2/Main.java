package project2;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.*;
import project2.judge;
import project2.race;

public class Main {
public static Vector<Thread> friend = new Vector<Thread>();
public static StringBuilder magicword=new StringBuilder(4);
static Semaphore finish1=new Semaphore(0);
static Semaphore finish=new Semaphore(0);
public static void main(String[] args) {
		// TODO Auto-generated method stub
	Semaphore mutex1= new Semaphore(1);
	Semaphore SignalJudge= new Semaphore(0);
	int numLine=3;
	Random random = new Random();
	for(int i=0; i<4; i++){
		char c = (char)('a' + random.nextInt(4));
		magicword.append(c);}
	System.out.println("magicword is "+Main.magicword);
	for (int i=0; i<10; i++){
		Thread racer = new race(mutex1,SignalJudge,numLine);
		racer.setName("racer"+i);
		friend.addElement(racer);
		racer.start();
	}
		Thread judge=new judge(SignalJudge);judge.setName("judge");
		judge.start();
}
public static Thread getfriend(Thread currthread, int j){//use to get a thread's friend he needed to wait before heading home
	Thread friendthread = null;
	for(int i=0;i<j;i++){
		if(friend.elementAt(i)==currthread){
			friendthread=friend.get(i+1);
			return friendthread;
		}
	}
	return friendthread;
}
public static Thread lastracer(){ //keep track of the last racer in friend list
	return friend.lastElement();
}
}
