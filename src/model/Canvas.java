package model;

import java.io.Serializable;

/**
 * The Canvas class contains the method to record the data includes min real
 * data, max real data, min imaginary data, max imaginary data, graph color
 * style, max iteration data of the graph.
 * 
 * @author ZHAOHUI XU
 *
 */
public class Canvas implements Serializable {

	private static final long serialVersionUID = 1L;
	private double minReal;
	private double maxReal;
	private double minImag;
	private double maxImag;
	private String colorStyle;
	private int maxIteration;

	/**
	 * Create Canvas instance.
	 * 
	 * @param minReal
	 * @param maxReal
	 * @param minImag
	 * @param maxImag
	 * @param colorStyle
	 * @param maxIteration
	 *
	 */
	public Canvas(double minReal, double maxReal, double minImag, double maxImag, String colorStyle, int maxIteration) {
		this.minReal = minReal;
		this.maxReal = maxReal;
		this.minImag = minImag;
		this.maxImag = maxImag;
		this.colorStyle = colorStyle;
		this.maxIteration = maxIteration;
	}

	/**
	 * The method that allows system to get the minReal value
	 * 
	 * @return
	 */
	public double getMinReal() {
		return minReal;
	}

	/**
	 * The method that allows system to get the maxReal value
	 * 
	 * @return
	 */
	public double getMaxReal() {
		return maxReal;
	}

	/**
	 * The method that allows system to get the minImag value
	 * 
	 * @return
	 */
	public double getMinImag() {
		return minImag;
	}

	/**
	 * The method that allows system to get the maxImag value
	 * 
	 * @return
	 */
	public double getMaxImag() {
		return maxImag;
	}

	/**
	 * The method that allows system to get the colorStyle value
	 * 
	 * @return
	 */
	public String getColorStyle() {
		return colorStyle;
	}

	/**
	 * The method that allows system to get the maxIteration value
	 * 
	 * @return
	 */
	public int getMaxIteration() {
		return maxIteration;
	}

	/**
	 * The method that allows system to switch different color style in each case
	 * 
	 * @param colorStyle
	 * @return
	 */
	public String getNextColor(String colorStyle) {
		switch (colorStyle) {
		case "Pure":
			return "Red";
		case "Red":
			return "Green";
		case "Green":
			return "Blue";
		case "Blue":
			return "Brown";
		case "Brown":
			return "Pure";
		}
		return "Pure";
	}
}
