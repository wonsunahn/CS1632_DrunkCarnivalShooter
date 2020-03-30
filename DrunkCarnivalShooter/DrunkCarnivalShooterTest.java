import static org.junit.Assert.*;

import gov.nasa.jpf.vm.Verify;
import org.junit.*;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>
 * Uses the Java Path Finder model checking tool to check DrunkCarnivalShooter
 * shoot method for all scenarios. It enumerates all possible states of the
 * targets as well as all possible target choices by the user.
 */

public class DrunkCarnivalShooterTest {
	private DrunkCarnivalShooter shooter; // The game object
	private boolean[] targets;
	private String failString; // A descriptive fail string for assertions

	private int targetChoice; // The user inputted target choice to test (can be between 0 - 3)

	/**
	 * Sets up the test fixture.
	 */
	@Before
	public void setUp() {
		targets = new boolean[4];
		/*
		 * TODO: Use the Java Path Finder Verify API to generate choices for targetChoice.
		 * It should take values 0-3.  To see how to use the Verify API, look at:
		 * https://github.com/javapathfinder/jpf-core/wiki/Verify-API-of-JPF
		 */
		targetChoice = Verify.getInt(0, 3);
		for(int i = 0; i < 4; i++) {
			targets[i] = Verify.getBoolean();
		}
		
		// Create the game
		shooter = DrunkCarnivalShooter.createInstance();
		// Set up the targets in the game to reflect the targets array
		for(int i = 0; i < 4; i++) {
			if(targets[i] == false) {
				shooter.takeDownTarget(i);
			}
		}
		
		// A failstring useful to pass to assertions to get a more descriptive error.
		failString = "Failure in " + shooter.getRoundString() + " (targetChoice=" + targetChoice + "):";
	}

	@After
	public void tearDown() {
		shooter = null;
		targetChoice = 0;
		targets = null;
	}

	/**
	 * Test case for void reset(Bean[] beans).
	 * Preconditions: None.
	 * Execution steps: Call logic.reset(beans).
	 * Invariants: If beanCount is greater than 0,
	 *             remaining bean count is beanCount - 1
	 *             in-flight bean count is 1 (the bean initially at the top)
	 *             in-slot bean count is 0.
	 *             If beanCount is 0,
	 *             remaining bean count is 0
	 *             in-flight bean count is 0
	 *             in-slot bean count is 0.
	 */
	@Test
	public void testShoot() {
		// TODO: Implement
		/*
		 * Currently, it just prints out the failString to demonstrate to you all the
		 * cases considered by Java Path Finder. If you called the Verify API correctly
		 * in setUp(), you should see all combinations of targets and targetChoice:
		 * 
		 * Failure in Round #0:                         (targetChoice=0):
		 * Failure in Round #0:                    ||   (targetChoice=0):
		 * Failure in Round #0:              ||         (targetChoice=0):
		 * Failure in Round #0:              ||    ||   (targetChoice=0):
		 * Failure in Round #0:        ||               (targetChoice=0):
		 * ...
		 * Failure in Round #0:  ||    ||          ||   (targetChoice=3):
		 * Failure in Round #0:  ||    ||    ||         (targetChoice=3):
		 * Failure in Round #0:  ||    ||    ||    ||   (targetChoice=3):
		 * 
		 * PLEASE REMOVE when you are done implementing.
		 */
		//System.out.println(failString);
		StringBuilder builder = new StringBuilder();
		shooter.shoot(targetChoice, builder);
		
		int remaining = 0;
		for(int i = 0; i < 4; i++) {
			if(shooter.isTargetStanding(i)) {
				remaining++;
			}
		}
		assertEquals(failString, remaining, shooter.getRemainingTargetNum());
	}
}
