

import java.util.LinkedList;
import java.math.BigDecimal;
import java.math.MathContext;

public class Working {

	// static MathContext MC = MathContext.UNLIMITED;
	static MathContext MC = new MathContext(100000);
	Sample workSample;
	Mix workMix;
	// BigDecimal zero = new BigDecimal(0,MC);
	BigDecimal TEN = new BigDecimal(10, MC);
	BigDecimal MIN = BigDecimal.ONE.divide(TEN.pow(100));
	BigDecimal EXP500 = new BigDecimal(Math.exp(500));

	public Working(Sample workSample, Mix workMix) {
		this.workSample = workSample;
		this.workMix = workMix;
	}

	@Override
	public String toString() {
		return "The sample you are working on is " + workSample.toString() + " and the Mix is " + workMix.toString();
	}

	public BigDecimal prob(LinkedList<double[]> list, double[] thetaj) {
		/*
		 * Description: receives the list of measurements related to a given
		 * individual and a the list of pcxarameters for a given gaussian and
		 * calculates the probability of the measurements fitting the gaussian
		 */

		int n = list.size();

		/* Get the set of parameters thetaj = {wj,sigj,a1j,a2j,b1j,b2j} */
		double sigj = thetaj[1];
		BigDecimal product = BigDecimal.ONE;

		/* For each of the points received */

		for (int l = 1; l <= n; l++) {
			double tl = list.get(l - 1)[0];
			double yl = list.get(l - 1)[1];
			double fl = workMix.fcurve(thetaj, tl);
			double gl = -Math.pow(yl - fl, 2.0) / (2 * Math.pow(sigj, 2));
			BigDecimal prodl = new BigDecimal(Math.exp(gl),MC).divide(new BigDecimal(Math.sqrt(2 * Math.PI * Math.pow(sigj, 2))),MC);
					
			// System.out.println(prodl);
			product = product.multiply(prodl, MathContext.DECIMAL128);
		}

		return product;

	}

	public BigDecimal xij(int ind, double[] thetaj) {

		// Description: Calculates de parameter Xij, where:
		// - i is the index of a given patient
		// - thetaj is one of the gaussians (with index j) in the mix

		LinkedList<double[]> yi = workSample.index(ind);

		BigDecimal den = BigDecimal.ZERO;

		// For a sigle individual ind go through all the curves
		for (int u = 1; u <= workMix.M; u++) {
			double[] thetau = workMix.paramList.get(u - 1);
			double wu = thetau[0];
			den = den.add(new BigDecimal(wu, MC).multiply(prob(yi, thetau), MC));

		}

		BigDecimal wj = new BigDecimal(thetaj[0], MC);
		BigDecimal num = wj.multiply(prob(yi, thetaj).multiply(EXP500, MC));

		return num.divide(den.multiply(EXP500, MC), MathContext.DECIMAL128);

	}

	public void nextWj(int j) {

		double sum = 0.0;
		int kj = workSample.K;
		double[] thetaj = workMix.paramList.get(j - 1);

		// For all the individuals
		for (int i = 0; i < kj; i++) {
			sum = sum + xij(i, thetaj).doubleValue();
		}

		workMix.update(j, "wj", sum / kj);
	}

	public void nextAj(int j) {

		int kj = workSample.K;

		BigDecimal num = BigDecimal.ZERO;
		BigDecimal den = num;

		for (int i = 0; i < kj; i++) {
			LinkedList<double[]> m = workSample.index(i);
			int n = m.size();

			for (int l = 0; l < n; l++) {

				double[] pair = m.get(l);
				BigDecimal yil = new BigDecimal(pair[1], MC);
				double tl = pair[0];
				double[] thetaj = workMix.paramList.get(j - 1);
				BigDecimal xij = xij(i, thetaj);
				double b1j = thetaj[4];
				double b2j = thetaj[5];

				BigDecimal aux1 = new BigDecimal(Math.exp(-b1j * tl) - Math.exp(-b2j * tl), MC);
				BigDecimal aux2 = new BigDecimal(Math.pow(Math.exp(-b1j * tl) - Math.exp(-b2j * tl), 2), MC);

				num = num.add(xij.multiply(yil).multiply(aux1), MC);
				den = den.add(xij.multiply(aux2), MC);
			}

		}

		if (den.compareTo(MIN) < 0)
			den = MIN;
		double aj = num.divide(den, MathContext.DECIMAL128).doubleValue();
		workMix.update(j, "a1j", aj);
		workMix.update(j, "a2j", -aj);

	}

	public void nextSigj(int j) {
		/*
		 * Atualiza o sigma;
		 */

		BigDecimal num = BigDecimal.ZERO;
		BigDecimal den = num; // valor output e denominador

		// For each individual
		for (int i = 0; i < workSample.K; i++) {

			// values refereing to the individual i
			LinkedList<double[]> yi = workSample.index(i);
			int n = yi.size();
			double[] thetaj = workMix.paramList.get(j - 1);

			// for each moment in time
			for (int l = 1; l <= n; l++) {
				double tl = yi.get(l - 1)[0];
				BigDecimal yl = new BigDecimal( yi.get(l - 1)[1]);
				BigDecimal aux = yl.subtract(new BigDecimal(workMix.fcurve(thetaj, tl),MC)).pow(2,MC);

				num = num.add(xij(i, thetaj).multiply(aux, MC), MC);
			}

			den = new BigDecimal(n, MC).multiply(xij(i, thetaj), MC);

		}

		num = num.multiply(EXP500, MC);
		den = den.multiply(EXP500, MC);

		if (den.compareTo(MIN) == -1)
			den = MIN;

		System.out.println(num);
		System.out.println(den);
		System.out.println(num.compareTo(MIN));
		if (num.compareTo(MIN) != -1)
			workMix.update(j, "sigj", (num.divide(den, MC)).doubleValue());

	}

	public double Q(Mix theta) {

		double r = 0.0;

		for (int j = 1; j <= workMix.M; j++) {

			double[] paramj_k = workMix.paramList.get(j - 1);
			double[] paramj = theta.paramList.get(j - 1);
			double wj = paramj[0];

			for (int i = 0; i < workSample.K; i++) {
				LinkedList<double[]> list = workSample.index(i);
				double aux1 = wj * prob(list, paramj).doubleValue();

				r = r + xij(i, paramj_k).doubleValue() * aux1;
			}

		}

		return r;
	}

	public BigDecimal totalProb() {

		int m = workMix.M;
		int k = workSample.K;
		BigDecimal sum = BigDecimal.ZERO;

		for (int j = 1; j <= m; j++) {
			double[] thetaj = workMix.paramList.get(j - 1);
			BigDecimal wj = new BigDecimal(thetaj[0]);

			BigDecimal prodj = BigDecimal.ONE;

			for (int i = 0; i < k; i++) {

				LinkedList<double[]> yi = workSample.index(i);
				prodj = prodj.add(prob(yi, thetaj));

			}
			sum = sum.add(wj.multiply(prodj));
		}
		return sum;
	}

	public void simulatedAnnealing(int R, double p) {

		// working variables to evaluate the update of blj
		double prob_j = 1.0; // probability of each curve
		double prob_old_heating = 1.0; // current total
		// probability
		double[] theta_j;

		// Esta secção é o cálculo da prob_total ---- podemos mudar a função
		// prob_total (+ arrumado)
		// for all gaussian curves
		for (int j = workMix.M; j > 0; j--) {

			theta_j = workMix.paramList.get(j - 1);
			double wj = workMix.paramList.get(j - 1)[0];

			// for all individuals in the sample
			for (int i = 0; i < workSample.K; i++) {
				LinkedList<double[]> list = workSample.index(i);
				prob_j = prob(list, theta_j).doubleValue() * prob_j;// prob_total
			}

			prob_old_heating = prob_old_heating * prob_j * wj;

			if (j != 0)// ---> Não devia ser j!=1 - nunca chega a ser zero
				theta_j = workMix.paramList.get(j - 1);

		}
		// Set a new auxiliary variable which can be changed while saving the
		// value of the previous one
		// (we will need to compare the two later)

		for (int j = 1; j <= workMix.M; j++) {
			// if (j!=1)//poupa um get
			// theta_j = workMix.ParamList.get(j-1);//Isto é trapalhão....

			theta_j = workMix.paramList.get(j - 1);

			// First b1 and b2 to test for the last blj values
			double b1 = workMix.paramList.get(j - 1)[4], b2 = workMix.paramList.get(j - 1)[5];
			// b1j and b2j and the values that will change in the cycle. b1 and
			// b2 are the reference values
			double b1j = b1, b2j = b2;
			int rHeating = 1;
			boolean stop = false;

			// updated probability for each heating cycle -->What we wan to
			// maximize in each cycle
			// prob_old_heating is the reference probability for each heating
			// cycle
			double probNewHeating = prob_old_heating;

			// Begin a heating
			while (rHeating <= R && !stop) {
				// Number of Heatings
				int rCooling = 1;

				// reference probability for each cooling cycle
				double probOldCooling = probNewHeating;
				// updated probability for each heating cycle -->What we wan to
				// maximize in each cycle
				double probNewCooling = probNewHeating;

				// Begin a cooling
				while (rCooling <= 100 && !stop) {
					// Cooling: while prob() increases in value (breaks for
					// decrease in value with 0,01 probability)

					// prob_new =
					// prob_new.divide(wj.multiply(prob_j_list[j-1]));
					// ---->Desnecessário

					// Random blj values in the neighbourhood of the previous->
					// [bl-0.1,bl+0.1]
					b2j = b2;
					b1j = (b1 - 0.1) * Math.random() * 0.2;
					if (b1j < 0 && b1j < b2j)
						b1j = b1;

					// check for knee choice
					// Option 1 (no heuristic)
					/*
					 * while (b2j<b1j ||b2j>5) //Pick again
					 * b2j=(b2-0.1)*Math.random()*0.2;
					 */

					// Option 2 (with heuristic -> keep the last b2j)

					b2j = (b2 - 0.1) * Math.random() * 0.2;
					if (b2j < b1j || b2j > 5)// keep previous
						b2j = b2;

					// Option 3 (w/ heuristic -> decrease .02)
					/*
					 * b2j=(b2-0.1)*Math.random()*0.2; if (b2j<b1j ||
					 * b2j>5)//decrease 0.2 b2j=b2-02;
					 * 
					 */

					// Updates prob_j (for all individuals in the sample)

					// Removes the "old probability" from the value of
					// prob_new_cooling and adds the new
					double prob_j_old = 1.0;
					double prob_j_new = 1.0;

					double[] theta_j_new = theta_j;
					theta_j_new[4] = b1j;
					theta_j_new[5] = b2j;

					for (int i = 0; i < workSample.K; i++) {
						LinkedList<double[]> list = workSample.index(i);
						prob_j_old = prob(list, theta_j).doubleValue() * prob_j_old;
						prob_j_new = prob(list, theta_j).doubleValue() * prob_j_new;
					}

					// update prob_new with the new theta_j for b1j',b2j'
					probNewCooling = probNewCooling * prob_j_new / prob_j_old;
					// System.out.println("Para R_cooling "+ R_cooling + " b1j"+
					// b1j+ " b2j"+b2j);
					// Improvement Verification

					// System.out.println("prob_new_cooling = " +
					// prob_new_cooling);
					// System.out.println("prob_old_cooling = " +
					// prob_old_cooling);

					// int aux = prob_new_cooling.compareTo(prob_old_cooling);
					// System.out.println(aux);

					if (probNewCooling > probOldCooling) {
						if (probNewCooling - probOldCooling < p && b1 - b1j < 0.00001 && b2 - b2j < 0.00001) {
							stop = true;
						} else {
							prob_old_heating = probNewCooling;
							b1 = b1j;// System.out.println("escolhidos" + b1);
							b2 = b2j;// System.out.println("escolhidos" + b2);
						}
					} else {
						// Accept these values with a probability of 0.01
						stop = Math.random() < 0.01;
					}

					rCooling++;
				}

				probNewHeating = probNewCooling;
				if (probNewHeating > prob_old_heating) {
					prob_old_heating = probNewHeating;
					b1 = b1j;
					b2 = b2j;
				} else {
					// Accept these values with a probability of 0.01
					stop = Math.random() < 0.01;
				}

				// Pick another pair of initial b1 and b2 and heat again
				b1 = Math.random() * 5;
				b2 = Math.random() * (5 - b1);

				rHeating++;
			}

			workMix.update(j, "b1", b1j);
			workMix.update(j, "b2", b2j);
		}
	}

	public static void main(String args[]) throws Exception {

		long startTime = System.nanoTime();
	
		// Teste do Mix
		// n.read("EM20.csv");

		// MiniSample
	
		// Teste do reader
		// n.read("EM100.csv");
		// System.out.println(n.work_sample.toString());
		/*
		 * 
		 * 
		 * 
		 * BigDecimal w1 = new BigDecimal(
		 * m1.get_parameter("w",m1.ParamList.get(1))); BigDecimal a11= new
		 * BigDecimal(m1.get_parameter("a1",m1.ParamList.get(1))); BigDecimal
		 * a21= new BigDecimal (m1.get_parameter("a2",m1.ParamList.get(1)));
		 * double s1= m1.get_parameter("sig",m1.ParamList.get(1)),b11=
		 * m1.get_parameter("b1",m1.ParamList.get(1)),b21=
		 * m1.get_parameter("b2",m1.ParamList.get(1)); Working w=new
		 * Working(sa,m1);
		 * 
		 * double w2=w.next_wj(1),a12=w.next_aj(1),a22=-a12; double
		 * s2=w.next_sig(1),b12=b11,b22=b21;
		 */

		// Teste do Q
		// Mix m2=new Mix(2);
		/*
		 * System.out.println(n.Xij(0,m1.ParamList.get(1)));
		 * 
		 * //Teste next_aj System.out.println("New aj= " +
		 * n.work_mix.ParamList.get(2)[2]);
		 * 
		 * //Teste next_sig System.out.println("New sigj= " +
		 * n.work_mix.ParamList.get(2)[1]);
		 * 
		 * //Teste do Q Mix m2=new Mix(2); System.out.println("Q = " + n.Q(m2));
		 * 
		 * //Teste total_prob System.out.println("total_prob = " +
		 * n.total_prob());
		 */
	}

}
