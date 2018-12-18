import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

final class Application extends JFrame {

  private static final int TIMER_DELAY = 1600;
  private static final boolean DEBUG = false;
  private static final int IMAGE_SIZE = 25;

  private int windowHeight, windowWidth;
  private String windowTitle;
  private JFrame mainFrame;
  private JPanel mainPanel, topPanel, buttonPanel, imagesPanel, logPanel;
  private ImagePanel leftImagePanel, midImagePanel, rightImagePanel;
  private JButton startButton, pauseButton, clearButton;
  private JLabel leftLabel, rightLabel;
  private JTextArea logJta;
  private JScrollPane logScrollPane;
  private ArrayList<ImagePanel> imageArray;
  private int frameCounter;
  private AffineTransform affineTransform;
  private Timer animationTimer;

  protected Application() {

    super("Java 2D Project");
    this.setWindowHeight(500);
    this.setWindowWidth(680);
    this.setWindowTitle("Java 2D Project");

    this.setImageArray(new ArrayList<>());
    this.setFrameCounter(0);
    this.setAffineTransform(new AffineTransform());
    this.setAnimationTimer(new Timer(Application.TIMER_DELAY, new Application.TimerListener()));

    this.constructGUI();
  }

  protected Application(int windowHeight, int windowWidth, String windowTitle) {

    super(windowTitle);
    this.setWindowHeight(windowHeight);
    this.setWindowWidth(windowWidth);
    this.setWindowTitle(windowTitle);

    this.setImageArray(new ArrayList<>());
    this.setFrameCounter(0);
    this.setAffineTransform(new AffineTransform());
    this.setAnimationTimer(new Timer(Application.TIMER_DELAY, new Application.TimerListener()));

    this.constructGUI();
  }

  // Setters

  private void setWindowWidth(int windowWidth) {
	    this.windowWidth = windowWidth;
  }
  private void setWindowHeight(int windowHeight) {
    this.windowHeight = windowHeight;
  }

  private void setWindowTitle(String windowTitle) {
    this.windowTitle = windowTitle;
  }

  private void setImageArray(ArrayList<ImagePanel> imageArray) {
    this.imageArray = imageArray;
  }
  
  private void setAffineTransform(AffineTransform affineTransform) {
	    this.affineTransform = affineTransform;
 }
  private void setAnimationTimer(Timer animationTimer) {
	    this.animationTimer = animationTimer;
 }
  private void setFrameCounter(int frameCounter) {
    this.frameCounter = frameCounter;
 }

  // Getters
  protected int getWindowWidth() {
    return this.windowWidth;
  }
  
  protected int getWindowHeight() {
    return this.windowHeight;
  }

  protected String getWindowTitle() {
    return this.windowTitle;
  }

  protected ArrayList<ImagePanel> getImageArray() {
    return this.imageArray;
  }
  
  public AffineTransform getAffineTransform() {
    return this.affineTransform;
  }

  protected Timer getAnimationTimer() {
    return this.animationTimer;
  }
  
  protected int getFrameCounter() {
    return this.frameCounter;
  }

  protected int getImageSizeConstant() {
    return Application.IMAGE_SIZE;
  }

  // Building the GUI

  private void constructGUI() {

    this.mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    this.topPanel = new JPanel(new BorderLayout());
    this.buttonPanel = new JPanel(new GridLayout(1, 5, 5, 5));
    this.imagesPanel = new JPanel(new GridLayout(1, 3, 10, 10));
    this.logPanel = new JPanel(new GridLayout(1, 1));

    this.leftImagePanel = new ImagePanel(this, this.buildFlag()); 
    this.midImagePanel = new ImagePanel(this, this.buildTriangle()); 
    this.rightImagePanel = new ImagePanel(this, this.buildLetterZ());  

    this.getImageArray().add(this.leftImagePanel);
    this.getImageArray().add(this.midImagePanel);
    this.getImageArray().add(this.rightImagePanel);

    this.leftImagePanel.setBackground(Color.WHITE);
    this.midImagePanel.setBackground(Color.WHITE);
    this.rightImagePanel.setBackground(Color.WHITE);
    this.leftImagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    this.midImagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    this.rightImagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    this.buttonPanel.setBorder(BorderFactory.createTitledBorder("Options"));
    this.imagesPanel.setBorder(BorderFactory.createTitledBorder("Images"));
    this.logPanel.setBorder(BorderFactory.createTitledBorder("Status log"));

    this.startButton = new JButton("Start");
    this.pauseButton = new JButton("Pause");
    this.clearButton = new JButton("Clear");
    this.leftLabel = new JLabel(); 
    this.rightLabel = new JLabel(); 

    this.logJta = new JTextArea();
    this.logJta.setEditable(false);
    this.logJta.setFont(new Font("Monospaced", 0, 12));
    this.logJta.setLineWrap(true);
    this.logScrollPane = new JScrollPane(this.logJta);

    this.buttonPanel.add(this.leftLabel);
    this.buttonPanel.add(this.startButton);
    this.buttonPanel.add(this.pauseButton);
    this.buttonPanel.add(this.clearButton);
    this.buttonPanel.add(this.rightLabel);

    this.imagesPanel.add(this.leftImagePanel);
    this.imagesPanel.add(this.midImagePanel);
    this.imagesPanel.add(this.rightImagePanel);

    this.logPanel.add(this.logScrollPane);

    this.topPanel.add(this.buttonPanel, BorderLayout.NORTH);
    this.topPanel.add(this.imagesPanel, BorderLayout.CENTER);
    this.mainPanel.add(this.topPanel);
    this.mainPanel.add(this.logPanel);

    //Action Handlers 
    this.startButton.addActionListener((ActionEvent e) -> {
      this.buttonHandler(false, "Starting animation", "Press \"Pause\" to pause", "start");
    });

    this.pauseButton.addActionListener((ActionEvent e) -> {
      this.buttonHandler(true, "Pausing animation", "Press \"Start\" to resume", "stop");
    });

    this.clearButton.addActionListener((ActionEvent e) -> {
      this.clearLog();
    });

    this.mainFrame = new JFrame(this.getWindowTitle());
    this.mainFrame.setContentPane(this.mainPanel);
    this.mainFrame.setSize(this.getWindowWidth(), this.getWindowHeight());
    this.mainFrame.setResizable(false);
    this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.mainFrame.setVisible(true);
  }

  // Utility methods

  private void addLogEntry(String message) {
    this.logJta.append(message + "\n");

    if (Application.DEBUG) {
      System.out.println(message);
    }
  }

  private void buttonHandler(boolean condition, String successMessage, String errorMessage,
      String methodName) {

    try {
      Method timerMethod;
      timerMethod = Timer.class.getDeclaredMethod(methodName);

      if (this.getAnimationTimer().isRunning() == condition) {

        this.addLogEntry(successMessage);

        timerMethod.invoke(this.getAnimationTimer());
      } else {

        this.addLogEntry(errorMessage);
      }
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
      this.addLogEntry("Error: " + ex);
    }
  }

  private void clearLog() {
    this.logJta.setText("");
  }

  // Animation methods

  private void handleTransformation() {

    int maxTransformations;

    if (Application.DEBUG) {
      maxTransformations = 6;
      this.testTransformation();
    } else {
      maxTransformations = 4;
      this.performTransformation();
    }

    this.getImageArray().forEach((ImagePanel panel) -> {
      panel.repaint();
    });

    this.setFrameCounter(this.getFrameCounter() + 1);

    if (this.getFrameCounter() > maxTransformations) {
      this.setFrameCounter(0);
      this.setAffineTransform(new AffineTransform());
    }
  }

  private void performTransformation() {
    switch (this.getFrameCounter()) {
      case 0:
        this.addLogEntry("Translate images -5 in x direction, Translate +7 in the y direction");
        this.getAffineTransform().translate(-5.0, 7.0);
        break;
      case 1:
        this.addLogEntry("Rotate images 45 degrees counterclockwise");
        this.getAffineTransform().rotate(45 * Math.PI / 180.0);
        break;
      case 2:
        this.addLogEntry("Rotate images 90 degrees clockwise");
        this.getAffineTransform().rotate(-90 * Math.PI / 180.0);
        break;
      case 3:
        this.addLogEntry("Scale images 2 times along x-axis and 0.5 times along y-axis");
        this.getAffineTransform().scale(2.0, 0.5);
        break;
      case 4:
        this.addLogEntry("Reset images to original positions");
        break;
      default:
        this.addLogEntry("Error: Invalid frameCounter value");
        break;
    }
  }

  private void testTransformation() {
    switch (this.getFrameCounter()) {
      case 0:
        this.addLogEntry("Translate images -5 units along the x-axis");
        this.getAffineTransform().translate(-5.0, 0);
        break;
      case 1:
        this.addLogEntry("Translate images +7 units along the y-axis");
        this.getAffineTransform().translate(0, 7.0);
        break;
      case 2:
        this.addLogEntry("Rotate images 45 degrees counterclockwise");
        this.getAffineTransform().rotate(45 * Math.PI / 180.0);
        break;
      case 3:
        this.addLogEntry("Rotate images 90 degrees clockwise");
        this.getAffineTransform().rotate(-90 * Math.PI / 180.0);
        break;
      case 4:
        this.addLogEntry("Scale images 2 times along x-axis");
        this.getAffineTransform().scale(2.0, 1.0);
        break;
      case 5:
        this.addLogEntry("Scale images 0.5 times along y-axis");
        this.getAffineTransform().scale(1.0, 0.5);
        break;
      case 6:
        this.addLogEntry("Reset images to original positions");
        break;
      default:
        this.addLogEntry("Error: Invalid frameCounter value");
        break;
    }
  }

  // Image methods
  private int[][] buildLetterZ() {

    int[][] builtImage;
    int z;

    builtImage = new int[Application.IMAGE_SIZE][Application.IMAGE_SIZE];

    for (int x = 0; x < Application.IMAGE_SIZE; x++) {
      z = Application.IMAGE_SIZE - x - 1;

      for (int y = z; y > 0; y--) {
        builtImage[x][y] = Color.WHITE.getRGB();
      }

      builtImage[x][z] = Color.GRAY.getRGB();

      for (int y = z + 1; y < builtImage.length - 1; y++) {
        builtImage[x][y] = Color.WHITE.getRGB();
      }
    }

    return builtImage;
  }

  private int[][] buildTriangle() {

    int[][] builtImage;
    int z;

    builtImage = new int[Application.IMAGE_SIZE][Application.IMAGE_SIZE];

    for (int x = 0; x < Application.IMAGE_SIZE; x++) {
      z = builtImage.length - x - 1;

      for (int y = z; y >= 0; y--) {
        builtImage[x][y] = Color.WHITE.getRGB();
      }

      for (int y = z; y < Application.IMAGE_SIZE; y++) {
        builtImage[x][y] = Color.BLUE.getRGB();
      }
    }

    return builtImage;
  }

  private int[][] buildFlag() {

    int[][] builtImage;
    int midpoint;

    builtImage = new int[Application.IMAGE_SIZE][Application.IMAGE_SIZE];
    midpoint = Application.IMAGE_SIZE / 2;

    for (int x = 0; x < Application.IMAGE_SIZE; x++) {

      builtImage[x][midpoint] = Color.WHITE.getRGB();

      for (int y = 0; y < Application.IMAGE_SIZE; y++) {

        builtImage[midpoint][y] = Color.WHITE.getRGB();

        for (int z = midpoint - 1; z >= 0 & y < midpoint; z--) {
          builtImage[y][z] = Color.GREEN.getRGB();
        }

        for (int z = midpoint - 1; z >= 0 & y > midpoint; z--) {
          builtImage[y][z] = Color.RED.getRGB();
        }

        for (int z = midpoint + 1; z < Application.IMAGE_SIZE & y < midpoint; z++) {
          builtImage[y][z] = Color.BLUE.getRGB();
        }

        for (int z = midpoint + 1; z < Application.IMAGE_SIZE & y > midpoint; z++) {
          builtImage[y][z] = Color.YELLOW.getRGB();
        }
      }
    }

    return builtImage;
  }

  // TimerListener
  private final class TimerListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      handleTransformation();
    }
  }
}