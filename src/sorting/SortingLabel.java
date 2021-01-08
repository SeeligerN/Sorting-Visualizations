package sorting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;

public class SortingLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SortingMain sm;
	private float yOffset = 0f;

	private int[] toVisualize;
	private int maxValue = 1;

	public SortingLabel(SortingMain sm) {
		this.sm = sm;
		
		SortingLabel sl = this;
		this.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = (int) ((float) e.getX() / ((float) sl.getWidth() / (float) toVisualize.length));
				int y = toVisualize[x];
				sl.setToolTipText("X: " + x + "; Y: " + y);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}
		});
		
		this.setVisible(true);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (toVisualize == null) {
			return;
		}

		g.setColor(Color.WHITE);
		for (int i = 0; i < toVisualize.length; i++) {
			if (sm.isColorized())
				g.setColor(Color.getHSBColor((float) toVisualize[i] / (float) maxValue / 2, 1, 1));
			if (sm.getVisualisation() == 0)
				g.fillRect((int) (i * ((float) this.getWidth() / (float) toVisualize.length)),
						(int) (this.getHeight()
								- ((float) (toVisualize[i] + yOffset) * ((float) this.getHeight() / (float) maxValue))),
						(int) Math.ceil((float) this.getWidth() / (float) toVisualize.length),
						(int) Math.ceil((float) this.getWidth() / (float) toVisualize.length));
			else if (sm.getVisualisation() == 1)
				g.fillRect((int) (i * ((float) this.getWidth() / (float) toVisualize.length)),
						(int) (this.getHeight()
								- ((float) (toVisualize[i] + yOffset) * ((float) this.getHeight() / (float) maxValue))),
						(int) Math.ceil((float) this.getWidth() / (float) toVisualize.length),
						(int) Math.ceil((float) (toVisualize[i] + 1) * ((float) this.getHeight() / (float) maxValue)));
			else if (sm.getVisualisation() == 2)
				if (i == 0)
					continue;
				else
					g.drawLine((int) (i * ((float) this.getWidth() / (float) toVisualize.length)),
							(int) (this.getHeight() - ((float) (toVisualize[i] + yOffset)
									* ((float) this.getHeight() / (float) maxValue))),
							(int) ((i - 1) * ((float) this.getWidth() / (float) toVisualize.length)),
							(int) (this.getHeight() - ((float) (toVisualize[i - 1] + yOffset)
									* ((float) this.getHeight() / (float) maxValue))));
		}

		if (sm.isGridded()) {
			g.setColor(Color.GRAY);
			int spacing = (int) Math.pow(10, (int) Math.log10(maxValue / 2) - 1);
			if (spacing == 0)
				return;
			int segments = (maxValue / spacing) + 1;
			float height = (1f / ((float) maxValue / spacing)) * (float) this.getHeight();
			for (int i = 0; i < segments + 1; i += 5) {
				int yPos = (int) (height * i);
				g.drawLine(0, this.getHeight() - yPos, this.getWidth(), this.getHeight() - yPos);
				g.drawString("" + spacing * i, 0, this.getHeight() - yPos);
			}
		}
	}

	public void visualize(int maxValue) {
		this.maxValue = maxValue;
		repaint();
	}

	public void visualize(int[] toVisualize, int maxValue) {
		this.toVisualize = toVisualize;
		this.maxValue = maxValue;
		repaint();
	}
}
