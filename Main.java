package NoteHtml;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import rtf.AdvancedRTFEditorKit;

public class Main {
	JMenuItem open=new JMenuItem("Open");
	String Directory="C:\\MultimediaNote\\" ;
	private JFrame frame;
	private DefaultListModel noteListModel;
	private DefaultListModel noteBookListModel;
	private JList noteBookList;
	JMenuItem setDirectory;
	 JMenuItem mntmRefresh=null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				        if ("Nimbus".equals(info.getName())) {
				            UIManager.setLookAndFeel(info.getClassName());
				            break;
				        }
				        Main window = new Main();
				        window.frame.setTitle("Multimedia NoteHtml");
					window.frame.setVisible(true);
					
				}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		JScrollPane scrollPane=new JScrollPane();
		
		JSplitPane splitPane = new JSplitPane();
		//splitPane.add(scrollPane);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		noteListModel=new DefaultListModel();
		noteBookListModel=new DefaultListModel();
		Thread runner =new Thread(){
			public void run(){
				
	//			updateListModel();
		//		updateNoteBookList();
				frame.repaint();
				
			}
		};
		

		JList noteList = new JList(noteListModel);
		noteBookList=new JList(noteBookListModel);
		runner.start();
		
		JMenuBar menuBar = new JMenuBar();
		
		
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem newNote = new JMenuItem("New NoteHtml");
		mnFile.add(newNote);
		newNote.addActionListener(new NoteDialog());
		
		JMenuItem newNoteBook = new JMenuItem("New NoteBook");
		mnFile.add(newNoteBook);
		mnFile.add(open);
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Thread runner=new Thread(){
						
						public void run(){
							 openNote();
						}
						
				};
				runner.start();
			    
			}
		});
		newNoteBook.addActionListener(new NoteBookListener());
		updateNoteBookList();
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		mntmExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				exitNote();
			}
		});
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		mntmRefresh = new JMenuItem("Refresh");
		mnTools.add(mntmRefresh);
		setDirectory=new JMenuItem("Set Working Directory");
		mnTools.add(setDirectory);
		
		mntmRefresh.addActionListener(new RefreshListener());
		setDirectory.addActionListener(new SetDirectory());
		scrollPane_1.setViewportView(noteList);
		noteList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getClickCount()==2){
					int index=noteList.getSelectedIndex();
					final String s=noteListModel.getElementAt(index).toString();
					
					Thread runner=new Thread(){
						
						public void run(){
							NoteHtml note=new NoteHtml(s);
							InputStream in = null;
							try {
								in = new FileInputStream(Directory+s+".rtf");
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							note.defaultStyledDocument = new DefaultStyledDocument(new StyleContext());
							try {
								note.rtfKit.read(in, note.defaultStyledDocument, 0);
							} catch (IOException | BadLocationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							note.textPane.setDocument(note.defaultStyledDocument);
							try {
								in.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					};
					runner.start();
				
				}
			}
			
		});
		noteList.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
				//	s=e.getSource().toString();
					//NoteHtml note=new NoteHtml(s);
					
				}
			}
			
		});

		scrollPane_2.setViewportView(noteBookList);
		splitPane.setLeftComponent(scrollPane_2);
		updateListModel();
		updateNoteBookList();
	}
	protected void updateListModel(){
		File f = new File(Directory);
		File[] files =f.listFiles(new TextFileFilter());
		noteListModel.removeAllElements();
		for(File file:files){
			if(file.isFile()){
				String name=file.getName();
				int dot = name.lastIndexOf('.');
				String base = (dot == -1) ? name : name.substring(0, dot);
				noteListModel.addElement(base);
			}
		}
       
	}
	protected void updateNoteBookList(){
		
		String s;

		File f = new File(Directory+File.separator+"notebookList.txt");
		BufferedReader br=null;
		
		try{
			br=new BufferedReader(new FileReader(f));
		}
		catch(IOException e){
			e.printStackTrace();
		}
		try {
			while((s=br.readLine())!=null){
				noteBookListModel.addElement(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	class NoteBookListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			String	noteBookName=JOptionPane.showInputDialog("Enter Name of the NoteHtml Book", "");
			BufferedWriter bw = null;
			
			File f = new File(Directory+File.separator+"notebookList.txt");
			try {
			 bw=new BufferedWriter(new FileWriter(f));
				bw.append(noteBookName.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			finally{
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
	}
	class NoteDialog implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			String	noteName=JOptionPane.showInputDialog("Enter Name of the NoteHtml", "");
			if(noteName!=null){
			createNewNote(noteName);
			}
			updateListModel();
			}
		}
	public void createNewNote(String name){
		
		File file=new File(Directory+File.separator+name+".rtf");
		System.out.println(Directory);
		FileOutputStream out=null;
		try {
			out=new FileOutputStream(file);
			new NoteHtml(name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void openNote(){
		 NoteHtml note =new NoteHtml("");
			JFileChooser chooser = new JFileChooser();
		      if (chooser.showOpenDialog(frame) !=
		          JFileChooser.APPROVE_OPTION)
		        return;
		      File file = chooser.getSelectedFile();
		      if (file == null)
		        return;

		      FileReader reader = null;
		      try {
		    	  
		        reader = new FileReader(file);
		       note.textPane.read(reader, null);
		      }
		      catch (IOException ex) {
		        JOptionPane.showMessageDialog(note,
		        "File Not Found", "ERROR", JOptionPane.ERROR_MESSAGE);
		      }
		      finally {
		        if (reader != null) {
		          try {
		            reader.close();
		          } catch (IOException x) {}
		        }
		      }
		      note.setTitle(file.getName());
	}
	public void exitNote(){
		WindowListener wndCloser = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
	}
		
	
	
	class TextFileFilter implements FileFilter {

	    public boolean accept(File file) {
	        // implement the logic to select files here..
	        String name = file.getName().toLowerCase();
	        
	        return name.endsWith(".rtf") ;
	        
	    }
	}
	class RefreshListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			updateListModel();
			updateNoteBookList();
		}
		
	}
	class SetDirectory implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			/*JFileChooser fileChooser=new JFileChooser();
			if (fileChooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
				return;
			Directory = fileChooser.getSelectedFile();
			System.out.print(Directory.getAbsolutePath());
			if(!Directory.isDirectory()){
				JOptionPane.showMessageDialog(frame, "Please Select a Directory","Error", JOptionPane.ERROR_MESSAGE, null);
			}
		}*/}
		
	}
}
	


