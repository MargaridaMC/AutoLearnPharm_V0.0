public class Measurement {
	
	/*
	 * Description: Class that defines the object Measurement. 
	 * This class regards the elementary object used to save the main information for the sample.
	 * 	
	 * 
	 * Possible modifications: 
	 * - implementation regarding hash table in the sample
	 * 
	 *
	 */

	int ind;
	double t;
	double val;
	Measurement next;
	Measurement prev;
	
	public Measurement(int ind, double t, double val) {
		this.ind = ind;
		this.t = t;
		this.val = val;
		this.next=null;
	}

	@Override
	public String toString() {
		return "Measurement_obj [ind=" + ind + ", t=" + t + ", val=" + val + "]";
	}

}
