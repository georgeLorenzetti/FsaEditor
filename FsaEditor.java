import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class FsaEditor {

    private JFrame window = new JFrame("Xtreme Fsa");

    public static int boundX = 0;
    public static int boundY = 5;
    public static int width = 1000;
    public static int height = 450;

    private FsaPanel fsaPanel = new FsaPanel(boundX, boundY, width, height, this);

    private JMenuBar mainMenu = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenuItem open = new JMenuItem("Open");
    private JMenuItem saveAs = new JMenuItem("Save As");
    private JMenuItem quit = new JMenuItem("Quit");

    private JMenu editMenu = new JMenu("Edit");
    private JMenuItem newState = new JMenuItem("New State");
    private JMenuItem newTransition = new JMenuItem("New Transition");
    private JMenuItem setInitial = new JMenuItem("Set Initial");
    private JMenuItem unsetInitial = new JMenuItem("Unset Initial");
    private JMenuItem setFinal = new JMenuItem("Set Final");
    private JMenuItem unsetFinal = new JMenuItem("Unset Final");
    private JMenuItem delete = new JMenuItem("Delete");
    private JButton resetButton = new JButton("Reset");
    private JButton stepButton = new JButton("Step");
    private JLabel eventInputLabel = new JLabel("Next event name:");
    private JTextField eventInput = new JTextField();
    private JComponent sequenceRecognised = new JComponent(){
        @Override
        public void paintComponent(Graphics g){
            //super.paintComponent(g);
            if(fsaPanel.getFsa().isRecognised()){
                g.drawString("Sequence Recognised", 10, 30);
            }else{
                g.drawString("Sequence Not Recognised", 10, 30);
            }
        }
    };

    private boolean fsaLoaded = false;

    public FsaEditor(){
        window.setLayout(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ActionListener actionListener = new ActionListener(){
            public void actionPerformed(ActionEvent e) { 
                if(e.getSource() == open){
                    openFile();
                }else if(e.getSource() == saveAs){
                    saveFile();
                }else if(e.getSource() == quit){
                    System.exit(0);
                }else if(e.getSource() == newState){
                    newState();
                }else if(e.getSource() == newTransition){
                    newTransition();
                }else if(e.getSource() == setInitial){
                    fsaPanel.setSelectedInitial();
                }else if(e.getSource() == unsetInitial){
                    fsaPanel.unsetSelectedInitial();
                }else if(e.getSource() == setFinal){
                    fsaPanel.setSelectedFinal();
                }else if(e.getSource() == unsetFinal){
                    fsaPanel.unsetSelectedFinal();
                }else if(e.getSource() == delete){
                    fsaPanel.deleteSelected();
                }else if(e.getSource() == resetButton){
                    fsaPanel.getFsa().reset();
                }else if(e.getSource() == stepButton){
                    stepFsa();
                }
                //System.out.println(fsaPanel.getFsa().isRecognised());
                sequenceRecognised.repaint();
            } 
        };

        KeyListener keyListener = new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getSource() == eventInput && e.getKeyCode() == KeyEvent.VK_ENTER){
                    stepFsa();
                }
            }

            @Override
            public void keyReleased(KeyEvent e){   
            }
            @Override
            public void keyTyped(KeyEvent e){   
            }
        };

        //MENUS

        //FILE
        open.addActionListener(actionListener);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        fileMenu.add(open);
        saveAs.addActionListener(actionListener);
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        fileMenu.add(saveAs);
        quit.addActionListener(actionListener);
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        fileMenu.add(quit);
        mainMenu.add(fileMenu);

        //EDIT
        newState.addActionListener(actionListener);
        editMenu.add(newState);
        newTransition.addActionListener(actionListener);
        editMenu.add(newTransition);
        setInitial.addActionListener(actionListener);
        editMenu.add(setInitial);
        unsetInitial.addActionListener(actionListener);
        editMenu.add(unsetInitial);
        setFinal.addActionListener(actionListener);
        editMenu.add(setFinal);
        unsetFinal.addActionListener(actionListener);
        editMenu.add(unsetFinal);
        delete.addActionListener(actionListener);
        editMenu.add(delete);
        mainMenu.add(editMenu);

        //control panel
        resetButton.setBounds(335, 470, 100, 70);
        resetButton.addActionListener(actionListener);
        window.add(resetButton);

        stepButton.setBounds(570, 470, 100, 70);
        stepButton.addActionListener(actionListener);
        window.add(stepButton);

  
        eventInputLabel.setBounds(445, 470, 120, 40);
        window.add(eventInputLabel);

        eventInput.setBounds(450, 500, 100, 40);
        eventInput.addKeyListener(keyListener);
        window.add(eventInput);

        sequenceRecognised.setBounds(800, 480, 300, 70);
        window.add(sequenceRecognised);
        //ADD MENU
        window.setJMenuBar(mainMenu);

        //ADD MAIN PANEL
        window.add(fsaPanel);

        window.setSize(1000, 600);
        window.setVisible(true);
    }

    public void openFile(){
        File file = new File(".");
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(file);
        fc.setFileFilter(new FileNameExtensionFilter("Fsa Files", "fsa"));
        int res = fc.showOpenDialog(window);
        FsaPanel old = fsaPanel;
        if(res == 0){
            try{
                window.remove(fsaPanel);
                fsaPanel = new FsaPanel(boundX, boundY, width, height, this);
                fsaPanel.getFsa().addListener(fsaPanel); 
                window.add(fsaPanel);
                Reader r = new FileReader(fc.getSelectedFile());
                FsaReaderWriter fsaRW = new FsaReaderWriter();
                fsaRW.read(r, fsaPanel.getFsa());
                r.close();
                fsaLoaded = true;
                window.setTitle("Xtreme Fsa - " + fc.getSelectedFile().getName());
            }catch(FileNotFoundException d){
                window.remove(fsaPanel);
                fsaPanel = old;
                window.add(fsaPanel);          
                JOptionPane.showMessageDialog(window, "File Not Found", "File Error", JOptionPane.ERROR_MESSAGE);
            }catch(FsaFormatException e){
                window.remove(fsaPanel);
                fsaPanel = old;
                window.add(fsaPanel);
                JOptionPane.showMessageDialog(window, clipErrorMsg(e.toString()), "File Format Error", JOptionPane.ERROR_MESSAGE);
            }catch(IOException e){
                window.remove(fsaPanel);
                fsaPanel = old;
                window.add(fsaPanel);
                JOptionPane.showMessageDialog(window, clipErrorMsg(e.toString()), "IOException", JOptionPane.ERROR_MESSAGE);
            }catch(IllegalArgumentException e){
                window.remove(fsaPanel);
                fsaPanel = old;
                window.add(fsaPanel);
                JOptionPane.showMessageDialog(window, clipErrorMsg(e.toString()), "Illegal Arguments Passed to Fsa", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void openFile(String fileName){
        FsaPanel old = fsaPanel;
        try{
            window.remove(fsaPanel);
            fsaPanel = new FsaPanel(boundX, boundY, width, height, this);
            fsaPanel.getFsa().addListener(fsaPanel); 
            window.add(fsaPanel);
            Reader r = new FileReader(fileName);
            FsaReaderWriter fsaRW = new FsaReaderWriter();
            fsaRW.read(r, fsaPanel.getFsa());
            r.close();
            fsaLoaded = true;
            window.setTitle("Xtreme Fsa - " + fileName);
        }catch(FileNotFoundException d){
            window.remove(fsaPanel);
            fsaPanel = old;
            window.add(fsaPanel);          
            JOptionPane.showMessageDialog(window, "File Not Found", "File Error", JOptionPane.ERROR_MESSAGE);
        }catch(FsaFormatException e){
            window.remove(fsaPanel);
            fsaPanel = old;
            window.add(fsaPanel);
            JOptionPane.showMessageDialog(window, clipErrorMsg(e.toString()), "File Format Error", JOptionPane.ERROR_MESSAGE);
        }catch(IOException e){
            window.remove(fsaPanel);
            fsaPanel = old;
            window.add(fsaPanel);
            JOptionPane.showMessageDialog(window, clipErrorMsg(e.toString()), "IOException", JOptionPane.ERROR_MESSAGE);
        }catch(IllegalArgumentException e){
            window.remove(fsaPanel);
            fsaPanel = old;
            window.add(fsaPanel);
            JOptionPane.showMessageDialog(window, clipErrorMsg(e.toString()), "Illegal Arguments Passed to Fsa", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveFile(){
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setFileFilter(new FileNameExtensionFilter("Fsa Files", "fsa"));
        int res = fc.showSaveDialog(null);

        //System.out.println(res);

        if(res == 0){
            try{
                String s = fc.getSelectedFile().getName();
                if(s.length() > 3){
                    if(!s.substring(s.length()-4).equals(".fsa")){
                        s += ".fsa";
                    }
                }
                Writer w = new FileWriter(s);
                window.setTitle("Xtreme Fsa - " + s);
                FsaReaderWriter fsaRW = new FsaReaderWriter();
                fsaRW.write(w, fsaPanel.getFsa());
                w.close();
            }catch(IOException e){
                JOptionPane.showMessageDialog(window, "Could not write to file", "File Error", JOptionPane.ERROR_MESSAGE);                
            }
        }
    }

    public void newState(){
        String j = (String)JOptionPane.showInputDialog("Please enter the name of the new state");

        if(j != null){
            if(j.length() > 0){
                Point p1 = MouseInfo.getPointerInfo().getLocation();
                Point p2 = window.getLocationOnScreen();
                Point p3 = new Point((int)(p1.getX() - p2.getX()), (int)(p1.getY() - p2.getY()));
                //System.out.println(p3.getX() + " - " + p3.getY());
                try{
                    fsaPanel.addState(j, p3);
                }catch(IllegalArgumentException e){
                    JOptionPane.showMessageDialog(window, clipErrorMsg(e.toString()), "Error Creating State", JOptionPane.ERROR_MESSAGE);
                }

            }
        }
    }

    public void newTransition(){
        String j = (String)JOptionPane.showInputDialog("Please enter the name of the new transition");
        if(j != null){
            if(j.length() > 0){
                try{
                    fsaPanel.addTransition(j);
                }catch(IllegalArgumentException e){
                    JOptionPane.showMessageDialog(window, clipErrorMsg(e.toString()), "Error Creating Transition", JOptionPane.ERROR_MESSAGE);
                }

            }
        }
    }

    public void repaintIsRecognised(){
        sequenceRecognised.repaint();
    }

    public void stepFsa(){
        fsaPanel.getFsa().step(eventInput.getText());
        sequenceRecognised.repaint();
        eventInput.setText("");
        //System.out.println(fsaPanel.getFsa().toString());
    }

    public FsaPanel getFsaPanel(){
        return fsaPanel;
    }

    public void moveUp(){
        Set<State> st = fsaPanel.getFsa().getStates();
        for(Iterator<State> it = st.iterator(); it.hasNext(); ){
            State s = it.next();
            s.moveBy(10, 10);
        }

        FsaImpl fs = fsaPanel.getFsa();
        fs.newState("WOW", 0, 0);
    }

    public String clipErrorMsg(String e){
        return e.substring(e.indexOf("Exception:")+10);
    }

    public static void main(String[] args){
        FsaEditor gui = new FsaEditor();

        //gui.openFile("sim.fsa");

        //gui.getFsaPanel().add(new TransitionIcon());
        //gui.getFsaPanel().add(new StateIcon(new StateImpl("win", 0, 0), 0, 0));
        //gui.getFsaPanel().repaint();
    }
    
}