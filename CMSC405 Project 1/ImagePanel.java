import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

final class ImagePanel extends JPanel {

  private Application parent;
  private int[][] imageArray;
  private BufferedImage bufferedImage;

  protected ImagePanel(Application parent, int[][] imageArray) {
    this.setApplication(parent);
    this.setImageArray(imageArray);
    this.setBufferedImage(new BufferedImage(parent.getImageSizeConstant(),
      parent.getImageSizeConstant(), BufferedImage.TYPE_INT_RGB));
    this.buildImage();
  }

  // Setters
  private void setApplication(Application parent) {
    this.parent = parent;
  }

  private void setImageArray(int[][] imageArray) {
    this.imageArray = imageArray;
  }
  private void setBufferedImage(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
  }

  // Getters

  protected Application getApplication() {
    return this.parent;
  }

  protected int[][] getImageArray() {
    return this.imageArray;
  }

  protected BufferedImage getBufferedImage() {
    return this.bufferedImage;
  }

  // Utility methods
 
  @Override
  protected void paintComponent(Graphics g) {

    super.paintComponent(g);

    int halfImageSize;
    Graphics2D g2;
    AffineTransform savedTransform;

    halfImageSize = this.getApplication().getImageSizeConstant() / 2;
    g2 = (Graphics2D) g.create();
    savedTransform = new AffineTransform();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    savedTransform.translate(this.getWidth() / 2.0, this.getHeight() / 2.0);

    savedTransform.scale(1.0, -1.0);
    savedTransform.translate(0, -this.getBufferedImage().getHeight());

    savedTransform.concatenate(this.getApplication().getAffineTransform());

    savedTransform.scale(1.0, -1.0);
    savedTransform.translate(0, -this.getBufferedImage().getHeight());

    g2.transform(savedTransform);

    g2.drawImage(this.getBufferedImage(), -halfImageSize, -halfImageSize, this);

    g2.dispose();
  }

  private void buildImage() {

    int imageSize;
    int[][] array;
    BufferedImage image;

    imageSize = this.getApplication().getImageSizeConstant();
    array = this.getImageArray();
    image = this.getBufferedImage();

     for (int x = 0; x < imageSize; x++) {
      for (int y = 0; y < imageSize; y++) {
        image.setRGB(x, y, array[x][y]);
      }
    }
  }
}