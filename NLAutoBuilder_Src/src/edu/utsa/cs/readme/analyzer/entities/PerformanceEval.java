package edu.utsa.cs.readme.analyzer.entities;

public class PerformanceEval {
	public double getPrecision() {
		return Precision;
	}

	public void setPrecision(double precision) {
		Precision = precision;
	}

	public double getRecall() {
		return Recall;
	}

	public void setRecall(double recall) {
		Recall = recall;
	}

	public double getFMeasure() {
		return FMeasure;
	}

	public void setFMeasure(double fMeasure) {
		FMeasure = fMeasure;
	}

	public int getItemcount() {
		return Itemcount;
	}

	public void setItemcount(int itemcount) {
		Itemcount = itemcount;
	}


	private double Precision;
	private double Recall;
	private double FMeasure;
	private int Itemcount;
	
	public PerformanceEval()
	{
		this.Precision=0;
		this.Recall=0;
		this.FMeasure=0;
		
	}
	
	public void Update(double p, double r, double f)
	{
		this.Precision=this.Precision+p;
		this.Recall=this.Recall+r;
		this.FMeasure=this.FMeasure+f;
		
	}
	
	
	public void PrintAverage(int itemcount)
	{
		this.Itemcount=itemcount;
		System.out.println("Average Precision="+(this.Precision/this.Itemcount));
		System.out.println("Average Recall="+(this.Recall/this.Itemcount));
		System.out.println("Average FMeasure="+(this.FMeasure/this.Itemcount));
		
		
	}

}
