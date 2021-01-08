package sorting;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SortingMain extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Random r = new Random();

	private Dimension maxLabelSize = new Dimension(999, 25);
	private Dimension wSize = new Dimension(940, 780);
	private Font header = new Font("Arial", Font.BOLD, 20);

	private String[] optionsUnsorted = { "Zufall", "zufällige Ordnung", "Kopfüber", "Sortiert" };
	private String[] optionsAlgorithm = { "QuickSort", "CountingSort", "BubbleSort", "InsertionSort", "MergeSort",
			"SelectionSort", "ShellSort", "HeapSort" };

	boolean hasUnsorted = false;
	int[] toSort;
	int size = 512;
	int elements = 512;
	int resolution = 100;
	int max = 1000;
	float waitTime;

	Thread curThread;
	Runnable curRunnable;

	private JSeparator sep1, sep2, sep3;
	private JLabel sizeL, elementsL, waitTimeL, timeOutput, comparisonsOutput, resolutionL, differenceL, general,
			single, multi;
	private JTextField sizeF, elementsF, resolutionF, differenceF;
	private JComboBox<String> algorithmBox, scrambleMethod;
	private JButton sortButton, timeButton, abortButton;
	private JCheckBox visible, gridded, colorized, comparisons;
	private JSlider delay;
	private SortingLabel sortingLabel;
	private JRadioButton collumns, dots, lines;

	public static void main(String[] args) {
		new SortingMain();
	}

	public SortingMain() {
		this.setSize(wSize);
		this.setTitle("Algorithmen");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		sortButton = new JButton("START");
		sortButton.addActionListener(ae -> {
			if (!updateFields())
				return;

			abortRunning();

			curRunnable = new Algorithms(sortingLabel, (String) algorithmBox.getSelectedItem(), this, false, 0, 0);
			curThread = new Thread(curRunnable);
			curThread.start();
			setAbortable(true);
		});

		abortButton = new JButton("STOP");
		abortButton.setEnabled(false);
		abortButton.addActionListener(ae -> {
			abortRunning();
			setAbortable(false);
		});

		sortingLabel = new SortingLabel(this);
		sortingLabel.setMinimumSize(new Dimension(0, 0));
		sortingLabel.setMaximumSize(new Dimension(9999, 9999));
		sortingLabel.setPreferredSize(new Dimension(9999, 9999));

		sizeL = new JLabel("Definitionsbereich: ");

		elementsL = new JLabel("Mengenbereich: ");

		sizeF = new JTextField(String.valueOf(size));
		sizeF.addActionListener(ae -> {
			updateFields();
			sortingLabel.visualize(toSort, elements);
		});
		sizeF.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (getNeedsElements())
					elementsF.setText(sizeF.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (getNeedsElements())
					elementsF.setText(sizeF.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		sizeF.setMaximumSize(maxLabelSize);

		elementsF = new JTextField(String.valueOf(elements));
		elementsF.setMaximumSize(maxLabelSize);
		elementsF.addActionListener(ae -> {
			updateFields();
			sortingLabel.visualize(toSort, elements);
		});

		scrambleMethod = new JComboBox<>(optionsUnsorted);
		scrambleMethod.setMaximumSize(maxLabelSize);
		scrambleMethod.addActionListener(ae -> {
			boolean needsElements = getNeedsElements();
			elementsL.setEnabled(!needsElements);
			elementsF.setEnabled(!needsElements);

			elementsF.setText(sizeF.getText());

			setSorted();
			updateFields();
			generateArray(size);
			sortingLabel.visualize(toSort, elements);
		});

		algorithmBox = new JComboBox<>(optionsAlgorithm);
		algorithmBox.setMaximumRowCount(optionsAlgorithm.length);
		algorithmBox.setToolTipText("Der Algorithmus, der genutzt wird");
		algorithmBox.setMaximumSize(maxLabelSize);

		waitTimeL = new JLabel(String.format("Hemmung : %.3f s", waitTime / 1000));

		delay = new JSlider(-100, 100, 0);
		delay.addChangeListener(ce -> {
			if (delay.getValue() == delay.getMaximum())
				waitTime = 0;
			else
				waitTime = (float) Math.pow(1.2, -(delay.getValue() - (delay.getMaximum() / 2)) / 2);
			waitTimeL.setText(String.format("Hemmung : %.3f s", waitTime / 1000));
			if (curRunnable instanceof Algorithms) {
				((Algorithms) curRunnable).abortCurrentWaiting();
			}
		});
		delay.setMinorTickSpacing(5);
		delay.setMajorTickSpacing(25);
		delay.getChangeListeners()[0].stateChanged(null);
		delay.setSnapToTicks(false);
		delay.setPaintTicks(true);

		visible = new JCheckBox("Visualisiert");
		visible.setSelected(true);

		gridded = new JCheckBox("Einteilung");
		gridded.setSelected(false);
		gridded.addActionListener(ae -> repaint());

		colorized = new JCheckBox("Farbig");
		colorized.setSelected(true);
		colorized.addActionListener(ae -> {
			repaint();
		});

		comparisons = new JCheckBox("Vergleiche");
		colorized.setSelected(true);

		ActionListener aeL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				repaint();
			}
		};

		collumns = new JRadioButton("Säulen");
		collumns.addActionListener(aeL);
		collumns.setSelected(true);

		lines = new JRadioButton("Linie");
		lines.addActionListener(aeL);

		dots = new JRadioButton("Punkte");
		dots.addActionListener(aeL);

		ButtonGroup bg = new ButtonGroup();
		bg.add(collumns);
		bg.add(lines);
		bg.add(dots);

		timeOutput = new JLabel();
		setTime(0l);

		comparisonsOutput = new JLabel();
		setComparisons(0);

		this.setVisible(true);

		GroupLayout gl = new GroupLayout(this.getContentPane());
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		this.setLayout(gl);

		sep1 = new JSeparator();

		sep2 = new JSeparator();

		sep3 = new JSeparator();

		resolutionL = new JLabel("Auflösung: ");

		differenceL = new JLabel("Maximum:  ");

		resolutionF = new JTextField("500");
		resolutionF.setMaximumSize(maxLabelSize);

		differenceF = new JTextField("1000");
		differenceF.setMaximumSize(maxLabelSize);

		timeButton = new JButton("START");
		timeButton.addActionListener(ae -> {
			if (!updateFields())
				return;

			abortRunning();

			if (comparisons.isSelected()) {
				curRunnable = new Algorithms(sortingLabel, (String) algorithmBox.getSelectedItem(), this, true, max,
						resolution);
				curThread = new Thread(curRunnable);
			} else {
				curRunnable = new TimedAlgorithms(sortingLabel, (String) algorithmBox.getSelectedItem(), this,
						resolution, max);
				curThread = new Thread(curRunnable);
			}
			curThread.start();
			setAbortable(true);
		});

		general = new JLabel("Allgemein");
		general.setFont(header);

		single = new JLabel("Einzelsortierung");
		single.setFont(header);

		multi = new JLabel("Messung");
		multi.setFont(header);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
		}

		gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(sortingLabel)
				.addGroup(gl.createParallelGroup().addComponent(general, Alignment.CENTER).addComponent(algorithmBox)
						.addGroup(gl.createSequentialGroup().addComponent(elementsL).addComponent(elementsF))
						.addComponent(scrambleMethod).addComponent(gridded).addComponent(colorized)
						.addComponent(collumns).addComponent(lines).addComponent(dots).addComponent(sep1)
						.addComponent(single, Alignment.CENTER)
						.addGroup(gl.createSequentialGroup().addComponent(sizeL).addComponent(sizeF))
						.addComponent(visible).addComponent(delay).addComponent(waitTimeL).addComponent(timeOutput)
						.addComponent(comparisonsOutput).addComponent(sep2).addComponent(sortButton, Alignment.CENTER)
						.addComponent(multi, Alignment.CENTER).addComponent(comparisons)
						.addGroup(gl.createSequentialGroup().addComponent(resolutionL).addComponent(resolutionF))
						.addGroup(gl.createSequentialGroup().addComponent(differenceL).addComponent(differenceF))
						.addComponent(timeButton, Alignment.CENTER).addComponent(sep3)
						.addComponent(abortButton, Alignment.CENTER)));

		gl.setVerticalGroup(gl.createParallelGroup().addComponent(sortingLabel).addGroup(gl.createSequentialGroup()
				.addComponent(general).addComponent(algorithmBox)
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(elementsL).addComponent(elementsF))
				.addComponent(scrambleMethod).addComponent(gridded).addComponent(colorized).addComponent(collumns)
				.addComponent(lines).addComponent(dots).addComponent(sep1).addComponent(single)
				.addGroup(gl.createParallelGroup(Alignment.BASELINE).addComponent(sizeL).addComponent(sizeF))
				.addComponent(visible).addComponent(delay).addComponent(waitTimeL).addComponent(timeOutput)
				.addComponent(comparisonsOutput).addComponent(sortButton).addComponent(sep2).addComponent(multi)
				.addComponent(comparisons)
				.addGroup(
						gl.createParallelGroup(Alignment.BASELINE).addComponent(resolutionL).addComponent(resolutionF))
				.addGroup(
						gl.createParallelGroup(Alignment.BASELINE).addComponent(differenceL).addComponent(differenceF))
				.addComponent(timeButton).addComponent(sep3).addComponent(abortButton)));

		generateArray(size);
		sortingLabel.visualize(toSort, elements);
	}

	/**
	 * Generates a new Array in correspondence with the selected scramble method.
	 * (Does not generate a new Array should the still be an unsorted one saved. )
	 * And returns that Array.
	 * 
	 * @param newSize
	 *            the new Size of the Array
	 * @return the scrambled Array.
	 */
	public int[] generateArray(int newSize) {

		if (hasUnsorted)
			return toSort;

		newSize = newSize > 0 ? newSize : -newSize;

		toSort = new int[newSize];

		if (optionsUnsorted[scrambleMethod.getSelectedIndex()].equalsIgnoreCase("zufällige ordnung")) {

			List<Integer> ints = new ArrayList<>();
			for (int i = 0; i < toSort.length; i++)
				ints.add(i);

			Collections.shuffle(ints);

			for (int i = 0; i < toSort.length; i++)
				toSort[i] = ints.get(i);

		} else if (optionsUnsorted[scrambleMethod.getSelectedIndex()].equalsIgnoreCase("zufall")) {

			for (int i = 0; i < toSort.length; i++) {
				toSort[i] = r.nextInt(elements);
			}

		} else if (optionsUnsorted[scrambleMethod.getSelectedIndex()].equalsIgnoreCase("kopfüber")) {

			for (int i = 0; i < toSort.length; i++) {
				toSort[i] = toSort.length - 1 - i;
			}

		} else if (optionsUnsorted[scrambleMethod.getSelectedIndex()].equalsIgnoreCase("sortiert")) {

			for (int i = 0; i < toSort.length; i++) {
				toSort[i] = i;
			}

		}
		hasUnsorted = true;
		return toSort;
	}

	/**
	 * Checks if a value is contained within a certain Array.
	 * 
	 * @param toBeChecked
	 *            the Array that is to be checked.
	 * @param toCheck
	 *            the value that should be searched for.
	 * @return wether or not the value is contained within the Array.
	 */
	public boolean contains(int[] toBeChecked, int toCheck) {
		for (int i : toBeChecked)
			if (i == toCheck)
				return true;
		return false;
	}

	/**
	 * Sets if the user should be able to abort the sorting and start a new sorting
	 * by disabling the corresponding button.
	 * 
	 * @param abortable
	 *            if the user is able to abort the current one.
	 */
	public void setAbortable(boolean abortable) {
		timeButton.setEnabled(!abortable);
		sortButton.setEnabled(!abortable);
		abortButton.setEnabled(abortable);
	}

	/**
	 * Aborts the current Sorting or TimedSorting.
	 */
	@SuppressWarnings("deprecation")
	public void abortRunning() {
		try {
			curThread.stop();
		} catch (Exception e) {
		}
	}

	/**
	 * Writes the values of the JTextFields into the corresponding variable. Sets
	 * the text of the Text Field to "ERROR" if it should not be an integer value.
	 * Also generates a new Array should any of the Values have changed.
	 * 
	 * @return the validity of the fields. true if there were no errors. false if
	 *         the was an error and an array could not be generated.
	 */
	public boolean updateFields() {
		boolean valid = true;
		boolean needsNewArray = false;
		try {
			int newSize = Integer.valueOf(sizeF.getText());
			if (newSize != size)
				needsNewArray = true;
			size = newSize;
		} catch (Exception e) {
			sizeF.setText("ERROR");
			valid = false;
		}
		try {
			int newElements = Integer.valueOf(elementsF.getText());
			if (newElements != elements)
				needsNewArray = true;
			elements = newElements;
		} catch (Exception e) {
			elementsF.setText("ERROR");
			valid = false;
		}
		try {
			resolution = Integer.valueOf(resolutionF.getText());
		} catch (Exception e) {
			resolutionF.setText("ERROR");
			valid = false;
		}
		try {
			max = Integer.valueOf(differenceF.getText());
		} catch (Exception e) {
			differenceF.setText("ERROR");
			valid = false;
		}

		if (needsNewArray) {
			setSorted();
			generateArray(size);
		}

		return valid;
	}

	/**
	 * Method to find out if the sorting should be visualised
	 * 
	 * @return if it should be visualized.
	 */
	public boolean isVisualized() {
		return visible.isSelected();
	}

	/**
	 * Method to find out what kind of Visualisation should be used.
	 * 
	 * @return 0 if dots should be drawn; 1 if collumns should be drawn; 2 if there
	 *         should be a line
	 */
	public int getVisualisation() {
		if (dots.isSelected())
			return 0;
		if (collumns.isSelected())
			return 1;
		if (lines.isSelected())
			return 2;
		return 2;
	}

	/**
	 * special method only used by TimedAlgorithms to set the size of the array.
	 * 
	 * @param newSize
	 *            the new size value specifying how large the array should now be.
	 * @return the new generated array.
	 */
	public int[] setSize(int newSize) {
		sizeF.setText("" + newSize);
		updateFields();
		return toSort;
	}

	/**
	 * @return the timeValue that should be waited in between visualisations in
	 *         Milliseconds.
	 */
	public float getWait() {
		return waitTime;
	}

	/**
	 * @return If the user should be able to adjust the number of elements manually.
	 */
	public boolean getNeedsElements() {
		if (optionsUnsorted[scrambleMethod.getSelectedIndex()].equalsIgnoreCase("zufällige ordnung"))
			return true;
		if (optionsUnsorted[scrambleMethod.getSelectedIndex()].equalsIgnoreCase("kopfüber"))
			return true;
		if (optionsUnsorted[scrambleMethod.getSelectedIndex()].equalsIgnoreCase("sortiert"))
			return true;
		return false;
	}

	/**
	 * Changes and formats the time label.
	 * 
	 * @param timeInMillis
	 *            the time to be written into the timelabel in Milliseconds.
	 */

	public void setTime(long timeInMillis) {
		timeOutput.setText(String.format("Zeit : %.3f s", (((float) timeInMillis / 1000f))));
	}

	/**
	 * CHanges and formats the comparisons output label.
	 * 
	 * @param comparisons
	 *            the amount of comparisons to set the label to
	 */
	public void setComparisons(int comparisons) {
		comparisonsOutput.setText("Vergleiche: " + comparisons);
	}

	/**
	 * A get-Method returning if the Visualisation should be colorized.
	 * 
	 * @return if the visualisation should be colorized.
	 */
	public boolean isColorized() {
		return colorized.isSelected();
	}

	/**
	 * A get-Method returning if there should be horizontal lines showing the
	 * magnitude of the results
	 * 
	 * @return if lines should be drawn
	 */
	public boolean isGridded() {
		return gridded.isSelected();
	}

	/**
	 * Let's the class know that the Array is no longer unsorted and that it has to
	 * be regenerated before passing into Sorting.
	 */
	public void setSorted() {
		hasUnsorted = false;
	}
}