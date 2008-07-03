package lv.accenture.ex05; 

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

/** This class contains a set of test cases that can be used to 
    test the implementation of the RatNum class.
    <p>
    RatNum is implemented for you, so it should already pass all the 
    tests in this suite.  This test is provided to give you examples
    of using the RatNum class, albeit in the context of a test driver.
*/
public class TestRatNum extends TestCase {

    // naming convention used throughout class: spell out number in
    // variable as its constructive form.  Unary minus is notated with
    // the prefix "neg", and the solidus is notated with an 'I'
    // character.  Thus, "1 + 2/3" becomes one_plus_two_I_three

    // some simple base RatNums
    private RatNum zero = new RatNum(0);
    private RatNum one = new RatNum(1);
    private RatNum negOne = new RatNum(-1);
    private RatNum two = new RatNum(2);
    private RatNum three = new RatNum(3);

    private RatNum one_I_two = new RatNum(1, 2);
    private RatNum one_I_three = new RatNum(1, 3);
    private RatNum one_I_four = new RatNum(1, 4);
    private RatNum two_I_three = new RatNum(2, 3);
    private RatNum three_I_four = new RatNum(3, 4);

    private RatNum negOne_I_two = new RatNum(-1, 2);

    // improper fraction
    private RatNum three_I_two = new RatNum(3, 2);

    // NaNs
    private RatNum one_I_zero = new RatNum(1, 0);
    private RatNum negOne_I_zero = new RatNum(-1, 0);
    private RatNum hundred_I_zero = new RatNum(100, 0);

    // ratnums: Set of varied ratnums (includes NaNs)
    // set is { 0, 1, -1, 2, 1/2, 3/2, 1/0, -1/0, 100/0 }
    private RatNum[] ratnums = new RatNum[] {
        zero, one, negOne, two, one_I_two, negOne_I_two, three_I_two,
        /* NaNs */ one_I_zero, negOne_I_zero, hundred_I_zero
    };

    // ratnans: Set of varied NaNs
    // set is { 1/0, -1/0, 100/0 }
    private RatNum[] ratnans = new RatNum[] {
        one_I_zero, negOne_I_zero, hundred_I_zero
    };

    // ratnanans: Set of varied non-NaN ratnums
    // set is ratnums - ratnans
    private RatNum[] ratnanans = new RatNum[] {
        zero, one, negOne, two, one_I_two, three_I_two
    };

    public TestRatNum(String name) { super(name); }

    // self-explanatory helper function
    private void eq( RatNum ratNum, String rep ) {
        assertEquals( rep, ratNum.toString() );
    }

    public void testOneArgCtor() {
        eq( zero, "0" );

        eq( one, "1" );

        RatNum four = new RatNum(4);
        eq( four, "4" );

        eq( negOne, "-1" );

        RatNum negFive = new RatNum(-5);
        eq( negFive, "-5" );

        RatNum negZero = new RatNum(-0);
        eq( negZero, "0" );
    }

    public void testTwoArgCtor() {
        RatNum one_I_two = new RatNum(1, 2);
        eq( one_I_two, "1/2" );

        RatNum two_I_one = new RatNum(2, 1);
        eq( two_I_one, "2" );

        RatNum three_I_two = new RatNum(3, 2);
        eq( three_I_two, "3/2" );

        RatNum negOne_I_thirteen = new RatNum(-1, 13);
        eq( negOne_I_thirteen, "-1/13" );

        RatNum fiftyThree_I_seven = new RatNum(53, 7);
        eq( fiftyThree_I_seven, "53/7" );

        RatNum zero_I_one = new RatNum(0, 1);
        eq( zero_I_one, "0" );
    }

    public void testTwoArgCtorOnNaN() {
        RatNum one_I_zero = new RatNum(1, 0);
        eq( one_I_zero, "NaN" );

        RatNum two_I_zero = new RatNum(2, 0);
        eq( two_I_zero, "NaN" );

        RatNum negOne_I_zero = new RatNum(-1, 0);
        eq( negOne_I_zero, "NaN" );

        RatNum zero_I_zero = new RatNum(0, 0);
        eq( zero_I_zero, "NaN" );

        RatNum negHundred_I_zero = new RatNum(-100, 0);
        eq( negHundred_I_zero, "NaN" );
    }

    public void testReduction() {
        RatNum negOne_I_negTwo = new RatNum(-1, -2);
        eq( negOne_I_negTwo, "1/2" );

        RatNum two_I_four = new RatNum(2, 4);
        eq( two_I_four, "1/2" );

        RatNum six_I_four = new RatNum(6, 4);
        eq( six_I_four, "3/2" );

        RatNum twentySeven_I_thirteen = new RatNum(27, 13);
        eq( twentySeven_I_thirteen, "27/13" );

        RatNum negHundred_I_negHundred = new RatNum(-100, -100);
        eq( negHundred_I_negHundred, "1" );
    }

    // self-explanatory helper function
    private void approxEq(double d1, double d2) {
        assertTrue (Math.abs(d1 - d2) < .0000001);
    }
    public void testApprox() {
        approxEq( zero.doubleValue(), 0.0 );
        approxEq( one.doubleValue(), 1.0 );
        approxEq( negOne.doubleValue(), -1.0 );
        approxEq( two.doubleValue(), 2.0 );
        approxEq( one_I_two.doubleValue(), 0.5 );
        approxEq( two_I_three.doubleValue(), 2./3. );
        approxEq( three_I_four.doubleValue(), 0.75 );

        // cannot test that one_I_zero.approx() approxEq Double.NaN,
        // because it WON'T!!!  Instead, construct corresponding
        // instance of Double and use .equals(..) method
        assertTrue( (new Double(Double.NaN)).
                    equals
                    (new Double(one_I_zero.doubleValue())) );

        // use left-shift operator "<<" to create integer for 2^30
        RatNum one_I_twoToThirty = new RatNum(1, (1<<30) );
        double quiteSmall = 1./Math.pow(2, 30);
        approxEq( one_I_twoToThirty.doubleValue(), quiteSmall );
    }

    public void testAddSimple() {
        eq( zero.add(zero), "0" );
        eq( zero.add(one), "1" );
        eq( one.add(zero), "1" );
        eq( one.add(one),  "2" );
        eq( one.add(negOne), "0" );
        eq( one.add(two), "3" );
        eq( two.add(two), "4" );
    }

    public void testAddComplex() {
        eq( one_I_two.add(zero), "1/2" );
        eq( one_I_two.add(one), "3/2" );
        eq( one_I_two.add(one_I_two), "1" );
        eq( one_I_two.add(one_I_three), "5/6" );
        eq( one_I_two.add(negOne), "-1/2" );
        eq( one_I_two.add(two), "5/2" );
        eq( one_I_two.add(two_I_three), "7/6" );
        eq( one_I_two.add(three_I_four), "5/4" );

        eq( one_I_three.add(zero), "1/3" );
        eq( one_I_three.add(two_I_three), "1" );
        eq( one_I_three.add(three_I_four), "13/12" );
    }

    public void testAddImproper() {
        eq( three_I_two.add(one_I_two), "2" );
        eq( three_I_two.add(one_I_three), "11/6" );
        eq( three_I_four.add(three_I_four), "3/2" );

        eq( three_I_two.add(three_I_two), "3" );
    }


    public void testAddOnNaN() {
        // each test case (addend, augend) drawn from the set
        // ratnums x ratnans

        for(int i=0; i<ratnums.length; i++) {
            for(int j=0; j<ratnans.length; j++) {
                eq( ratnums[i].add(ratnans[j]), "NaN" );
                eq( ratnans[j].add(ratnums[i]), "NaN" );
            }
        }

    }

    public void testAddTransitively() {
        eq( one.add(one).add(one), "3" );
        eq( one.add(one.add(one)), "3" );
        eq( zero.add(zero).add(zero), "0" );
        eq( zero.add(zero.add(zero)), "0" );
        eq( one.add(two).add(three), "6" );
        eq( one.add(two.add(three)), "6" );

        eq( one_I_three.add(one_I_three).add(one_I_three), "1" );
        eq( one_I_three.add(one_I_three.add(one_I_three)), "1" );

        eq( one_I_zero.add(one_I_zero).add(one_I_zero), "NaN" );
        eq( one_I_zero.add(one_I_zero.add(one_I_zero)), "NaN" );

        eq( one_I_two.add(one_I_three).add(one_I_four), "13/12" );
        eq( one_I_two.add(one_I_three.add(one_I_four)), "13/12" );
    }

    public void testSubSimple() {
        eq( zero.sub(one), "-1");
        eq( zero.sub(zero), "0");
        eq( one.sub(zero), "1" );
        eq( one.sub(one), "0" );
        eq( two.sub(one), "1" );
        eq( one.sub(negOne), "2" );
        eq( one.sub(two), "-1" );
        eq( one.sub(three), "-2" );
    }

    public void testSubComplex() {
        eq( one.sub(one_I_two), "1/2" );
        eq( one_I_two.sub(one), "-1/2" );
        eq( one_I_two.sub(zero), "1/2" );
        eq( one_I_two.sub(two_I_three), "-1/6" );
        eq( one_I_two.sub(three_I_four), "-1/4" );
    }

    public void testSubImproper() {
        eq( three_I_two.sub(one_I_two), "1" );
        eq( three_I_two.sub(one_I_three), "7/6" );
    }

    public void testSubOnNaN() {
        // analogous to testAddOnNaN()

        for(int i=0; i<ratnums.length; i++) {
            for( int j=0; j<ratnans.length; j++) {
                eq( ratnums[i].sub(ratnans[j]), "NaN" );
                eq( ratnans[j].sub(ratnums[i]), "NaN" );
            }
        }
    }

    public void testSubTransitively() {
        // subtraction is not transitive; testing that operation is
        // correct when *applied transitivitely*, not that it obeys
        // the transitive property

        eq( one.sub(one).sub(one), "-1" );
        eq( one.sub(one.sub(one)), "1" );
        eq( zero.sub(zero).sub(zero), "0" );
        eq( zero.sub(zero.sub(zero)), "0" );
        eq( one.sub(two).sub(three), "-4" );
        eq( one.sub(two.sub(three)), "2" );

        eq( one_I_three.sub(one_I_three).sub(one_I_three), "-1/3" );
        eq( one_I_three.sub(one_I_three.sub(one_I_three)), "1/3" );

        eq( one_I_zero.sub(one_I_zero).sub(one_I_zero), "NaN" );
        eq( one_I_zero.sub(one_I_zero.sub(one_I_zero)), "NaN" );

        eq( one_I_two.sub(one_I_three).sub(one_I_four), "-1/12" );
        eq( one_I_two.sub(one_I_three.sub(one_I_four)), "5/12" );
    }

    public void testMulProperties() {
        // zero property
        for(int i=0; i<ratnanans.length; i++) {
            eq( zero.mul(ratnanans[i]) , "0" );
            eq( ratnanans[i].mul(zero) , "0" );
        }

        // one property
        for(int i=0; i<ratnanans.length; i++) {
            eq( one.mul(ratnanans[i]) , ratnanans[i].toString() );
            eq( ratnanans[i].mul(one) , ratnanans[i].toString() );
        }

        // negOne property
        for(int i=0; i<ratnanans.length; i++) {
            eq( negOne.mul(ratnanans[i]) , ratnanans[i].negate().toString() );
            eq( ratnanans[i].mul(negOne) , ratnanans[i].negate().toString() );
        }
    }

    public void testMulSimple() {
        eq( two.mul(two), "4" );
        eq( two.mul(three), "6" );
        eq( three.mul(two), "6" );
    }

    public void testMulComplex() {
        eq( one_I_two.mul(two), "1" );
        eq( two.mul(one_I_two), "1" );
        eq( one_I_two.mul(one_I_two), "1/4" );
        eq( one_I_two.mul(one_I_three), "1/6" );
        eq( one_I_three.mul(one_I_two), "1/6" );
    }

    public void testMulImproper() {
        eq( three_I_two.mul(one_I_two), "3/4" );
        eq( three_I_two.mul(one_I_three), "1/2" );
        eq( three_I_two.mul(three_I_four), "9/8" );
        eq( three_I_two.mul(three_I_two), "9/4" );
    }

    public void testMulOnNaN() {
        // analogous to testAddOnNaN()

        for(int i=0; i<ratnums.length; i++) {
            for( int j=0; j<ratnans.length; j++) {
                eq( ratnums[i].mul(ratnans[j]), "NaN" );
                eq( ratnans[j].mul(ratnums[i]), "NaN" );
            }
        }
    }

    public void testMulTransitively() {
        eq( one.mul(one).mul(one), "1" );
        eq( one.mul(one.mul(one)), "1" );
        eq( zero.mul(zero).mul(zero), "0" );
        eq( zero.mul(zero.mul(zero)), "0" );
        eq( one.mul(two).mul(three), "6" );
        eq( one.mul(two.mul(three)), "6" );

        eq( one_I_three.mul(one_I_three).mul(one_I_three), "1/27" );
        eq( one_I_three.mul(one_I_three.mul(one_I_three)), "1/27" );

        eq( one_I_zero.mul(one_I_zero).mul(one_I_zero), "NaN" );
        eq( one_I_zero.mul(one_I_zero.mul(one_I_zero)), "NaN" );

        eq( one_I_two.mul(one_I_three).mul(one_I_four), "1/24");
        eq( one_I_two.mul(one_I_three.mul(one_I_four)), "1/24");
    }

    public void testDivSimple() {
        eq( zero.div(zero), "NaN" );
        eq( zero.div(one), "0" );
        eq( one.div(zero), "NaN" );
        eq( one.div(one), "1" );
        eq( one.div(negOne), "-1" );
        eq( one.div(two), "1/2" );
        eq( two.div(two), "1" );
    }

    public void testDivComplex() {
        eq( one_I_two.div(zero), "NaN" );
        eq( one_I_two.div(one), "1/2" );
        eq( one_I_two.div(one_I_two), "1" );
        eq( one_I_two.div(one_I_three), "3/2" );
        eq( one_I_two.div(negOne), "-1/2" );
        eq( one_I_two.div(two), "1/4" );
        eq( one_I_two.div(two_I_three), "3/4" );
        eq( one_I_two.div(three_I_four), "2/3" );

        eq( one_I_three.div(zero), "NaN" );
        eq( one_I_three.div(two_I_three), "1/2" );
        eq( one_I_three.div(three_I_four), "4/9" );
    }

    public void testDivImproper() {
        eq( three_I_two.div(one_I_two), "3" );
        eq( three_I_two.div(one_I_three), "9/2" );
        eq( three_I_two.div(three_I_two), "1" );
    }

    public void testDivOnNaN() {
        // each test case (addend, augend) drawn from the set
        // ratnums x ratnans

        for(int i=0; i<ratnums.length; i++) {
            for(int j=0; j<ratnans.length; j++) {
                eq( ratnums[i].div(ratnans[j]), "NaN" );
                eq( ratnans[j].div(ratnums[i]), "NaN" );
            }
        }

    }

    public void testDivTransitively() {
        // (same note as in testSubTransitively re: transitivity property)

        eq( one.div(one).div(one), "1" );
        eq( one.div(one.div(one)), "1" );
        eq( zero.div(zero).div(zero), "NaN" );
        eq( zero.div(zero.div(zero)), "NaN" );
        eq( one.div(two).div(three), "1/6" );
        eq( one.div(two.div(three)), "3/2" );

        eq( one_I_three.div(one_I_three).div(one_I_three), "3" );
        eq( one_I_three.div(one_I_three.div(one_I_three)), "1/3" );

        eq( one_I_zero.div(one_I_zero).div(one_I_zero), "NaN" );
        eq( one_I_zero.div(one_I_zero.div(one_I_zero)), "NaN" );

        eq( one_I_two.div(one_I_three).div(one_I_four), "6" );
        eq( one_I_two.div(one_I_three.div(one_I_four)), "3/8" );

    }

    public void testNegate() {
        eq( zero.negate(), "0" );
        eq( one.negate() , "-1" );
        eq( negOne.negate(), "1" );
        eq( two.negate(), "-2" );
        eq( three.negate(), "-3" );

        eq( one_I_two.negate(), "-1/2" );
        eq( one_I_three.negate(), "-1/3" );
        eq( one_I_four.negate(), "-1/4" );
        eq( two_I_three.negate(), "-2/3" );
        eq( three_I_four.negate(), "-3/4" );

        eq( three_I_two.negate(), "-3/2" );

        eq( one_I_zero.negate(), "NaN" );
        eq( negOne_I_zero.negate(), "NaN" );
        eq( hundred_I_zero.negate(), "NaN" );
    }

    // helper function, "decode-and-check"
    private void decChk(String s, RatNum expected) {
        eq( RatNum.parse(s), expected.toString() );
    }
    public void testParse() {
        decChk("0", zero);

        decChk("1",   one );
        decChk("1/1", one );
        decChk("2/2", one );
        decChk("-1/-1", one );

        decChk("-1", negOne );
        decChk("1/-1", negOne );
        decChk("-3/3", negOne );

        decChk("2", two );
        decChk("2/1", two );
        decChk("-4/-2", two );

        decChk("1/2", one_I_two );
        decChk("2/4", one_I_two );

        decChk("3/2", three_I_two );
        decChk("-6/-4", three_I_two );

        decChk("NaN", one_I_zero );
        decChk("NaN", negOne_I_zero );
    }

    public void testEqualsReflexive() {
        for (int i=0; i<ratnums.length; i++) {
            assertTrue(ratnums[i].equals(ratnums[i]));
        }
    }
    public void testEquals() {
        assertTrue( one.equals(one) );
        assertTrue( one.add(one).equals(two) );
        assertTrue( one_I_two.equals( one.div(two) ) );
        assertTrue( three_I_two.equals( three.div(two) ) );

        assertTrue( one_I_zero.equals( one_I_zero ) );
        assertTrue( one_I_zero.equals( negOne_I_zero ) );
        assertTrue( one_I_zero.equals( hundred_I_zero ) );

        assertTrue( ! one.equals(zero) );
        assertTrue( ! zero.equals(one) );
        assertTrue( ! one.equals(two) );
        assertTrue( ! two.equals(one) );
        assertTrue( ! one.equals(negOne) );
        assertTrue( ! negOne.equals(one) );

        assertTrue( ! one.equals(one_I_two) );
        assertTrue( ! one_I_two.equals(one) );
        assertTrue( ! one.equals(three_I_two) );
        assertTrue( ! three_I_two.equals(one) );
    }

    private void assertGreater( RatNum larger, RatNum smaller ) {
        assertTrue( larger.compareTo(smaller) > 0 );
        assertTrue( smaller.compareTo(larger) < 0 );
    }
    public void testCompareToReflexive() {
        // reflexivitiy
        for(int i=0; i<ratnums.length; i++) {
            assertTrue( ratnums[i].compareTo(ratnums[i]) == 0);
        }
    }
    public void testCompareToNonFract() {
        assertGreater( one, zero );
        assertGreater( one, negOne );
        assertGreater( two, one );
        assertGreater( two, zero );
        assertGreater( zero, negOne );
    }
    public void testCompareToFract() {
        assertGreater( one, one_I_two );
        assertGreater( two, one_I_three );
        assertGreater( one, two_I_three );
        assertGreater( two, two_I_three );
        assertGreater( one_I_two, zero );
        assertGreater( one_I_two, negOne );
        assertGreater( one_I_two, negOne_I_two );
        assertGreater( zero, negOne_I_two );
    }
    public void testCompareToNaNs() {
        for(int i=0; i<ratnans.length; i++) {
            for(int j=0; j<ratnans.length; j++) {	    
                assertTrue( ratnans[i].compareTo(ratnans[j]) == 0);
            }
            for(int j=0; j<ratnanans.length; j++) {
                assertGreater( ratnans[i], ratnanans[j] );
            }
        }
    }

    private void assertPos( RatNum n ) {
        assertTrue( n.isPositive() );
        assertTrue( ! n.isNegative() );
    }
    private void assertNeg( RatNum n ) {
        assertTrue( n.isNegative() );
        assertTrue( ! n.isPositive() );
    }
    public void testIsPosAndIsNeg() {
        assertTrue( ! zero.isPositive() );
        assertTrue( ! zero.isNegative() );

        assertPos(one);
        assertNeg(negOne);
        assertPos(two);
        assertPos(three);

        assertPos(one_I_two);
        assertPos(one_I_three);
        assertPos(one_I_four);
        assertPos(two_I_three);
        assertPos(three_I_four);

        assertNeg(negOne_I_two);

        assertPos(three_I_two);

        assertPos(one_I_zero);
        assertPos(negOne_I_zero); // non-intuitive; see spec
        assertPos(hundred_I_zero);
    }

    public void testIsNaN() {
        for (int i=0; i<ratnans.length; i++) {
            assertTrue(ratnans[i].isNaN());
        }
        for (int i=0; i<ratnanans.length; i++) {
            assertTrue(!ratnanans[i].isNaN());
        }
    }
    
    public void testSortedSet() {
    	SortedSet<RatNum> s = new TreeSet<RatNum>();
    	s.add(two_I_three);
    	s.add(zero);
    	s.add(one_I_zero);
    	s.add(two);
    	
    	assertTrue(zero.compareTo(two_I_three) < 0);
    	assertTrue(two_I_three.compareTo(two) < 0);
    	assertTrue(two.compareTo(one_I_zero) < 0);
    	
    	int count = 0; 
    	for (RatNum r:s) {
    		if (count == 0) {
    			eq(r,"0");
    		}
    		else if (count == 1) {
    			eq(r,"2/3");
    		}
    		else if (count == 2) {
    			eq(r,"2");
    		}
    		else if (count == 3) {
    			eq(r,"NaN");
    		}    	
    		count++;
    	}    
    }

    
    public void testSet() {
    	Set<RatNum> s = new HashSet<RatNum>();
    	
    	s.add(one_I_two);
    	RatNum two_I_four = new RatNum(2,4);
    	s.add(two_I_four);
    	assertTrue(one_I_two.equals(two_I_four));
    	assertTrue(one_I_two.hashCode() == two_I_four.hashCode());
    	
    	s.add(one_I_zero);
    	s.add(hundred_I_zero);
    	assertTrue(one_I_zero.equals(hundred_I_zero));
    	assertTrue(one_I_zero.hashCode() == hundred_I_zero.hashCode());
    	
    	assertTrue(s.size() == 2);
    	
    }

}
