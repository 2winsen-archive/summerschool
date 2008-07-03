package lv.accenture.ex05;

import java.lang.Double;

import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * RatNum represents an immutable rational number. It includes all of the
 * elements of the set of rationals, as well as the special "NaN" (not-a-number)
 * element that results from division by zero.
 * <p>
 * The "NaN" element is special in many ways. Any arithmetic operation (such as
 * addition) involving "NaN" will return "NaN". With respect to comparison
 * operations, such as less-than, "NaN" is considered equal to itself, and
 * larger than all other rationals.
 * <p>
 * Examples of RatNums include "-1/13", "53/7", "4", "NaN", and "0".
 */
public class RatNum implements Comparable {

	private final int numer;
	private final int denom;

	/** @effects Constructs a new RatNum = n. */
	public RatNum(int n) {
		numer = n;
		denom = 1;
		// TODO
	}

	/**
	 * @effects If d = 0, constructs a new RatNum = NaN. Else constructs a new
	 *          RatNum = (n / d). String s = ""; s = toString(numer);
	 */
	public RatNum(int n, int d) {

		if (d == 0) {
			numer = 1;
			denom = d;
		} else {

			// denom is negative
			int nn = n, dd = d;

			if (d < 0) {
				nn = -(n);
				dd = -(d);
			}

			// if can be simplified
			if (gcd(nn, dd) != 0) {
				int a = gcd(nn, dd);
				nn = nn / a;
				dd = dd / a;
			}

			numer = nn;
			denom = dd;
		}

		// TODO
	}

	/**
	 * Checks to see if the representation invariant is being violated and if
	 * so, throws RuntimeException
	 * 
	 * @throws RuntimeException
	 *             if representation invariant is violated
	 */
	public void checkRep() throws RuntimeException {
		// TODO
	}

	/** @return true iff this is NaN (not-a-number) */
	public boolean isNaN() {
		if (denom == 0)
			return true;
		else
			return false;
	}

	/** @return true iff this < 0. */
	public boolean isNegative() {
		// TODO
		if (numer < 0)
			return true;
		else
			return false;
	}

	/** @return true iff this > 0. */
	public boolean isPositive() {
		// TODO
		if (numer > 0)
			return true;
		else
			return false;
	}

	/**
	 * @requires rn != null
	 * @return a negative number if this < rn, 0 if this = rn, a positive number
	 *         if this > rn.
	 */
	public int compareTo(Object o) throws ClassCastException {
		// TODO
		if (o instanceof RatNum) {
			RatNum r = (RatNum) o;

			if (this.isNaN() && r.isNaN())
				return 0;
			else if (!this.isNaN() && r.isNaN())
				return -1;
			else if (this.isNaN() && !r.isNaN())
				return 1;
			else if (!this.isNaN() && !r.isNaN()) {
				if (this.doubleValue() > r.doubleValue())
					return 1;
				else if (this.doubleValue() < r.doubleValue())
					return -1;
				else if (this.doubleValue() == r.doubleValue())
					return 0;
			}
		}
		return 1;
	}

	/**
	 * Approximates the value of this rational with a double value
	 * 
	 * @return a double approximation value for this RatNum. Note that "NaN" is
	 *         mapped to Double.NaN
	 */
	public double doubleValue() {
		// TODO
		double d = 0;
		if (this.isNaN())
			return Double.NaN;
		else
			return (double) numer / denom;
	}

	/**
	 * @return a String representing this, in reduced terms. The returned string
	 *         will either be "NaN", or it will take on either of the forms "P"
	 *         or "P/Q", where P and Q are both integers and Q != 0.
	 */
	public String toString() {
		// if (sauceejs ir 0) {
		// return "NaN"
		// else if (sauceejs ir 1) {
		// atgriezh skaitiitaaju kaa stringu
		// }
		// else {
		// / atgriezh "num/denom"
		// }

		// TODO
		String s = "";
		if (denom == 0)
			s = "NaN";
		else if (denom == 1)
			s = Integer.toString(numer);
		else {
			return Integer.toString(numer) + "/" + Integer.toString(denom);
		}

		return s;
	}

	/** @return a new Rational equal to (0 - this). */
	public RatNum negate() {
		// TODO
		RatNum r = new RatNum(-(this.numer), this.denom);
		return r;
	}

	/**
	 * @requires arg != null
	 * @return a new RatNum equal to (this + arg). If either argument is NaN,
	 *         then returns NaN.
	 */
	public RatNum add(RatNum arg) {
		// TODO

		int a = numer * arg.denom + arg.numer * denom;
		int b = denom * arg.denom;

		RatNum rr = new RatNum(a, b);
		if (b != 0) {
			return rr;
		}
		return rr;
	}

	/**
	 * @requires arg != null
	 * @return a new RatNum equal to (this - arg). If either argument is NaN,
	 *         then returns NaN.
	 */
	public RatNum sub(RatNum arg) {
		// TODO
		int a = numer * arg.denom - arg.numer * denom;
		int b = denom * arg.denom;

		RatNum rr = new RatNum(a, b);
		if (b != 0) {
			return rr;
		}
		return rr;
	}

	/**
	 * @requires arg != null
	 * @return a new RatNum equal to (this * arg). If either argument is NaN,
	 *         then returns NaN.
	 */
	public RatNum mul(RatNum arg) {
		// TODO
		return new RatNum(numer * arg.numer, denom * arg.denom);
	}

	/**
	 * @requires arg != null
	 * @return a new RatNum equal to (this / arg). If arg is zero, or if either
	 *         argument is NaN, then returns NaN.
	 */
	public RatNum div(RatNum arg) {
		// TODO
		if (arg.isNaN())
			return new RatNum(1, 0);
		else
			return new RatNum(numer * arg.denom, denom * arg.numer);

	}

	/**
	 * Returns the greatest common divisor of 'a' and 'b'.
	 * 
	 * @requires b != 0
	 * @return largest d such that a % d = 0 and b % d = 0
	 */
	private static int gcd(int a, int b) {
		// Euclid's method
		if (b == 0)
			return 0;
		while (b != 0) {
			int tmp = b;
			b = a % b;
			a = tmp;
		}
		if (a > 0) {
			return a;
		} else {
			return -a;
		}
	}

	/**
	 * Standard hashCode function.
	 * 
	 * @return an int that all objects equal to this will also return.
	 */
	public int hashCode() {
		// TODO
		return this.toString().hashCode();
	}

	/**
	 * Standard equality operation.
	 * 
	 * @return true if and only if 'obj' is an instance of a RatNum and 'this' =
	 *         'obj'. Note that NaN = NaN for RatNums.
	 */
	public boolean equals(Object obj) {
		// TODO
		if (obj instanceof RatNum) {
			RatNum r = (RatNum) obj;
			if (this.toString().equals(r.toString()))
				return true;
		}
		return false;
	}

	/**
	 * Makes a RatNum from a string describing it.
	 * 
	 * @requires 'ratStr' is an instance of a string, with no spaces, of the
	 *           form:
	 *           <UL>
	 *           <LI> "NaN" <LI> "N/M", where N and M are both integers in
	 *           decimal notation, and M != 0, or <LI> "N", where N is an
	 *           integer in decimal notation.
	 *           </UL>
	 * @returns NaN if ratStr = "NaN". Else returns a RatNum r = ( N / M ),
	 *          letting M be 1 in tanObjecthe case where only "N" is passed in.
	 */
	public static RatNum parse(String ratStr) {
		if(ratStr == "NaN") return new RatNum(1,0);
		String[] as = ratStr.split("/");
		if(as.length == 1)
		{
			return new RatNum(Integer.parseInt(as[0]));
		}
		else
		{
			return new RatNum(Integer.parseInt(as[0]),Integer.parseInt(as[1]));
		}
	}
}
