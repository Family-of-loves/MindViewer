package com.example.mindviewer;

public class Brainwaves {
	
	/* Creator Singleton Pattern Obj*/
	private volatile static Brainwaves uniqueInstance;
	
	/* EEG eSense Values */
	int attention;
	int meditation;
	
	/* EEG Power Signal*/
	double delta;
	double theta;
	double lowAlpha;
	double highAlpha;
	double lowBeta;
	double highBeta;
	double lowGamma;
	double highGamma;
	
	public Brainwaves (){}
	
	/* Define Singleton Pattern */
	public static Brainwaves getInstance(){
		if(uniqueInstance == null){
			//synchronized(Brainwaves.class){
				//if(uniqueInstance == null)
					uniqueInstance = new Brainwaves();
			//}
		}
		return uniqueInstance;
	}
	
	void setAtt(int val)	{	attention = val;	}
	void setMed(int val)	{	meditation = val;	}
	
	void setDt(double val)	{	delta = val;	}
	void setTh(double val)	{	theta = val;	}
	void setLa(double val)	{	lowAlpha = val;	}
	void setHa(double val)	{	highAlpha = val;	}
	void setLb(double val)	{	lowBeta = val;		}
	void setHb(double val)	{	highBeta = val;	}
	void setLg(double val)	{	lowGamma = val;	}
	void setHg(double val)	{	highGamma = val;	}
	
	int getAtt(){ return this.attention; }
	int getMed(){ return this.meditation; }
	
	double setDt(){	return this.delta;	}
	double setTh(){	return this.theta;	}
	double setLa(){	return this.lowAlpha;	}
	double setHa(){	return this.highAlpha;	}
	double setLb(){	return this.lowBeta;	}
	double setHb(){	return this.highBeta;	}
	double setLg(){	return this.lowGamma;	}
	double setHg(){	return this.highGamma;	}
	
	
}