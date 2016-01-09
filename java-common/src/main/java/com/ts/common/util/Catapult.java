package com.ts.common.util;

public class Catapult {
	private double myRangeMeters, myRangeFeet;
	private int[] myInitialVel, myRadians;

	Catapult(int[] initialVel, int[] radians)
	{
	    myRangeMeters = 0.0;
	    myRangeFeet = 0.0;
	    myInitialVel = initialVel;
	    myRadians = radians;
	}

	public void calcProjectileDist()
	{
		double [] rangeMeters=new double[myInitialVel.length];
		for(int i =0; i<myInitialVel.length;i++){
			rangeMeters[i]=(Math.pow(myInitialVel[i], 2) * Math.sin(2 * myRadians[i])) / 9.8;
		}
	}

	public void convertMetersToFeet()
	{
	    myRangeFeet = myRangeMeters * 3.28084;
	}

	public double getProjectileDist()
	{
	    return myRangeFeet;
	}
}
