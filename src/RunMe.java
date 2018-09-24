import java.util.Arrays;

public class RunMe {

	public void expectationMaximization(Working oldWork,int R,double p){

		boolean stop=false;
		
		//EM will stop when (bdj(k+1)-bdj(k))^2<0.000001 for d=1,2 and j=1,..., M
		while(!stop){	
			int M = oldWork.workMix.M;
			boolean auxStop=true;
			//We start we two equal variables.
			//Then we will iterate over the new variable and compare with the original one
			//If the the new is better it will be kept. Otherwise we go back to the previous one.
			Working newWork = oldWork;
			
			//Iterate aj for all curves
			for (int j=1;j<=M;j++){
				
				newWork.nextAj(j);

				if (newWork.totalProb().compareTo(oldWork.totalProb())==1){
					oldWork=newWork;
				}
			}
			
			//Iterate wj for all the curves
			
			for (int j=1;j<=M;j++){
				/*
				double sum1=0;
				for (int k=1; k<=M;k++){
					double[] thetaj=oldWork.work_mix.ParamList.get(k-1);
					sum1=sum1+thetaj[0];
				}
				System.out.println("sum1 = " + sum1);*/
				
				newWork.nextWj(j);

				if (newWork.totalProb().compareTo(oldWork.totalProb())==1){
					oldWork=newWork;
				}
				
			}
			
			//Iterate bj's for all curves
			for (int j=1;j<=M;j++){
				newWork.simulatedAnnealing(R,p);

				if (newWork.totalProb().compareTo(oldWork.totalProb())==1){
					oldWork=newWork;
				}
			}
			
			//Iterate sigj for all curves
			for (int j=1;j<=M;j++){
				newWork.nextSigj(j);

				if (newWork.totalProb().compareTo(oldWork.totalProb())==1){
					oldWork=newWork;
				}
			}
			
			//Compare
			for (int j=1;j<=M;j++){
			
				double b1jk=oldWork.workMix.paramList.get(j-1)[4];
				System.out.println(oldWork.workMix.toString());
				double b2jk=oldWork.workMix.paramList.get(j-1)[5];
				
				double new_b1jk=oldWork.workMix.paramList.get(j-1)[4];
				double new_b2jk=oldWork.workMix.paramList.get(j-1)[5];
				
				boolean cond1=(Math.pow(b1jk-new_b1jk, 2)<0.01);
				boolean cond2=(Math.pow(b2jk-new_b2jk, 2)<0.01);
						
				auxStop=auxStop && cond1 && cond2;
				
				//If the weight of a curve becomes so small that it is not representative it is eliminated.
				if(oldWork.workMix.paramList.get(j-1)[0]<0.00001){
					System.out.println(Arrays.toString(oldWork.workMix.paramList.get(j-1)));
					oldWork.workMix.paramList.remove(j-1);
					oldWork.workMix.M=oldWork.workMix.M-1;
					M=M-1;
					
					//Renormalization
					double sum=0.0;
					for (int m=1;j<=M;j++){
					sum=sum+oldWork.workMix.paramList.get(m-1)[0];
					}
					
					for (int n=1;j<=M;j++){
					oldWork.workMix.update(j,"wj",oldWork.workMix.paramList.get(n-1)[0]/sum);
					}
				}

			}
			
			stop=auxStop;
			
			
		}

	}





	//Para depois integrar no código da Interface
	//Inputs necessários:
	// - M - número de curvas gaussianas desejado
	// - sample - ficheiro com a amostra
	public static void main(String[] args) throws Exception {

		//Eliminar estas linhas --- vêm diretamente do input da interface
		//int M = 6;
		//String filename = "EM20.csv";

		//Working oldWork=new Working(new Sample(),new Mix());
		//ficheiro de mix?????
		//oldWork.read(filename);

		//Expectation Maximization*
		
		//RunMe run =new RunMe();
	//	run.expectationMaximization(oldWork,M);
		
		System.out.println("The End");
		
		
	}

}
