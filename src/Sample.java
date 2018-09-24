import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

public class Sample {
	/*
	 * Description: Class that defines the object Sample, and all the methods that change its parameters. 
	 * The Class is implemented as a LinkedList of Measurements.
	 * List of methods:
	 * - void add (Measurement Measurement).
	 * - int length ().
	 * - Measurement element (int ind).
	 * - LinkedList<double[]> indice (int n).
	 * - Sample join (Sample sb).
	 *	 
	 * Comments: This class is defined over the class Measurement.
	 * 
	 * Possible modifications: 
	 *  - remove last from Sample; 
	 *  - implement data type as hash table
	 * 
	 *
	 */

	Measurement first;
	int size,K; //K gives the number of individuals in the sample

	@Override
	public String toString() {
		/*
		 * Description: String Constructor.
		 * Lists all the Piggys contained in the active sample.
		 * Comments: Possible changes regarding presentation depending on the expected output.
		 * 
		 */
		String Measurements ="";		

		for (int i=0;i<size;i++) {
			Measurement out=this.element(i);
			Measurements=Measurements+out.toString() + System.lineSeparator();

		}


		return "Sample_obj [Measurements=" + Measurements + "size=" + size + "]";
	}

	public Sample() {
		this.first = null;
		this.size = 0;
		this.K=0;
	}

	public void add (int joe_ind, double joe_time, double joe_val) {

		/*
		 * Description:Adds a Measurement to the sample.
		 * 
		 * Comments:if there is already one Measurement with the same ID in the Sample, 
		 * the new Measurement will be added to position just after the last one with it's ID.
		 *
		 */

		Measurement joe=new Measurement(joe_ind,  joe_time,  joe_val);
		
		if(first == null){ //empty sample
			first = joe;
			K++;
			}

		else if(first.ind>joe.ind) { //input >all inds in sample
			Measurement aux=first;
			first = joe;
			first.next=aux;
		}

		else {
			Measurement it = first;
			while (it.next !=null && it.next.ind<=joe.ind) {
				it=it.next;
			}

			Measurement aux=it.next;
			it.next=joe;
			joe.next=aux;
			if(it.ind!=joe.ind){
				K++;
			}
		}
		size++;

	}

	public int length () {
		/*		
		 * Description: Returns the length of the Sample (number of Measurements, counting with repetitions).
		 */

		return size;
	}

	public Measurement element (int p) {
		/*  
		 * Description: Given a position, returns the vector of the sample in said position.
		 */


		if (size==0) {
			System.out.println("ERROR Type x: The sample is empty");
			return null;
		}

		if (size<p) {
			System.out.println("ERROR Type x: The position is larger than the sample size");
			return null;
		}

		else {
			int i=0;
			Measurement it=first;

			while (i!=p) { 
				it=it.next;
				i++;
			}

			return it;
		}


	}	


	public LinkedList<double[]> index (int ind) {
		/* 
		 * Description:Returns a list of pairs (time and value) associated with one Measurement with index n.
		 * Comments: If that index n isn't present in the sample a message will be displayed.
		 */

		LinkedList<double[]> p=new LinkedList<double[]>();

		if(first==null) {
			System.out.println("The sample is empty.");
			return null;
		}		


		else{
			Measurement it=first;

			while (it.next!=null && it.next.ind<ind) {
				it=it.next;
			}

			if(it.val!= ind && (it.next==null || it.next.val>ind)) {
				System.out.println("The individual is not present in the sample.");
				return null;
			}

			else {
				
				while (it.next!=null && it.next.ind==ind) { 
					double[] v= {it.t,it.val}; 
					p.add(v);
					it=it.next; 
					
				} 
				//The cycle stops in the last measurement with ind=n
				double[] v= {it.t,it.val}; 
				p.add(v);
			
				return p;
			}


		}


	}

	public Sample join (Sample sb) {
		/*
		 * Description: Receives two samples and adds one to the other.
		 */
		return join(this,sb);
	}

	private Sample join(Sample sa, Sample sb) {


		if(sa.first==null)
			return sb;

		if(sb.first==null)
			return sa;

		else {

			Measurement it=sb.first;

			while(it!=null) {
				sa.add(it.ind,it.t,it.val);
				it=it.next;
			}

			return sa;
		}

	}

	public void readSample(String filename) throws Exception {

		BufferedReader CSVFile = new BufferedReader(new FileReader(filename));

	    String dataRow = CSVFile.readLine();

		while (dataRow != null) {

			String[] dataArray = dataRow.split(";");

			int ind = Integer.parseInt(dataArray[0]);
			double time = Double.parseDouble(dataArray[1]);
			double val = Double.parseDouble(dataArray[2]);

			add(ind, time, val);

			dataRow = CSVFile.readLine();
		}
		CSVFile.close();

	}
	
	public static void main (String args[]) throws Exception{
		Sample s1=new Sample();
		

		
		System.out.println(s1.toString());

	}

}