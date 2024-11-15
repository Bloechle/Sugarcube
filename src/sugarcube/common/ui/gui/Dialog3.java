package sugarcube.common.ui.gui;

import sugarcube.common.data.Zen;
import sugarcube.common.interfaces.Closable;
import sugarcube.common.system.io.File3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class Dialog3 extends JDialog implements Closable
{  
  private Component parent;
  private Panel3 panel = new Panel3();
  private CloseAdapter adapter = new CloseAdapter();

  public Dialog3(Component parent, String title, boolean isDialogModal, JComponent... components)
  {
    this(parent, title, (ImageIcon) null);
    this.setModalityType(isDialogModal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
    this.panel.addCenter(components);
    this.addWindowListener(adapter);
  }

  public Dialog3(Component parent, String title, ImageIcon icon)
  {
    super(getParentWindow(parent), title);
    this.parent = parent;
    this.setIconImage(icon == null ? Zen.S3_ICON.getImage() : icon.getImage());
    super.getContentPane().add(panel, BorderLayout.CENTER);
    this.setMinimumSize(new Dimension(200, 30));
    this.addWindowListener(adapter);
  }

  @Override
  public Panel3 getContentPane()
  {
    return this.panel;
  }

  public void disableCloseButton()
  {
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
  }

  public void enableCloseButton()
  {
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public Panel3 panel()
  {
    return this.panel;
  }

  public void display()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {    
        pack();        
        setLocationRelativeTo(parent);           
        setVisible(true);
      }
    });
  }

  @Override
  public void close()
  {
    this.setVisible(false);
    this.dispose();
  }

  public void closed()
  {
  }

  public static Window getParentWindow(Component c)
  {
    if (c == null)
      return null;
    while (!(c instanceof Window))
      c = c.getParent();
    return c instanceof Window ? (Window) c : null;
  }

  public static void showComponentDialog(Component parent, String title, boolean isDialogModal, Component content)
  {
    Dialog3 dialog = new Dialog3(parent, title, isDialogModal);
    dialog.panel.addCenter(content);
    dialog.display();
  }

  public static Object showInputDialog(Component c, Icon icon, String title, String message, Object selected, Object... values)
  {
    if (values.length == 0)
      values = null;
    return JOptionPane.showInputDialog(c, message, title, JOptionPane.PLAIN_MESSAGE, icon, values, selected);
  }

  public static String showStringDialog(Component c, Icon icon, String title, String message, String selected, String... values)
  {
    if (values.length == 0)
      values = null;
    return (String) JOptionPane.showInputDialog(c, message, title, JOptionPane.PLAIN_MESSAGE, icon, values, selected);
  }

  public static boolean showConfirmDialog(Component c, Icon icon, String title, String message)
  {
    return JOptionPane.showConfirmDialog(c, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon) == JOptionPane.YES_OPTION;
  }

  public static void showMessageDialog(Component c, Icon icon, String title, String message)
  {
    JOptionPane.showMessageDialog(c, message, title, JOptionPane.INFORMATION_MESSAGE, icon);
  }
  
  public static void showErrorDialog(Component c, Icon icon, String title, String message)
  {
    JOptionPane.showMessageDialog(c, message, title, JOptionPane.ERROR_MESSAGE, icon);
  }  

  public static File3 showFileOpenDialog(Component c, File directory, String... extensions)
  {
    FileChooser3 fileChooser = (directory == null ? new FileChooser3().filter(extensions) : new FileChooser3(directory).filter(extensions));
    return (fileChooser.showOpenDialog(c) == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
  }

  public static File3 showFileSaveDialog(Component c, File directory, String... extensions)
  {
    FileChooser3 fileChooser = (directory == null ? new FileChooser3().filter(extensions) : new FileChooser3(directory).filter(extensions));
    return (fileChooser.showSaveDialog(c) == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
  }

  public class CloseAdapter extends WindowAdapter
  {
    @Override
    public void windowClosed(WindowEvent e)
    {
      closed();
    }
  }
}
