import java.util.Locale;
import java.util.ResourceBundle;

import com.ickik.ladder.view.ConfigurationFrame;



/**
 * Main class on which the soft start.
 * @author Patrick Allgeyer
 * @version 0.1.000, 09/03/11
 */
public class Ladder {

	
	/**
	 * Main method. No argument is required.
	 * @param args unused.
	 */
	public static void main(String[] args) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("language", new Locale("en", "US"));
		new ConfigurationFrame(resourceBundle);
	}
}
