package team10;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
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
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class mapMaker {
	int mapSize = 15;
	int difficulty = 1;
	int mapDimension = 169;
	int maxWalls = 225; // Total
	String mapName = "Default";
	JButton btnMedium;
	JButton btnSmall;
	JButton btnLarge;
	JPanel panel;
	IconTracker tracker;
	static ImageIcon activeIcon;
	ImageIcon emptyIcon;
	ImageIcon wallIcon;
	ImageIcon goalIcon;
	ImageIcon playerIcon;
	GridLayout mapGridLayout = new GridLayout(13, 13, 1, 1);
	int activeNum = 1;
	boolean hasPlayerIcon = false;
	boolean hasGoalIcon = false;
	ArrayList<JLabel> mapGridList;
	private JFrame frmIceRunnerMap;
	private final ButtonGroup diffGroup = new ButtonGroup();

	//resize the screen for each map size
	public void setBounds() {
		if (this.mapSize == 15) {
			this.panel.removeAll();
			this.mapGridLayout.setColumns(13);
			this.mapGridLayout.setRows(13);
			this.frmIceRunnerMap.setSize(new Dimension(675, 675));
			this.rebuildGrid();
			this.panel.repaint();
		}
		if (this.mapSize == 20) {
			this.panel.removeAll();
			this.mapGridLayout.setColumns(18);
			this.mapGridLayout.setRows(18);
			this.frmIceRunnerMap.setSize(new Dimension(825, 825));
			this.rebuildGrid();
			this.panel.repaint();
		}
		if (this.mapSize == 25) {
			this.panel.removeAll();
			this.mapGridLayout.setColumns(23);
			this.mapGridLayout.setRows(23);
			this.frmIceRunnerMap.setSize(new Dimension(875, 875));
			this.rebuildGrid();
			this.panel.repaint();
		}
	}

	public void setMaxWalls() {
		this.maxWalls = this.mapSize * this.mapSize;
	}

	private void initialize() {
		this.setActiveIcon(this.wallIcon);
		this.frmIceRunnerMap = new JFrame();
		this.frmIceRunnerMap.setVisible(true);
		this.frmIceRunnerMap.setSize(new Dimension(675, 675));
		this.frmIceRunnerMap.getContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.frmIceRunnerMap.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.frmIceRunnerMap.setIconImage(
				Toolkit.getDefaultToolkit().getImage(mapMaker.class.getResource("/res/snowflake-512.png")));
		this.frmIceRunnerMap.setTitle("Ice Runner Map Maker");
		this.frmIceRunnerMap.setResizable(false);
		this.frmIceRunnerMap.getContentPane().setBackground(Color.DARK_GRAY);
		this.frmIceRunnerMap.setDefaultCloseOperation(3);
		this.frmIceRunnerMap.setBackground(Color.DARK_GRAY);
		this.frmIceRunnerMap.getContentPane().setLayout(new BorderLayout(0, 0));
		JToolBar toolBar = new JToolBar();
		toolBar.setFocusTraversalKeysEnabled(false);
		toolBar.setBorder(new EmptyBorder(0, 0, 0, 0));
		toolBar.setOrientation(1);
		toolBar.setFloatable(false);
		toolBar.setToolTipText("Toolbar");
		toolBar.setRollover(true);
		toolBar.setBackground(Color.DARK_GRAY);
		this.frmIceRunnerMap.getContentPane().add((Component) toolBar, "West");
		final JButton btnWall = new JButton("");
		btnWall.setSelected(true);
		btnWall.setToolTipText("Wall");
		btnWall.setSelectedIcon(new ImageIcon(mapMaker.class.getResource("/res/Wall_Selected.png")));
		btnWall.setBackground(Color.BLACK);
		btnWall.setIcon(new ImageIcon(mapMaker.class.getResource("/res/Wall.png")));
		toolBar.add(btnWall);
		final JButton btnPlayer = new JButton("");
		btnPlayer.setToolTipText("Player Start");
		btnPlayer.setBackground(Color.BLACK);
		btnPlayer.setSelectedIcon(new ImageIcon(mapMaker.class.getResource("/res/Player_Selected.png")));
		btnPlayer.setIcon(this.getPlayerIcon());
		toolBar.add(btnPlayer);
		final JButton btnGoal = new JButton("");
		btnGoal.setToolTipText("Player Finish");
		btnGoal.setBackground(Color.BLACK);
		btnGoal.setSelectedIcon(new ImageIcon(mapMaker.class.getResource("/res/Goal_Selected.png")));
		btnGoal.setIcon(this.getGoalIcon());
		btnGoal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		toolBar.add(btnGoal);
		final JButton btnEmpty = new JButton("");
		btnEmpty.setToolTipText("Empty");
		btnEmpty.setSelectedIcon(new ImageIcon(mapMaker.class.getResource("/res/Empty_Selected.png")));
		btnEmpty.setIcon(this.getEmptyIcon());
		btnEmpty.setBackground(Color.BLACK);
		toolBar.add(btnEmpty);
		Component verticalStrut = Box.createVerticalStrut(20);
		toolBar.add(verticalStrut);
		JButton btnClear = new JButton("Clear");
		btnClear.setIconTextGap(10);
		btnClear.setFont(new Font("Dialog", 1, 12));
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mapMaker.this.clearMap();
			}
		});
		toolBar.add(btnClear);
		Component verticalStrut_1 = Box.createVerticalStrut(30);
		toolBar.add(verticalStrut_1);
		JLabel lblSizes = new JLabel("Sizes:");
		lblSizes.setFont(new Font("Dialog", 1, 14));
		lblSizes.setForeground(Color.WHITE);
		toolBar.add(lblSizes);
		this.btnSmall = new JButton("Small");
		toolBar.add(this.btnSmall);
		this.btnSmall.setEnabled(false);
		Component verticalStrut_3 = Box.createVerticalStrut(10);
		toolBar.add(verticalStrut_3);
		this.btnMedium = new JButton("Medium");
		toolBar.add(this.btnMedium);
		Component verticalStrut_4 = Box.createVerticalStrut(10);
		toolBar.add(verticalStrut_4);
		this.btnLarge = new JButton("Large");
		toolBar.add(this.btnLarge);

		Component verticalStrut_2 = Box.createVerticalStrut(10);
		toolBar.add(verticalStrut_2);

		JLabel lblDifficulty = new JLabel("Difficulty");
		lblDifficulty.setForeground(Color.WHITE);
		toolBar.add(lblDifficulty);

		JRadioButton diffButton1 = new JRadioButton("1");
		diffButton1.setSelected(true);
		diffGroup.add(diffButton1);
		diffButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				difficulty = 1;
			}
		});
		toolBar.add(diffButton1);

		JRadioButton diffButton2 = new JRadioButton("2");
		diffGroup.add(diffButton2);
		diffButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				difficulty = 2;
			}
		});
		toolBar.add(diffButton2);

		JRadioButton diffButton3 = new JRadioButton("3");
		diffGroup.add(diffButton3);
		diffButton3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				difficulty = 3;
			}
		});
		toolBar.add(diffButton3);

		JRadioButton diffButton4 = new JRadioButton("4");
		diffGroup.add(diffButton4);
		diffButton4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				difficulty = 4;
			}
		});
		toolBar.add(diffButton4);

		JRadioButton diffButton5 = new JRadioButton("5");
		diffGroup.add(diffButton5);
		diffButton5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				difficulty = 5;
			}
		});
		toolBar.add(diffButton5);
		JMenuBar menuBar = new JMenuBar();
		this.frmIceRunnerMap.getContentPane().add((Component) menuBar, "North");
		menuBar.setBackground(Color.GRAY);
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mapMaker.this.buildLua();
			}
		});
		mnFile.add(mntmSave);
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mapMaker.this.exit();
			}
		});
		mnFile.add(mntmExit);
		this.panel = new JPanel();
		this.panel.setAlignmentY(0.0f);
		this.panel.setAlignmentX(0.0f);
		this.panel.setAutoscrolls(true);
		this.frmIceRunnerMap.getContentPane().add(this.panel);
		this.panel.setCursor(Cursor.getPredefinedCursor(0));
		this.panel.setBorder(new BevelBorder(1, null, null, null, null));
		this.panel.setBackground(Color.BLACK);
		this.panel.setLayout(this.mapGridLayout);
		this.mapGridList = new ArrayList();
		this.btnSmall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mapMaker.this.btnSmall.setEnabled(false);
				mapMaker.this.btnMedium.setEnabled(true);
				mapMaker.this.btnLarge.setEnabled(true);
				mapMaker.this.mapSize = 15;
				mapMaker.this.difficulty = 1;
				mapMaker.this.setBounds();
			}
		});
		this.btnLarge.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mapMaker.this.btnLarge.setEnabled(false);
				mapMaker.this.btnSmall.setEnabled(true);
				mapMaker.this.btnMedium.setEnabled(true);
				mapMaker.this.mapSize = 25;
				mapMaker.this.difficulty = 3;
				mapMaker.this.setEmptyIcon(mapMaker.this.getScaledImage(mapMaker.this.getEmptyIcon(), 25, 25));
				mapMaker.this.setBounds();
			}
		});
		this.btnMedium.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mapMaker.this.btnMedium.setEnabled(false);
				mapMaker.this.btnSmall.setEnabled(true);
				mapMaker.this.btnLarge.setEnabled(true);
				mapMaker.this.mapSize = 20;
				mapMaker.this.difficulty = 2;
				mapMaker.this.setBounds();
			}
		});
		this.tracker = new IconTracker();
		int i = 0;
		while (i < (this.mapSize - 2) * (this.mapSize - 2)) {
			final JLabel label = new JLabel();
			this.mapGridList.add(label);
			label.setIcon(this.getEmptyIcon());
			this.panel.add(label);
			label.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getButton() == 1) {
						if (mapMaker.this.activeNum == 3) {
							mapMaker.this.tracker.getGoal().setIcon(mapMaker.this.emptyIcon);
							label.setIcon(mapMaker.this.goalIcon);
							mapMaker.this.tracker.setGoal(label);
						} else if (mapMaker.this.activeNum == 2) {
							mapMaker.this.tracker.getPlayer().setIcon(mapMaker.this.emptyIcon);
							label.setIcon(mapMaker.this.playerIcon);
							mapMaker.this.tracker.setPlayer(label);
						} else if (mapMaker.this.activeNum == 1) {
							label.setIcon(mapMaker.this.wallIcon);
						} else if (mapMaker.this.activeNum == 4) {
							label.setIcon(mapMaker.this.emptyIcon);
						}
					} else if (arg0.getButton() == 3) {
						label.setIcon(mapMaker.this.emptyIcon);
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
			++i;
		}
		btnWall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnWall.setSelected(true);
				btnEmpty.setSelected(false);
				btnGoal.setSelected(false);
				btnPlayer.setSelected(false);
				mapMaker.this.setActiveIcon(mapMaker.this.wallIcon);
				mapMaker.this.activeNum = 1;
			}
		});
		btnPlayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnWall.setSelected(false);
				btnEmpty.setSelected(false);
				btnGoal.setSelected(false);
				btnPlayer.setSelected(true);
				mapMaker.this.setActiveIcon(mapMaker.this.playerIcon);
				mapMaker.this.activeNum = 2;
			}
		});
		btnGoal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnWall.setSelected(false);
				btnEmpty.setSelected(false);
				btnGoal.setSelected(true);
				btnPlayer.setSelected(false);
				mapMaker.this.setActiveIcon(mapMaker.this.goalIcon);
				mapMaker.this.activeNum = 3;
			}
		});
		btnEmpty.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnWall.setSelected(false);
				btnEmpty.setSelected(true);
				btnGoal.setSelected(false);
				btnPlayer.setSelected(false);
				mapMaker.this.setActiveIcon(mapMaker.this.emptyIcon);
				mapMaker.this.activeNum = 4;
			}
		});
	}

	//getters and setters
	private Icon getGoalIcon() {
		return this.goalIcon;
	}

	private Icon getPlayerIcon() {
		return this.playerIcon;
	}

	public ImageIcon getActiveIcon() {
		return activeIcon;
	}

	public void setActiveIcon(ImageIcon icon) {
		activeIcon = icon;
	}

	public void setEmptyIcon(ImageIcon icon) {
		this.emptyIcon = icon;
	}

	public void setPlayerIcon(ImageIcon icon) {
		this.playerIcon = icon;
	}

	public void setGoalIcon(ImageIcon icon) {
		this.goalIcon = icon;
	}

	public void setWallIcon(ImageIcon icon) {
		this.wallIcon = icon;
	}

	public ImageIcon getEmptyIcon() {
		return this.emptyIcon;
	}
	//end getters and setters
	
	//delete all the tiles on the map
	public void clearMap() {
		int i = 0;
		while (i < this.mapGridList.size()) {
			this.mapGridList.get(i).setIcon(this.emptyIcon);
			++i;
		}
	}

	//Generate the lua code based on the currently painted grid.
	public void buildLua() {
			boolean foundGoal = false;
			boolean foundPlayer = false;
			boolean foundWall = false;
			int m = 0;
			//
			while (!(foundGoal && foundPlayer && foundWall)) {
				if (this.mapGridList.get(m).getIcon().equals(this.goalIcon)) {
					foundGoal = true;
				} else if (this.mapGridList.get(m).getIcon().equals(this.playerIcon)) {
					foundPlayer = true;
				} else if (this.mapGridList.get(m).getIcon().equals(this.wallIcon)) {
					foundWall = true;
				}
				if (++m > (this.mapSize - 2) * (this.mapSize - 2) - 1)
					break;
			}
			if (foundGoal && foundPlayer && foundWall) {
				try {
					JFileChooser chooser = new JFileChooser(
							String.valueOf(System.getProperty("user.home")) + "/Desktop");
					chooser.setFileSelectionMode(2);
					int result = chooser.showSaveDialog(chooser);
					if (result == 0) {
						this.mapName = chooser.getSelectedFile().getName();
						if (this.mapName.contains(".lua")) {
							this.mapName = this.mapName.substring(0, this.mapName.length() - 4);
						}
						JRadioButton temp = new JRadioButton();
						System.out.println(temp.getText());
						this.setMaxWalls();
						String path = chooser.getSelectedFile().getAbsolutePath();
						PrintWriter writer = path.endsWith(".lua") ? new PrintWriter(path, "UTF-8")
								: new PrintWriter(String.valueOf(path) + ".lua", "UTF-8");
						
						//generate the basic file requirements
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
						writer.println("name = \"" + this.mapName.toUpperCase() + "\",");
						writer.println("level = " + this.difficulty + ",");
						writer.println("kit = MapKit({size = " + this.mapSize + ", walls = " + this.maxWalls + " })");
						writer.println("})");
						writer.println("");
						
						//generate the border based on map size
						if (this.mapSize == 15) {
							writer.println("map:add_walls(Wall(0, 0), Right(14))");
							writer.println("map:add_walls(Wall(1, 0), Down(13))");
							writer.println("map:add_walls(Wall(1, 14), Down(13))");
							writer.println("map:add_walls(Wall(14, 1), Right(13))");
						} else if (this.mapSize == 20) {
							writer.println("map:add_walls(Wall(0, 0), Right(19))");
							writer.println("map:add_walls(Wall(1, 0), Down(18))");
							writer.println("map:add_walls(Wall(1, 19), Down(18))");
							writer.println("map:add_walls(Wall(19, 1), Right(18))");
						} else if (this.mapSize == 25) {
							writer.println("map:add_walls(Wall(0, 0), Right(24))");
							writer.println("map:add_walls(Wall(1, 0), Down(23))");
							writer.println("map:add_walls(Wall(1, 24), Down(23))");
							writer.println("map:add_walls(Wall(24, 1), Right(23))");
						}
						// turns the array list(mapgrid) into 2d coordinates
						int z = this.mapSize - 2;
						int i = 1;
						while (i < z * z + 1) {
							int x;
							int y;
							if ((i - 1) % z > 0) {
								x = (i - 1) / z;
								y = (i - 1) % z;
							} else {
								x = (i - 1) / z;
								y = 0;
							}
							// check each tile for the type, if it finds a tile that isn't empty generate the code specific to that type.
							if (this.mapGridList.get(i - 1).getIcon().equals(this.wallIcon)) {
								writer.println("map:add_walls(Wall(" + (x + 1) + "," + (y + 1) + "), Up(0))");
							} else if (this.mapGridList.get(i - 1).getIcon().equals(this.playerIcon)) {
								writer.println("map:set_player(Player(" + (x + 1) + "," + (y + 1) + "))");
							} else if (this.mapGridList.get(i - 1).getIcon().equals(this.goalIcon)) {
								writer.println("map:set_goal(Goal(" + (x + 1) + "," + (y + 1) + "))");
							}
							++i;
						}
						writer.println("");
						writer.println("IceRunner.register_map(map);");
						writer.close();

					}
					JOptionPane.showMessageDialog(this.frmIceRunnerMap, "Map Not Saved!");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this.frmIceRunnerMap, "Error Occured While Saving!");
				}
			} else {
				// display an error if the user hasn't placed the minimum required tiles.
				JOptionPane.showMessageDialog(this.frmIceRunnerMap,
						"Please place at least one wall, start tile, and finish tile...");
			}
		
	}

	public void exit() {
		System.exit(0);
	}

	// Initialize the grid and add listeners to each Jlabel so they will change to the appropriate tile when clicked.
	public void rebuildGrid() {
		this.setMaxWalls();
		this.mapGridList.clear();
		int i = 0;
		while (i < (this.mapSize - 2) * (this.mapSize - 2)) {
			final JLabel label = new JLabel();
			this.mapGridList.add(label);
			label.setIcon(this.getEmptyIcon());
			this.panel.add(label);
			this.panel.revalidate();
			label.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (arg0.getButton() == 1) {
						if (mapMaker.this.activeNum == 3) {
							mapMaker.this.tracker.getGoal().setIcon(mapMaker.this.emptyIcon);
							label.setIcon(mapMaker.this.goalIcon);
							mapMaker.this.tracker.setGoal(label);
						} else if (mapMaker.this.activeNum == 2) {
							mapMaker.this.tracker.getPlayer().setIcon(mapMaker.this.emptyIcon);
							label.setIcon(mapMaker.this.playerIcon);
							mapMaker.this.tracker.setPlayer(label);
						} else if (mapMaker.this.activeNum == 1) {
							label.setIcon(mapMaker.this.wallIcon);
						} else if (mapMaker.this.activeNum == 4) {
							label.setIcon(mapMaker.this.emptyIcon);
						}
					} else if (arg0.getButton() == 3) {
						label.setIcon(mapMaker.this.emptyIcon);
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
			++i;
		}
	}

	private ImageIcon getScaledImage(ImageIcon icon, int w, int h) {
		Image image = icon.getImage();
		BufferedImage resizedImg = new BufferedImage(w, h, 2);
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
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
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

	public class IconTracker {
		JLabel goal;
		JLabel player;

		public IconTracker() {
			this.goal = new JLabel();
			this.player = new JLabel();
		}

		public void setGoal(JLabel label) {
			this.goal = label;
		}

		public void setPlayer(JLabel label) {
			this.player = label;
		}

		public JLabel getGoal() {
			return this.goal;
		}

		public JLabel getPlayer() {
			return this.player;
		}
	}

}
