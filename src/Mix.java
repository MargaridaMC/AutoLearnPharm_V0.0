import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class Mix {

	/*
	 * Description: Class that defines the object Mix, and all the methods that change its parameters. 
	 * This class regards a mixture of Gaussians and the probability calculations needed to Maximize the
	 * The parameters parameters are: wj ; sigj ; a1j ; a2j ; b1j ; b2jg;
	 * The Class is implemented as a LinkedList comprised of 6 dim Vectors (the parameters regarding the Gaussian Mix)
	 * List of methods:
	 * - LinkedList<double[]> theta()
	 * - double prob(Sample s)
	 * - void update(LinkedList<double[]> paramList) 
	 * 	
	 * 
	 * Note: 
	 * All the methods will receive inputs that might be obtainable trough the object Mix, in order to lower the computational
	 * effort that would raise, for methods with equal inputs.
	 * 
	 * Possible modifications: 
	 * - implementation regarding inputs, depending on the required interface for the system.
	 * 
	 *
	 */


	/*
	 * paramList gives the current list of parameters (It is a linked list of arrays of 6 elements).
	 * M is the number of gaussians so far described the list of parameters (hence the length of the list).
	 */

	LinkedList <double[]> paramList;
	int M;

	@Override
	public String toString() {

		String l="";

		for (int i=0;i<M;i++) {
			double[] pi = paramList.get(i);
			l=l+Arrays.toString(pi) + System.lineSeparator();

		}
		return l;
	}



	public Mix(LinkedList<double[]> paramList) {
		super();
		this.paramList = paramList;
		this.M=paramList.size();
	}



	public LinkedList<double[]> theta(){
		/*
		 * Description:Returns the current list of parameters.
		 */
		return paramList;
	}

	public double fcurve(double[] thetaj, double tl){

		double a1j=thetaj[2];
		double a2j=thetaj[3];
		double b1j=thetaj[4];
		double b2j=thetaj[5];

		return a1j*Math.exp(-b1j*tl)+a2j*Math.exp(-b2j*tl);

	}

	public void update(int j,String param,double val) {

		/*
		 * Description: Receives the parameter to edit in a specific curve, j.
		 * Note: Theta_j={wj,sigj,a1j,a2j,b1j,b2j}
		 */

		switch(param){
		case "wj" : case "w":
			paramList.get(j-1)[0]=val;
			break;
		case "sigj":case "sig":
			paramList.get(j-1)[1]=val;
			break;
		case "a1j":case "a1":
			paramList.get(j-1)[2]=val;
			break;
		case "a2j":case "a2":
			paramList.get(j-1)[3]=val;
			break;
		case "b1j":case "b1":
			paramList.get(j-1)[4]=val;
			break;
		case "b2j":case "b2":
			paramList.get(j-1)[5]=val;
			break;
		default:
			System.out.println("This parameter does not exist or its name ir not correctly written");
			break;
		}


	}

	public void mixread (String filename) throws Exception{

		BufferedReader CSVFile = new BufferedReader(new FileReader(filename));

		String dataRow = CSVFile.readLine();

		while (dataRow != null) {

			String[] dataArray = dataRow.split(";");

			double wj = Double.parseDouble(dataArray[0]);
			double sigj = Double.parseDouble(dataArray[1]);
			double a1j = Double.parseDouble(dataArray[2]);
			double a2j = Double.parseDouble(dataArray[3]);
			double b1j = Double.parseDouble(dataArray[4]);
			double b2j = Double.parseDouble(dataArray[5]);

			double[] thetaj={wj,sigj,a1j,a2j,b1j,b2j};

			paramList.add(thetaj);

			dataRow = CSVFile.readLine();
		}

		CSVFile.close();

		M=paramList.size();
	}

	public void mixwrite (String filename) {


		BufferedWriter writer =null;
		try {
			File f = new File(filename);
			writer = new BufferedWriter(new FileWriter (f));
			for (int j=0;j<M;j++) {				
				String w =Double.toString(paramList.get(j)[0]);
				String sig = Double.toString(paramList.get(j)[1]);
				String a1j = Double.toString(paramList.get(j)[2]);
				String a2j = Double.toString(paramList.get(j)[3]);
				String b1j = Double.toString(paramList.get(j)[4]);
				String b2j = Double.toString(paramList.get(j)[5]);

				writer.write(w + " " + sig + " " + a1j + " " + a2j + " " + b1j + " " + b2j+ System.lineSeparator());
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				writer.close ();
			} catch (final Exception e) {
			}
			
		}


	}
	public static void main (String args[]) throws Exception{
		LinkedList <double [] >param = new LinkedList <double []>() ;
		double [] v1 = {1.1,1.2,1.3,1.4,1.5,1.6,1.7};
		double [] v2 = {2.1,2.2,2.3,2.4,2.5,2.6,2.7};
		double [] v3 = {3.1,3.2,3.3,3.4,3.5,3.6,3.7};


		param.add(v1);
		param.add(v2);
		param.add(v3);

		
		Mix mix=new Mix(param);
		mix.mixwrite("ola.txt");
		
		

		

	}

}
	


