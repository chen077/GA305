package GA;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import junit.framework.Assert;


public class KnapsackProblemGA {

	public class Chromosome implements Comparable<Chromosome>{
		private boolean[] chrom;  //gene
		private double value=0;   //fitness
		
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		
		private Chromosome(int size) {
			if(size <= 0 ) return; // size equal to the length of objectWeight
			this.chrom = new boolean[size];
			for(int i=0; i<size; i++) {
				chrom[i] = random.nextDouble() >=0.5;//完全没有问题，初始化随机,几率相同
			}
			this.value = calculateValue(this);
		}
	
		@Override
		public int compareTo(Chromosome o) {
			// TODO Auto-generated method stub
			if(this.value > o.value ) return 1;  
			else if(this.value < o.value) return -1;
			else return 0;
		}
	}
	
	private Random random = new Random();
	private double capacity = 6750;
	private int maxGeneration = 1000;
	private int popNum = 500;  //length of population (even)
	private static Chromosome[] pop = null;
	private double totalValue=0;   //total fitness of the entire generation
	private double bestValue=0;    //best fitneses
	private double indiMutation = 0.01; //mutation chance of individual
	private double chroMutation = 0.1; //mutation chance of gene
	private double indiCross = 0.8;   //crossorver chance
	private double[] objectWeight = {54,183,106,82,30,58,71,166,117,190,90,191,205,128,110,89,63,6,140,86,
			30,91,156,31,70,199,142,98,178,16,140,31,24,197,101,73,16,73,2,159,71,102,144,151,27,131,209,
			164,177,177,129,146,17,53,64,146,43,170,180,171,130,183,5,113,207,57,13,163,20,63,12,24,9,42,6,
			109,170,108,46,69,43,175,81,5,34,146,148,114,160,174,156,82,47,126,102,83,58,34,21,14};
	private double[] objectValue  = {597,596,593,586,581,568,567,560,549,548,547,529,529,527,520,491,482,
			478,475,475,466,462,459,458,454,451,449,443,442,421,410,409,395,394,390,377,375,366,361,
			347,334,322,315,313,311,309,296,295,294,289,285,279,277,276,272,248,246,245,238,237,232,231,
			230,225,192,184,183,176,171,169,165,165,154,153,150,149,147,143,140,138,134,132,127,124,123,114,
			111,104,89,74,63,62,58,55,48,27,22,12,6,0}; 
	private int n = objectWeight.length; //number of objects--length of chromosome
	private Chromosome bestCase = new Chromosome(n);
	
// 	constructor
//	public KnapsackProblemGA(int c) {
//		this.maxGeneration=c;
//	}
	
	//initialize population
	protected void initPopulation() {
		pop = new Chromosome[popNum];
		for(int i=0; i<popNum; i++) {
			pop[i] = new Chromosome(n);
			while(pop[i].value == 0) {   //capacity 设值过小可能导致无限循环
				pop[i] = new Chromosome(n);
			}
		}
	}
	
	//calculate total fitness
	private void calculateTotal1() {
		totalValue = 0;
		for(int i=0; i<pop.length; i++) {
			double value = calculateValue(pop[i]);
			pop[i].setValue(value);
			totalValue += value;
		}
	}
	
	//calculate individual fitness ---没问题
	private double calculateValue(Chromosome chromosome) {
		double value = 0;  
        double weight = 0;  
        for (int i = 0; i < chromosome.chrom.length; i++) {  
            if (chromosome.chrom[i]) {  
                weight += objectWeight[i];  
                value += objectValue[i];  
            }  
        }  
        if (weight > capacity) {  //if weight exceeded capacity 
            return 0;  
        } else {  
            return value;  
        }  
	}
	
	//record the best fitness
	private void recordBest1() {
		for(int i=0; i<pop.length; i++) {
			if(pop[i].value > bestValue) {
				bestValue = pop[i].value;
				for(int j=0; j<n; j++) {
					bestCase.chrom[j]=pop[i].chrom[j];
				}
				bestCase.setValue(bestValue);
			}
		}
	}
	
	//replace the worst individual with the best one.
	private void replace() {
		for(int i=0; i<popNum; i++) {
			if(pop[i].value <= 0) {
				pop[i] = bestCase;
			}
		}
	}
	
	//sort pop array and then select top 1% individual
	//while loop till chromosomes fill the array
	 private void select11() {
     	Chromosome[] tmpPopulation = new Chromosome[popNum];
     	for(int a=0; a<popNum; a++) {
     		tmpPopulation[a] = new Chromosome(n);
     	}
     	Arrays.sort(pop,Collections.reverseOrder());
     	int A = popNum/100;
     	for(int i=0; i<A; i++) {
     		tmpPopulation[i] = pop[i];
     	}
     	for(int i = A; i < popNum; i++) {  //i=0 会导致种群之中，从而收敛过快，仅取到局部最优解
         	int a = selectId();
             for(int j = 0; j < n; j++) {  
                 tmpPopulation[i].chrom[j] = pop[a].chrom[j];  
             }  
         }
         pop = tmpPopulation;  
     }
	 
	//轮盘赌法选择个体：Roulette Wheel Selection// 最大问题--为什么一开始sum不为0？
     private int selectId(){
 		double index = Math.random()*totalValue;  //[0,1) not equally divided if >=
 		double sum = 0;
 		for(int i=0;i<popNum;i++) {
 			sum+= pop[i].value;
 			if(sum>index) return i;
 		}
 		return 0;
 	}
     
    //crossover1
   	private void crossover1() {
  		 for(int i = 0; i < popNum; i = i + 2)  
               for(int j = 0; j < n; j++) {  
                   if(Math.random() < indiCross) {  
                       boolean tmp = pop[i].chrom[j];  
                       pop[i].chrom[j] = pop[i+1].chrom[j];  
                       pop[i+1].chrom[j] = tmp;  
                   }  
               }  
    }
   	
	//mutation
	private void mutation1() {
		for(int i = 0; i < popNum; i++) {
			if(Math.random() < indiMutation) {
				for(int j=0; j<n; j++) {  
	                if(Math.random() < chroMutation) {  
	                	boolean org = pop[i].chrom[j];
	                	pop[i].chrom[j] = !org;
	                }  
	            }  
			}
		}
	}
	
	private void solve(){
		initPopulation();
		for(int i=0; i<maxGeneration; i++) {
			calculateTotal1();
            recordBest1();  
            replace();
            select11();
            crossover1();
            mutation1(); 
		}
		System.out.println("Best Solution: ");
		int totalWeight = 0;   
        for(int i = 0; i < n; i++) {  
            if(bestCase.chrom[i]){  
                totalWeight += objectWeight[i]; 
                System.out.print("1");
            }else   System.out.print("0");
        }  
        System.out.println();
        System.out.println("total profit:" + bestValue); 
        System.out.println("total weight:" + totalWeight); 
	}
	 
	public static void main(String[] args){
		// TODO Auto-generated method stub
			KnapsackProblemGA  g = new KnapsackProblemGA();
			g.solve();
		
		
	}
	
	@Test
    public void test1() {
        KnapsackProblemGA g = new KnapsackProblemGA();
        Chromosome chr1 = new Chromosome(100);
        for (int i = 0; i < chr1.chrom.length; i++) {
            chr1.chrom[i] = false;
        }
        double c1 = g.calculateValue(chr1);
        Assert.assertEquals(0.0, c1);
    }
    
    @Test
    public void test2(){
        KnapsackProblemGA g = new KnapsackProblemGA();
        Chromosome chr1 = new Chromosome(100);
        for (int i = 0; i < chr1.chrom.length; i++) {
            chr1.chrom[i] = true;
        }
        double c1 = g.calculateValue(chr1);
        Assert.assertEquals(0.0, c1);
    }
    
    @Test
    public void test3(){
        KnapsackProblemGA g = new KnapsackProblemGA();
        Chromosome chr1 = new Chromosome(100);
        for (int i = 0; i < 10; i++) {
            chr1.chrom[i] = true;
        }
        for (int i = 10; i < chr1.chrom.length; i++) {
            chr1.chrom[i] = false;
        }
        double c1 = g.calculateValue(chr1);
        Assert.assertEquals(5745.0, c1);
    }	
    
    @Test
    public void test4() {
    	KnapsackProblemGA g = new KnapsackProblemGA();
    	Assert.assertEquals(null, pop);
    	g.initPopulation();
    	g.calculateTotal1();
    	Assert.assertNotSame(0,totalValue);
    }
    
    @Test
    public void test5() {
    	KnapsackProblemGA g = new KnapsackProblemGA();
    	Chromosome[] p1 = new Chromosome[popNum];
    	Assert.assertNotSame(p1, pop);
    	p1=pop;
    	Assert.assertEquals(p1, pop);
    	g.initPopulation();
    	int countT=0;
    	int countF=0;
    	for(int i=0; i<popNum; i++) {
    		for(int j=0; j<n; j++) {
    			if(pop[i].chrom[j])  countT++;
    			else countF++;
    		}
    	}
    	g.crossover1();
    	int count1=0;
    	int count2=0;
    	for(int i=0; i<popNum; i++) {
    		for(int j=0; j<n; j++) {
    			if(pop[i].chrom[j])  count1++;
    			else count2++;
    		}
    	}
    	Assert.assertEquals(countT, count1);
    	Assert.assertEquals(countF, count2);
    	Assert.assertNotSame(p1, pop);
    }
	
    @Test //mutation
    public void test6() {
    	KnapsackProblemGA g = new KnapsackProblemGA();
    	Chromosome[] p1 = new Chromosome[popNum];
    	Assert.assertNotSame(p1, pop);
    	p1=pop;
    	Assert.assertEquals(p1, pop);
    	g.initPopulation();
    	int count1=0;
    	int count2=0;
    	for(int i=0; i<popNum; i++) {
    		for(int j=0; j<n; j++) {
    			if(pop[i].chrom[j])  count1++;
    			else count2++;
    		}
    	}
    	g.mutation1();
    	int countT=0;
    	int countF=0;
    	for(int i=0; i<popNum; i++) {
    		for(int j=0; j<n; j++) {
    			if(pop[i].chrom[j])  countT++;
    			else countF++;
    		}
    	}
    	Assert.assertNotSame(countT, count1);
    	Assert.assertNotSame(countF, count2);
    	Assert.assertNotSame(p1, pop);
    }

}
