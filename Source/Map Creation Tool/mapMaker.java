import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

public class mapMaker {

	int mapSize = 15; // Default Map Size (Small is 15, Medium is 20, and Large
						// is 25)
	int difficulty = 1; // Basically changes the color of the map, currently
						// color is not implemented in the map maker. This
						// number will be 1 for small maps, 2 for medium and 3
						// for large.
	int mapDimension = 169; // Number of tiles in the editable map area.
	int maxWalls = 225; // Number of tiles including the border of the map

	String mapName = "Default"; // The file name and

	// map size buttons
	JButton btnMedium;
	JButton btnSmall;
	JButton btnLarge;

	// map editing area
	JPanel panel;

	// keep track of icon props
	IconTracker tracker;

	static ImageIcon activeIcon; // allows us to check which icon is currently
									// selected
	ImageIcon emptyIcon; // icon that represents empty space on the map
	ImageIcon wallIcon; // icon that represents our ice blocks in game
	ImageIcon goalIcon; // icon that represents the finishing tile in game
	ImageIcon playerIcon; // icon that represents the starting point in game

	GridLayout mapGridLayout = new GridLayout(13, 13, 1, 1);
	int activeNum = 1;
	boolean hasPlayerIcon = false; // Check to see if there is already a
									// starting tile on the map
	boolean hasGoalIcon = false; // Check to see if there is already a goal tile

	ArrayList<JLabel> mapGridList; // Keep Track of the tile properties with an
									// array list. This will allow us to check
									// where in the grid to put walls.

	// window frame
	private JFrame frmIceRunnerMap;

	/*
	 * Keep track of the goal and player icons. We don't want to allow someone
	 * to add more than one goal or player to the map builder.
	 */
	public class IconTracker {
		JLabel goal = new JLabel();
		JLabel player = new JLabel();

		public IconTracker() {
		}

		public void setGoal(JLabel label) {
			goal = label;
		}

		public void setPlayer(JLabel label) {
			player = label;
		}

		public JLabel getGoal() {
			return goal;
		}

		public JLabel getPlayer() {
			return player;
		}
	}

	public void setBounds() {
		// Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

		if (mapSize == 15) {
			panel.removeAll();
			mapGridLayout.setColumns(13);
			mapGridLayout.setRows(13);
			frmIceRunnerMap.setSize(new Dimension(630, 600));
			rebuildGrid();
			panel.repaint();
		}
		if (mapSize == 20) {
			panel.removeAll();
			mapGridLayout.setColumns(18);
			mapGridLayout.setRows(18);
			frmIceRunnerMap.setSize(new Dimension(720, 700));
			rebuildGrid();
			panel.repaint();
		}
		if (mapSize == 25) {
			panel.removeAll();
			mapGridLayout.setColumns(23);
			mapGridLayout.setRows(23);
			frmIceRunnerMap.setSize(new Dimension(820, 800));
			rebuildGrid();
			panel.repaint();
		}
	}

	// set the maximum number of editable tiles

	// set the
	// mgetContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	// frmIceRunnerMap.aximum number of tiles allowed in the map including the
	// border
	public void setMaxWalls() {
		maxWalls = (mapSize * mapSize);
	}

	// Default Constructor
	public mapMaker() {

	}

	// Build the window and set up all the action listeners
	private void initialize() {
		setActiveIcon(wallIcon);
		frmIceRunnerMap = new JFrame();
		frmIceRunnerMap.setVisible(true);
		frmIceRunnerMap.setSize(new Dimension(630, 600));
		frmIceRunnerMap.getContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		frmIceRunnerMap.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		frmIceRunnerMap.setIconImage(
				Toolkit.getDefaultToolkit().getImage(mapMaker.class.getResource("/res/snowflake-512.png")));
		frmIceRunnerMap.setTitle("Ice Runner Map Maker");
		frmIceRunnerMap.setResizable(false);
		frmIceRunnerMap.getContentPane().setBackground(Color.DARK_GRAY);
		frmIceRunnerMap.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frmIceRunnerMap.setBackground(Color.DARK_GRAY);
		frmIceRunnerMap.getContentPane().setLayout(new BorderLayout(0, 0));
		JToolBar toolBar = new JToolBar();
		toolBar.setFocusTraversalKeysEnabled(false);
		toolBar.setBorder(new EmptyBorder(0, 0, 0, 0));
		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBar.setFloatable(false);
		toolBar.setToolTipText("Toolbar");
		toolBar.setRollover(true);
		toolBar.setBackground(Color.DARK_GRAY);
		frmIceRunnerMap.getContentPane().add(toolBar, BorderLayout.WEST);

		JButton btnWall = new JButton("");
		btnWall.setSelected(true);
		btnWall.setToolTipText("Wall");

		btnWall.setSelectedIcon(new ImageIcon(mapMaker.class.getResource("/res/Wall_Selected.png")));
		btnWall.setBackground(Color.BLACK);
		btnWall.setIcon(new ImageIcon(mapMaker.class.getResource("/res/Wall.png")));
		toolBar.add(btnWall);

		JButton btnPlayer = new JButton("");
		btnPlayer.setToolTipText("Player Start");
		btnPlayer.setBackground(Color.BLACK);
		btnPlayer.setSelectedIcon(new ImageIcon(mapMaker.class.getResource("/res/Player_Selected.png")));
		btnPlayer.setIcon(getPlayerIcon());
		toolBar.add(btnPlayer);

		JButton btnGoal = new JButton("");
		btnGoal.setToolTipText("Player Finish");
		btnGoal.setBackground(Color.BLACK);
		btnGoal.setSelectedIcon(new ImageIcon(mapMaker.class.getResource("/res/Goal_Selected.png")));
		btnGoal.setIcon(getGoalIcon());
		btnGoal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		toolBar.add(btnGoal);

		JButton btnEmpty = new JButton("");
		btnEmpty.setToolTipText("Empty");
		btnEmpty.setSelectedIcon(new ImageIcon(mapMaker.class.getResource("/res/Empty_Selected.png")));
		btnEmpty.setIcon(getEmptyIcon());
		btnEmpty.setBackground(Color.BLACK);
		toolBar.add(btnEmpty);

		Component verticalStrut = Box.createVerticalStrut(20);
		toolBar.add(verticalStrut);

		JButton btnClear = new JButton("Clear");
		btnClear.setIconTextGap(10);
		btnClear.setFont(new Font("Dialog", Font.BOLD, 12));
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearMap();
			}
		});
		toolBar.add(btnClear);

		Component verticalStrut_1 = Box.createVerticalStrut(30);
		toolBar.add(verticalStrut_1);

		JLabel lblSizes = new JLabel("Sizes:");
		lblSizes.setFont(new Font("Dialog", Font.BOLD, 14));
		lblSizes.setForeground(Color.WHITE);
		toolBar.add(lblSizes);

		btnSmall = new JButton("Small");

		toolBar.add(btnSmall);
		btnSmall.setEnabled(false);
		Component verticalStrut_3 = Box.createVerticalStrut(10);
		toolBar.add(verticalStrut_3);

		btnMedium = new JButton("Medium");

		toolBar.add(btnMedium);

		Component verticalStrut_4 = Box.createVerticalStrut(10);
		toolBar.add(verticalStrut_4);

		btnLarge = new JButton("Large");

		toolBar.add(btnLarge);

		JMenuBar menuBar = new JMenuBar();
		frmIceRunnerMap.getContentPane().add(menuBar, BorderLayout.NORTH);
		menuBar.setBackground(Color.GRAY);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buildLua();
			}
		});
		mnFile.add(mntmSave);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		mnFile.add(mntmExit);

		panel = new JPanel();
		panel.setAlignmentY(0.0f);
		panel.setAlignmentX(0.0f);
		panel.setAutoscrolls(true);
		frmIceRunnerMap.getContentPane().add(panel);
		panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBackground(Color.BLACK);
		panel.setLayout(mapGridLayout);
		mapGridList = new ArrayList<JLabel>();

		btnSmall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSmall.setEnabled(false);
				btnMedium.setEnabled(true);
				btnLarge.setEnabled(true);
				mapSize = 15;
				difficulty = 1;
				setBounds();
			}
		});

		btnLarge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLarge.setEnabled(false);
				btnSmall.setEnabled(true);
				btnMedium.setEnabled(true);
				mapSize = 25;
				difficulty = 3;
				setEmptyIcon(getScaledImage(getEmptyIcon(), 25, 25));
				setBounds();
			}
		});

		btnMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnMedium.setEnabled(false);
				btnSmall.setEnabled(true);
				btnLarge.setEnabled(true);
				mapSize = 20;
				difficulty = 2;
				setBounds();
			}
		});

		tracker = new IconTracker();

		// Build the map grid
		for (int i = 0; i < ((mapSize - 2) * (mapSize - 2)); i++) {
			JLabel label = new JLabel();
			mapGridList.add(label);
			label.setIcon(getEmptyIcon());
			panel.add(label);
			label.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getButton() == MouseEvent.BUTTON1) {
						if (activeNum == 3) { // check to see if a goal has
												// already been added to the
												// map, if it has delete the old
												// one and place the new one.
							tracker.getGoal().setIcon(emptyIcon); // delete the
																	// old
																	// finish
																	// tile
							label.setIcon(goalIcon);
							tracker.setGoal(label);
						} else if (activeNum == 2) {
							tracker.getPlayer().setIcon(emptyIcon); // delete
																	// the old
																	// player
																	// start
																	// tile
							label.setIcon(playerIcon);
							tracker.setPlayer(label);
						} else if (activeNum == 1) {
							label.setIcon(wallIcon);
						} else if (activeNum == 4) {
							label.setIcon(emptyIcon);
						}

					}
					// allow right clicking to remove any walls or icons
					else if (arg0.getButton() == MouseEvent.BUTTON3) {
						label.setIcon(emptyIcon);
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {

				}

				@Override
				public void mouseExited(MouseEvent arg0) {

				}

				@Override
				public void mousePressed(MouseEvent arg0) {

				}

				@Override
				public void mouseReleased(MouseEvent arg0) {

				}

			});
		}

		btnWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnWall.setSelected(true);
				btnEmpty.setSelected(false);
				btnGoal.setSelected(false);
				btnPlayer.setSelected(false);
				setActiveIcon(wallIcon);
				activeNum = 1;
			}
		});

		btnPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnWall.setSelected(false);
				btnEmpty.setSelected(false);
				btnGoal.setSelected(false);
				btnPlayer.setSelected(true);
				setActiveIcon(playerIcon);
				activeNum = 2;
			}
		});

		btnGoal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnWall.setSelected(false);
				btnEmpty.setSelected(false);
				btnGoal.setSelected(true);
				btnPlayer.setSelected(false);
				setActiveIcon(goalIcon);
				activeNum = 3;
			}
		});
		btnEmpty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnWall.setSelected(false);
				btnEmpty.setSelected(true);
				btnGoal.setSelected(false);
				btnPlayer.setSelected(false);
				setActiveIcon(emptyIcon);
				activeNum = 4;
			}
		});

	}

	private Icon getGoalIcon() {
		return goalIcon;
	}

	private Icon getPlayerIcon() {
		return playerIcon;
	}

	private Icon getWallIcon() {
		return wallIcon;
	}

	public ImageIcon getActiveIcon() {

		return activeIcon;
	}

	// Set the currently selected icon (Wall, Goal, Empty, or Player) in the
	// tool bar.
	public void setActiveIcon(ImageIcon icon) {
		activeIcon = icon;
	}

	public void setEmptyIcon(ImageIcon icon) {
		emptyIcon = icon;
	}

	public void setPlayerIcon(ImageIcon icon) {
		playerIcon = icon;
	}

	public void setGoalIcon(ImageIcon icon) {
		goalIcon = icon;
	}

	public void setWallIcon(ImageIcon icon) {
		wallIcon = icon;
	}

	public ImageIcon getEmptyIcon() {
		return emptyIcon;
	}

	// Delete all the tiles and start over
	public void clearMap() {
		for (int i = 0; i < mapGridList.size(); i++) {
			mapGridList.get(i).setIcon(emptyIcon);
		}

	}

	// Generate the map code and save it to a lua file.
	public void buildLua() {
		boolean foundGoal = false;
		boolean foundPlayer = false;
		boolean foundWall = false;

		int m = 0;
		// check to make sure there is at least the minimum
		while (foundGoal == false || foundPlayer == false || foundWall == false) {

			if (mapGridList.get(m).getIcon().equals(goalIcon)) {
				foundGoal = true;
			} else if (mapGridList.get(m).getIcon().equals(playerIcon)) {
				foundPlayer = true;
			} else if (mapGridList.get(m).getIcon().equals(wallIcon)) {
				foundWall = true;
			}

			m++;
			// Exit the loop when you run out of grid spaces to check.
			if (m > (((mapSize - 2) * (mapSize - 2)) + 1)) {
				break;
			}
		}

		if (foundGoal && foundPlayer && foundWall) {
			try {
				JFileChooser chooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");

				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int result = chooser.showSaveDialog(chooser);
				if (result == JFileChooser.APPROVE_OPTION) {
					mapName = chooser.getSelectedFile().getName();
					if (mapName.contains(".lua")) {
						mapName = mapName.substring(0, mapName.length() - 4);
					}
					setMaxWalls();
					String path = chooser.getSelectedFile().getAbsolutePath();
					PrintWriter writer;
					if (path.endsWith(".lua")) {
						writer = new PrintWriter(path, "UTF-8");
					} else
						writer = new PrintWriter(path + ".lua", "UTF-8");

					writer.println("local Map = IceRunner.Map");
					writer.println("local MapKit = IceRunner.MapKit");
					writer.println("local Up = IceRunner.MapTools.UpExtent");
					writer.println("local Down = IceRunner.MapTools.DownExtent");
					writer.println("local Left = IceRunner.MapTools.LeftExtent");
					writer.println("local Right = IceRunner.MapTools.RightExtent");
					writer.println("local Wall = IceRunner.Map.Wall");
					writer.println("local MapKit = IceRunner.MapTools.MapKit");
					writer.println("local Player = Map.Player");
					writer.println("local Goal = Map.Goal");
					writer.println("");
					writer.println("local map = Map({");
					writer.println("name = \"" + mapName.toUpperCase() + "\",");
					writer.println("level = " + difficulty + ",");
					writer.println("kit = MapKit({size = " + mapSize + ", walls = " + maxWalls + " })");
					writer.println("})");
					writer.println("");

					// create the appropriate surrounding wall in lua
					if (mapSize == 15) {
						writer.println("map:add_walls(Wall(0, 0), Right(14))");
						writer.println("map:add_walls(Wall(1, 0), Down(13))");
						writer.println("map:add_walls(Wall(1, 14), Down(13))");
						writer.println("map:add_walls(Wall(14, 1), Right(13))");
					} else

					if (mapSize == 20) {
						writer.println("map:add_walls(Wall(0, 0), Right(19))");
						writer.println("map:add_walls(Wall(1, 0), Down(18))");
						writer.println("map:add_walls(Wall(1, 19), Down(18))");
						writer.println("map:add_walls(Wall(19, 1), Right(18))");
					} else

					if (mapSize == 25) {
						writer.println("map:add_walls(Wall(0, 0), Right(24))");
						writer.println("map:add_walls(Wall(1, 0), Down(23))");
						writer.println("map:add_walls(Wall(1, 24), Down(23))");
						writer.println("map:add_walls(Wall(24, 1), Right(23))");
					}

					int x;
					int y;
					int z = mapSize - 2; // editable region of the map will be 2
											// less than the actual maps size.
					for (int i = 1; i < ((z * z) + 1); i++) {
						if (((i - 1) % z) > 0) {
							x = (i - 1) / z;
							y = (i - 1) % z;
						} else {
							x = (i - 1) / z;
							y = 0;
						}
						if (mapGridList.get(i - 1).getIcon().equals(wallIcon)) {
							writer.println("map:add_walls(Wall(" + (x + 1) + "," + (y + 1) + "), Up(0))");
						} else if (mapGridList.get(i - 1).getIcon().equals(playerIcon)) {
							writer.println("map:set_player(Player(" + (x + 1) + "," + (y + 1) + "))");

						} else if (mapGridList.get(i - 1).getIcon().equals(goalIcon)) {
							writer.println("map:set_goal(Goal(" + (x + 1) + "," + (y + 1) + "))");
						}

					}
					writer.println("");
					writer.println("IceRunner.register_map(map);");
					writer.close();
				} else {
					JOptionPane.showMessageDialog(frmIceRunnerMap, "Map Not Saved!");
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frmIceRunnerMap, "Error Occured While Saving!");
			}
		} else {
			JOptionPane.showMessageDialog(frmIceRunnerMap,
					"Please place at least one wall, start tile, and finish tile...");
		}
	}

	// close the program
	public void exit() {
		System.exit(0);
	}

	public void rebuildGrid() {
		// Build the map grid
		setMaxWalls();
		mapGridList.clear();
		for (int i = 0; i < ((mapSize - 2) * (mapSize - 2)); i++) {
			JLabel label = new JLabel();
			mapGridList.add(label);
			label.setIcon(getEmptyIcon());
			panel.add(label);
			panel.revalidate();
			label.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getButton() == MouseEvent.BUTTON1) {
						if (activeNum == 3) { // check to see if a goal has
												// already been added to the
												// map, if it has delete the old
												// one and place the new one.
							tracker.getGoal().setIcon(emptyIcon); // delete the
																	// old
																	// finish
																	// tile
							label.setIcon(goalIcon);
							tracker.setGoal(label);
						} else if (activeNum == 2) {
							tracker.getPlayer().setIcon(emptyIcon); // delete
																	// the old
																	// player
																	// start
																	// tile
							label.setIcon(playerIcon);
							tracker.setPlayer(label);
						} else if (activeNum == 1) {
							label.setIcon(wallIcon);
						} else if (activeNum == 4) {
							label.setIcon(emptyIcon);
						}

					}
					// allow right clicking to remove any walls or icons
					else if (arg0.getButton() == MouseEvent.BUTTON3) {
						label.setIcon(emptyIcon);

					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {

				}

				@Override
				public void mouseExited(MouseEvent arg0) {

				}

				@Override
				public void mousePressed(MouseEvent arg0) {

				}

				@Override
				public void mouseReleased(MouseEvent arg0) {

				}

			});
		}

	}

	private ImageIcon getScaledImage(ImageIcon icon, int w, int h) {
		Image image = icon.getImage();
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(image, 0, 0, w, h, null);
		g2.dispose();
		return icon;
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mapMaker mm = new mapMaker();
					mm.setEmptyIcon(new ImageIcon(mapMaker.class.getResource("/res/Empty.png")));
					mm.setPlayerIcon(new ImageIcon(mapMaker.class.getResource("/res/Player.png")));
					mm.setGoalIcon(new ImageIcon(mapMaker.class.getResource("/res/Goal.png")));
					mm.setWallIcon(new ImageIcon(mapMaker.class.getResource("/res/Wall.png")));
					mm.initialize();
					mm.frmIceRunnerMap.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
