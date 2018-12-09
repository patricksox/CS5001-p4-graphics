package model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;

import model.Canvas;

/**
 * The MandelbrotModel class contains the method to handle the Mandelbrot model.
 * 
 * @author ZHAOHUI XU
 *
 */

public class MandelbrotModel {

	private int width; // the width of the image
	private int height; // the height of the image
	private int stateIndex; // the index of current state
	public static final String INITIAL_COLOR_SCHEME = "Pure";
	private ArrayList<Canvas> canvas;// the list used to implement redo and undo
	private ObjectOutputStream oos;
	private BufferedImage image; // the image generated from Mandelbrot data
	MandelbrotCalculator mandelCalc;
	private FileOutputStream fos;
	private FileInputStream fis;
	private ObjectInputStream ois;
	private Canvas last;

	/**
	 * Create model instance.
	 * 
	 * @param image
	 *            width
	 * @param imagr
	 *            height
	 */
	public MandelbrotModel(int width, int height) {
		this.width = width;
		this.height = height;
		this.stateIndex = 0;
		this.canvas = new ArrayList<Canvas>() {
			// In order to maintain version compatibility during serialization,
			// deserialization still preserves the uniqueness of the object during version
			// upgrade.
			private static final long serialVersionUID = 1L;
			{
				add(new Canvas(MandelbrotCalculator.INITIAL_MIN_REAL, MandelbrotCalculator.INITIAL_MAX_REAL,
						MandelbrotCalculator.INITIAL_MIN_IMAGINARY, MandelbrotCalculator.INITIAL_MAX_IMAGINARY,
						INITIAL_COLOR_SCHEME, MandelbrotCalculator.INITIAL_MAX_ITERATIONS));
			}
		};
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * This method is used to save the current Mandelbrot parameter settings. File
	 * name format:"MSFile_yyyy-MM-dd_HH:mm:ss.ser".
	 * 
	 * @throws IOException
	 */
	public void saveParamFile() throws IOException {
		String fileName = "MSFile_";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		Date date = new Date();
		String extension = ".ser";
		fos = new FileOutputStream(fileName + dateFormat.format(date) + extension);
		oos = new ObjectOutputStream(fos);
		oos.writeObject(canvas.get(stateIndex));
	}

	/**
	 * This method is used to save the current Mandelbrot image. File name
	 * format:"MSPic_yyyy-MM-dd_HH:mm:ss.jpeg".
	 * 
	 * @throws IOException
	 */
	public void saveImage() throws IOException {
		String fileName = "MSPic_";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String extension = ".jpeg";
		Date date = new Date();
		String time = dateFormat.format(date);
		File savedImage = new File(fileName + time + extension);
		ImageIO.write(image, "JPG", savedImage);
	}

	/**
	 * update the image with current Mandelbrot parameters.
	 */
	public void updateImage() {
		last = canvas.get(stateIndex);
		mandelCalc = new MandelbrotCalculator();
		int[][] mandelbrotData = mandelCalc.calcMandelbrotSet(width, height, last.getMinReal(), last.getMaxReal(),
				last.getMinImag(), last.getMaxImag(), last.getMaxIteration(),
				MandelbrotCalculator.DEFAULT_RADIUS_SQUARED);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int value = mandelbrotData[y][x];
				int color = calculateColor(value, last.getColorStyle());
				image.setRGB(x, y, color);
			}
		}
	}

	/**
	 * getter of the image
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * calculator of the color with given color scheme. all color schemes except for
	 * the "Pure" are implemented by modifying the HSB values. for "blue", "red" and
	 * "green"and "Brown", the brightness is changed according to the Mandelbrot set.
	 * 
	 * @param value
	 * @param colorStyle
	 * @return
	 */
	public int calculateColor(int value, String colorStyle) {
		int maxIteration = canvas.get(stateIndex).getMaxIteration();
		if (value == maxIteration)
			return 0x00000000;
		switch (colorStyle) {
		case "Pure":
			return 0xffffffff;
		case "Red":
			return Color.HSBtoRGB(1f, 1f, ((float) value / maxIteration) % 1f);
		case "Green":
			return Color.HSBtoRGB(0.333f, 1f, ((float) value / maxIteration) % 1f);
		case "Blue":
			return Color.HSBtoRGB(0.55f, 1f, ((float) value / maxIteration) % 1f);
		case "Brown":
			return Color.HSBtoRGB(1.1f, 1f, ((float) value / maxIteration) % 1f);
		}
		return 0xffffffff;
	}

	/**
	 * This method is used to load Mandelbrot set data from a saved parameter
	 * settings.
	 * 
	 * @param file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadParamFile(File file) throws IOException, ClassNotFoundException {
		fis = new FileInputStream(file);
		ois = new ObjectInputStream(fis);
		Canvas ca = (Canvas) ois.readObject();
		canvas.add(ca);
		stateIndex++;
	}

	/**
	 * this method is used to remove the parameters after the current state. the
	 * main reason for this is to let the system store subsequent data after undo.
	 */
	public void removeData() {
		for (int i = canvas.size() - 1; i > stateIndex; i--) {
			canvas.remove(i);
		}
	}

	/**
	 * There are four version of this method to add a new state to the data list
	 * that takes in different parameters. The first state does not take in
	 * arguments. This is used to change the color setting for the image. The color
	 * setting sequence is built in the parameter class. Other parameters are
	 * inherited from the former state.
	 */
	public void setNextState() {
		stateIndex++;
		last = canvas.get(stateIndex - 1);
		canvas.add(new Canvas(last.getMinReal(), last.getMaxReal(), last.getMinImag(), last.getMaxImag(),
				last.getNextColor(last.getColorStyle()), last.getMaxIteration()));
	}

	/**
	 * The second one takes in a square from the former Mandelbrot Set image. The
	 * new Mandelbrot Set data is generated with the ranges for real and imaginary
	 * part from the square. Other parameters are inherited from the former state.
	 * 
	 * @param getxMin
	 * @param getxMax
	 * @param getyMin
	 * @param getyMax
	 */
	public void setNextState(double getxMin, double getxMax, double getyMin, double getyMax) {
		stateIndex++;
		last = canvas.get(stateIndex - 1);
		double minr = getyMin / 850.0 * (last.getMaxReal() - last.getMinReal()) + last.getMinReal();
		double mini = getxMin / 850.0 * (last.getMaxImag() - last.getMinImag()) + last.getMinImag();
		double maxr = getyMax / 850.0 * (last.getMaxReal() - last.getMinReal()) + last.getMinReal();
		double maxi = getxMax / 850.0 * (last.getMaxImag() - last.getMinImag()) + last.getMinImag();
		canvas.add(new Canvas(minr, maxr, mini, maxi, last.getColorStyle(), last.getMaxIteration()));
	}

	/**
	 * The third state takes in new max iteration number. This is used to update the
	 * max iteration number. Other parameters are inherited from the former state.
	 * 
	 * @param maxIteration
	 */
	public void setNextState(int maxIteration) {
		stateIndex++;
		last = canvas.get(stateIndex - 1);
		canvas.add(new Canvas(last.getMinReal(), last.getMaxReal(), last.getMinImag(), last.getMaxImag(),
				last.getColorStyle(), maxIteration));
	}

	/**
	 * The forth one takes in a line from the former Mandelbrot Set image. The new
	 * Mandelbrot Set data is generated from a line. Other parameters are inherited from the former state.
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 */
	public void setNextState2(double x1, double x2, double y1, double y2) {
		stateIndex++;
		last = canvas.get(stateIndex - 1);
		double minr = last.getMinReal()
				- ((y2 - y1) * (last.getMaxReal() - last.getMinReal()) / last.getMaxIteration() * 0.5);
		double mini = last.getMinImag()
				- ((x2 - x1) * (last.getMaxImag() - last.getMinImag()) / last.getMaxIteration() * 0.5);
		double maxr = last.getMaxReal()
				- ((y2 - y1) * (last.getMaxReal() - last.getMinReal()) / last.getMaxIteration() * 0.5);
		double maxi = last.getMaxImag()
				- ((x2 - x1) * (last.getMaxImag() - last.getMinImag()) / last.getMaxIteration() * 0.5);
		canvas.add(new Canvas(minr, maxr, mini, maxi, last.getColorStyle(), last.getMaxIteration()));
	}

	/**
	 * getter of the data list.
	 * 
	 * @return
	 */
	public ArrayList<Canvas> getData() {
		return canvas;
	}

	/**
	 * getter of the current state.
	 * 
	 * @return
	 */
	public Canvas getState() {
		return canvas.get(stateIndex);
	}

	/**
	 * getter of Index.
	 * 
	 * @return
	 */
	public int getStateIndex() {
		return stateIndex;
	}

	/**
	 * setter of Index.
	 * 
	 * @param stateIndex
	 */
	public void setStateIndex(int stateIndex) {
		this.stateIndex = stateIndex;
	}

}
