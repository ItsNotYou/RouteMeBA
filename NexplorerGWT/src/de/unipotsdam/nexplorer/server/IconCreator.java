package de.unipotsdam.nexplorer.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IconCreator extends HttpServlet {

	private static final long serialVersionUID = -4234683874504768278L;

	private Color clear;
	private Color away;
	private Color busy;

	public IconCreator() {
		this.clear = new Color(181, 230, 29);
		this.away = new Color(255, 242, 0);
		this.busy = new Color(255, 127, 39);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String idValue = req.getParameter("id");
		if (idValue == null) {
			return;
		}

		Color background = Color.white;
		String status = req.getParameter("status");
		if (status == null) {
			return;
		} else if (status.equals("clear")) {
			background = clear;
		} else if (status.equals("away")) {
			background = away;
		} else if (status.equals("busy")) {
			background = busy;
		}

		int xSize = 16;
		int ySize = 16;

		Color transparent = Color.cyan;

		BufferedImage bufferedImage = createImage(idValue, xSize, ySize, transparent, background);
		Image finalImage = makeColorTransparent(bufferedImage, transparent);
		BufferedImage sendableCopy = copyImage(finalImage, xSize, ySize);

		resp.setContentType("image/png");
		ImageIO.write(sendableCopy, "png", resp.getOutputStream());
	}

	private BufferedImage copyImage(Image finalImage, int xSize, int ySize) {
		BufferedImage result = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);

		Graphics g = result.getGraphics();
		g.drawImage(finalImage, 0, 0, xSize, ySize, null);
		g.dispose();

		return result;
	}

	private BufferedImage createImage(String idValue, int xSize, int ySize, Color transparent, Color background) {
		int ovalWidth = 2;

		Font font = getFont();
		BufferedImage bufferedImage = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);

		Graphics g = bufferedImage.getGraphics();
		Dimension start = calculateStartCoordinates(idValue, xSize, ySize, g, font);

		g.setColor(transparent);
		g.fillRect(-1, -1, xSize + 1, ySize + 1);

		g.setColor(Color.black);
		g.fillOval(-1, -1, xSize + 1, ySize + 1);

		g.setColor(background);
		g.fillOval(ovalWidth / 2 - 1, ovalWidth / 2 - 1, xSize - ovalWidth + 1, ySize - ovalWidth + 1);

		g.setColor(Color.black);
		g.setFont(font);
		g.drawString(idValue, start.width, start.height);

		g.dispose();
		return bufferedImage;
	}

	private Font getFont() {
		return Font.decode("Verdana").deriveFont(9f);
	}

	private Dimension calculateStartCoordinates(String message, int xSize, int ySize, Graphics g, Font font) {
		TextLayout layout = new TextLayout(message, font, g.getFontMetrics(font).getFontRenderContext());

		int height = (int) layout.getBounds().getHeight();
		int width = g.getFontMetrics(font).stringWidth(message);

		int xStart = (xSize - width) / 2;
		int yStart = (ySize + height) / 2;

		return new Dimension(xStart, yStart);
	}

	/**
	 * Creates a new image with the given color as transparent. Taken from <a href="http://www.rgagnon.com/javadetails/java-0265.html">http://www.rgagnon.com/javadetails/java-0265.html</a>
	 * 
	 * @param im
	 *            Input image
	 * @param color
	 *            Color that should be transparent
	 * @return
	 */
	public static Image makeColorTransparent(Image im, final Color color) {
		ImageFilter filter = new RGBImageFilter() {

			// the color we are looking for... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFF000000;

			public final int filterRGB(int x, int y, int rgb) {
				if ((rgb | 0xFF000000) == markerRGB) {
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
				} else {
					// nothing to do
					return rgb;
				}
			}
		};

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
}
