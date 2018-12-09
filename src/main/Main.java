package main;
import guiDelegate.GuiDelegate;
/**
 * Start a main function.
 * @param args - user input
 * @author 180019653
 */
public class Main {
		public static void main(String[] args) {
			// pass the model object to the delegate, so that it can observe, display, and change the model
			new GuiDelegate(); 
		}
}
