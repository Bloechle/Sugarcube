package sugarcube.common.ui.gui;

import sugarcube.common.data.Clipboard;

import javax.swing.*;
import java.awt.event.*;

public class TextArea3 extends JTextArea
{
  protected JPopupMenu popup;

  public TextArea3()
  {
    super();
    this.initialize();
  }

  public TextArea3(int cols)
  {
    super(0, cols);
    this.initialize();
  }

  public TextArea3(int rows, int cols)
  {
    super(rows, cols);
    this.initialize();
  }

  public TextArea3(int rows, int cols, Font3 font)
  {
    super(rows, cols);
    this.setFont(font);
    this.initialize();
  }

  public TextArea3(String text)
  {
    super(text);
    this.initialize();
  }

  public TextArea3(String text, int rows, int cols, KeyListener listener)
  {
    super(text, rows, cols);
    this.addKeyListener(listener);
  }

  public TextArea3(String text, Font3 font)
  {
    super(text);
    this.setFont(font);
    this.initialize();
  }

  public TextArea3(String text, int rows, int cols)
  {
    super(text, rows, cols);
    this.initialize();
  }

  public TextArea3(String text, int rows, int cols, Font3 font)
  {
    super(text, rows, cols);
    this.setFont(font);
    this.initialize();
  }

  public ScrollPane3 scrollWrap()
  {
    return new ScrollPane3(this);
  }

  public void addText(String text)
  {
    this.setText(this.getText() + text + "\n");
  }

  private void initialize()
  {    
    this.setWrapStyleWord(true);
    this.setLineWrap(true);

    this.popup = new JPopupMenu();

    JMenuItem menuItem = new JMenuItem("Copy");
    menuItem.addActionListener(new WriteToClipBoardActionListener());
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    popup.add(menuItem);

    popup.addSeparator();

    menuItem = new JMenuItem("Clear");
    menuItem.addActionListener(new ClearTextAreaActionListener());
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
    popup.add(menuItem);

    popup.addSeparator();

    menuItem = new JMenuItem("Find");
    menuItem.addActionListener(new FindTextAreaActionListener());
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    popup.add(menuItem);

    this.addMouseListener(new PopupListener());
  }

  private class WriteToClipBoardActionListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      String text = TextArea3.this.getSelectedText();
      Clipboard.clip(text == null ? TextArea3.this.getText() : text);
    }
  }

  private class ClearTextAreaActionListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      TextArea3.this.setText("");
    }
  }

  private class FindTextAreaActionListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      String find = JOptionPane.showInputDialog("Please enter a text to find","");
      String text = getText();
      if (find != null && !find.isEmpty())
      {
        int index = text.indexOf(find);
        if (index >= 0)
        {
          TextArea3.this.setCaretPosition(index);
          TextArea3.this.setSelectionEnd(index + find.length());
          TextArea3.this.setSelectionStart(index);
          TextArea3.this.repaint();
        }
      }
    }
  }

  private class PopupListener extends MouseAdapter
  {
    @Override
    public void mouseReleased(MouseEvent e)
    {
      if (e.isPopupTrigger())
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
  }
}
