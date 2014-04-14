package Project;

/**
 * Exception for when the Cascade Classifier fails to load a XML
 * 
 * @author Marcus Karpoff, Marko Babic
 * 
 */
public class ClassiferLoadFailure extends Exception {
	private static final long serialVersionUID = 1L;

	public ClassiferLoadFailure(String location) {
		super("Failure to load cascade classifier at location: " + location);

	}

}