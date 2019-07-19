package com.aimir.fep;


class SingleToneObject{
	public static SingleToneObject so;
	private volatile int count;
	
	public static synchronized SingleToneObject getInstance(){
		if(so == null){
			so = new SingleToneObject();
		}
		return so;
	}
	
	public void print(String aa) throws InterruptedException{
		for(int i=0; i<5; i++){
			System.out.println(aa + " call so - " + count++);
			Thread.sleep(20000);			
		}

	}
}


class Job implements Runnable{
	private String name;
	public Job(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		try {
			SingleToneObject so = SingleToneObject.getInstance();
			so.print(name);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public class SingletoneTest {
	
	public static void main(String[] args){
		try {
			for(int i=0; i<10; i++){
				Thread t = new Thread(new Job("aa-" + i));
				t.start();
			}
			
			Thread.sleep(5000);
			System.out.println("===============================");
			
			for(int j=0; j<10; j++){
				Thread t = new Thread(new Job("가가-" + j));
				t.start();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

