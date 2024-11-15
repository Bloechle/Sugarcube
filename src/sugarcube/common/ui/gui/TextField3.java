package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.Set3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.interfaces.Checkable;
import sugarcube.common.interfaces.Checker;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

public class TextField3 extends JTextField
{
  public static interface Listener
  {
    public void textFieldEvent(TextField3 textField, Object event);
  }

  private Set3<Listener> listeners = new Set3<>();

  public TextField3(String text, int columns, ActionListener... listeners)
  {
    super(text, columns);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
    init();
  }

  public TextField3(String text, ActionListener... listeners)
  {
    super(text);
    for (ActionListener listener : listeners)
      this.addActionListener(listener);
    init();
  }

  public TextField3(String text, KeyListener listener)
  {
    super(text);
    this.addKeyListener(listener);
    init();
  }

  private void init()
  {

    getDocument().addDocumentListener(new DocumentListener()
    {
      @Override
      public void insertUpdate(DocumentEvent de)
      {
        notifyListeners(de);
      }

      @Override
      public void removeUpdate(DocumentEvent de)
      {
        notifyListeners(de);
      }

      @Override
      public void changedUpdate(DocumentEvent de)
      {
        notifyListeners(de);
      }
    });
    addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent ae)
      {
        notifyListeners(ae);
      }
    });

  }

  public void notifyListeners(Object e)
  {
    for (Listener listener : listeners)
    {
      listener.textFieldEvent(this, e);
    }
  }

  public void addListener(Listener listener)
  {
    this.listeners.add(listener);
  }

  public TextField3 ensureSize(int w, int h)
  {
    return this.ensureSize(new Dimension(w, h));
  }

  public TextField3 ensureSize(Dimension dimension)
  {
    this.setMinimumSize(dimension);
    this.setPreferredSize(dimension);
    this.setMaximumSize(dimension);
    return this;
  }

  public static Checker fieldChecker(final JTextField field, final Checkable checkable)
  {
    final Checker checker = new Checker()
    {
      final Color white = Color3.ASPARAGUS_GREEN.brighter();
      final Color red = Color3.CARROT_ORANGE.brighter();

      @Override
      public void doCheck()
      {
        field.setBackground(checkable.check() ? white : red);
      }
    };

    field.getDocument().addDocumentListener(new DocumentListener()
    {
      @Override
      public void insertUpdate(DocumentEvent de)
      {
        checker.doCheck();
      }

      @Override
      public void removeUpdate(DocumentEvent de)
      {
        checker.doCheck();
      }

      @Override
      public void changedUpdate(DocumentEvent de)
      {
        checker.doCheck();
      }
    });
    field.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent ae)
      {
        checker.doCheck();
      }
    });
    return checker;
  }
}
