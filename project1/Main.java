package project1;
import java.util.Random;
import project1.commuters; 
import project1.garage;
public class Main extends Thread{
	static String A = "A";
	static String B = "B";
	public static final String[] OfficeR = {"A","B"};
	public static final String[] EZpass = {"EZpass","NoEZpass"};
	public static void main(String[] args) throws InterruptedException {	
	///15 commuters with randomly assigned route to office and ezpass
	for (int i=0; i<15; i++){
		Random random = new Random();
		String randomOfficeRoute = OfficeR[random.nextInt(OfficeR.length)];
		String randomEzpassSubscription = EZpass[random.nextInt(EZpass.length)];
		Thread commuter=new commuters(randomOfficeRoute, randomEzpassSubscription);
		commuter.setName("commuter"+i);
		commuter.start();
	}
	for(int i=0; i<2; i++){
		Thread garage=new garage();
		garage.setName("attendent"+i);
		garage.start();
	}

	Thread trainA=new train(A);
	Thread trainB=new train(B);
	trainA.start();
	trainB.start();
			
}
}
