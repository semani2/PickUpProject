package com.example.pickupv1;

public class PickUpDriver {
	
	private String driverName;
	
	private double driverLongitude;
	
	private double driverLatitude;
	
	public PickUpDriver(String driverName, double driverLongitude, double driverLatitude)
	{
		this.driverName = driverName;
		this.driverLatitude = driverLatitude;
		this.driverLongitude = driverLongitude;
	}
	
	public String getDriverName(){
		return driverName;
	}
	
	public void setDriverName(String driverName){
		this.driverName = driverName;
	}
	
	public double getDriverLongitude(){
		return driverLongitude;
	}
	
	public void setDriverLongitude(double driverLongitude){
		this.driverLongitude = driverLongitude;
	}
	
	public double getDriverLatitude(){
		return driverLatitude;
	}
	
	public void setDriverLatitude(double driverLatitude){
		this.driverLatitude = driverLatitude;
	}
	
	

}
