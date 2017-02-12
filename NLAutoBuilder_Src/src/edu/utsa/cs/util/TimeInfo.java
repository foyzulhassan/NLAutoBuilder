package edu.utsa.cs.util;

public class TimeInfo {
    private long sum;
    private long start;
    public TimeInfo(){
	this.sum = 0L;
    }
    public long getSum(){
	return this.sum;
    }
    public void setStart(long current){
	this.start = current;
    }
    public long getStart(){
	return this.start;
    }
    public void addToSum(long period) {
	this.sum = this.sum + period;
    }
}
