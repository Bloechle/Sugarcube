package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.StringMap;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.ui.gui.FontChooser3.FontItem;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class FontChooser3 extends ComboBox3<FontItem> implements ItemListener
{
  private Mute mute = new Mute();
  // preview font size
  private static int fontsize = 14;
  private int lastSize = 5;
  private StringMap<FontItem> items = new StringMap<FontItem>();
  private StringSet lasts = new StringSet();

  public FontChooser3()
  {
    String[] fonts = Font3.ListOSFonts();
    for (String font : fonts)
      addFontItem(font, null);
    this.init();
  }

  public FontChooser3(Class path, String... fonts)
  {
    for (String font : fonts)
      addFontItem(font, path);
    this.init();
  }

  private void init()
  {
    updateList(null);
    this.setEditable(true);
    this.setEditor(new FontComboEditor());
    this.setRenderer(new FontCellRenderer());
    this.addItemListener(this);
  }

  private FontItem addFontItem(String fontname, Object path)
  {
    FontItem item = new FontItem(fontname, path);
    if (item.font != null)
      items.put(item.toString(), item);
    return item;
  }

  public int previewFontsize()
  {
    return fontsize;
  }

  public void setPreviewFontSize(int previewFontSize)
  {
    this.fontsize = previewFontSize;
    updateList(selectedName());
  }

  public int lastSize()
  {
    return lastSize;
  }

  public void setLastSize(int size)
  {
    this.lastSize = size;
    boolean update = false;
    while (lasts.size() > size)
    {
      lasts.removeFirst();
      update = true;
    }
    if (update)
      updateList(selectedName());
  }

  @Override
  public void itemStateChanged(ItemEvent e)
  {
    String name = selectedName();
    if (name != null && lastSize > 0 && !(lasts.size() > 0 && (lasts.first().equals(name))))
    {
      lasts.remove(name);
      lasts.add(name);
      if (lasts.size() > lastSize)
        lasts.removeFirst();
      // updateList(font);
    }
  }

  private synchronized void updateList(String selected)
  {
    mute.setOn();
    removeAllItems();
    if (lasts.size() > 0)
    {
      for (String last : lasts)
      {
        FontItem item = items.get(last);
        if (item != null)
          item = new FontItem(item.fontname, item.font);
        addItem(item);
      }
      addItem(new FontItem(null, null)); // separator
    }
    // regular items
    for (String font : items.keySet())
    {
      FontItem item = items.get(font);
      addItem(item);
    }
    if (selected != null)
      setSelectedItem(false, selected);
    mute.setOff();
  }

  public String selectedName()
  {
    Object selected = this.getSelectedItem();
    if (selected != null)
      return ((FontItem) selected).fontname;
    else
      return null;
  }

  @Override
  public Dimension getPreferredSize()
  {
    return new Dimension(super.getPreferredSize().width, new JComboBox().getPreferredSize().height);
  }

  @Override
  public void setSelectedItem(Object fontItem)
  {
    this.setSelectedItem(true, fontItem);
  }

  @Override
  public void setSelectedItem(boolean triggerAction, Object fontItem)
  {
    FontItem item = fontItem instanceof FontItem ? (FontItem) fontItem : items.get(fontItem.toString()); // first
                                                                                                         // in
                                                                                                         // recent
                                                                                                         // items
    if (item != null)
      super.setSelectedItem(triggerAction, item);
  }

  class FontComboEditor extends BasicComboBoxEditor
  {
    class AutoCompletionDoc extends PlainDocument
    {
      private JTextField field = FontComboEditor.this.editor;

      @Override
      public void replace(int i, int j, String s, AttributeSet set) throws BadLocationException
      {
        super.remove(i, j);
        insertString(i, s, set);
      }

      @Override
      public void insertString(int i, String s, AttributeSet set) throws BadLocationException
      {
        if (s != null && !"".equals(s))
        {
          String s1 = getText(0, i);
          String s2 = getMatch(s1 + s);
          int j = (i + s.length()) - 1;
          if (s2 == null)
          {
            s2 = getMatch(s1);
            j--;
          }
          if (s2 != null)
            FontChooser3.this.setSelectedItem(s2);
          super.remove(0, getLength());
          super.insertString(0, s2, set);
          field.setSelectionStart(j + 1);
          field.setSelectionEnd(getLength());
        }
      }

      @Override
      public void remove(int i, int j) throws BadLocationException
      {
        int k = field.getSelectionStart();
        if (k > 0)
          k--;
        String s = getMatch(getText(0, k));
        super.remove(0, getLength());
        super.insertString(0, s, null);

        if (s != null)
          FontChooser3.this.setSelectedItem(s);
        try
        {
          field.setSelectionStart(k);
          field.setSelectionEnd(getLength());
        } catch (Exception exception)
        {
        }
      }
    }

    private FontComboEditor()
    {
      editor.setDocument(new AutoCompletionDoc());
      if (items.size() > 0)
        editor.setText(items.firstKey());
    }

    private String getMatch(String input)
    {
      if (items.has(input))
        return input;
      else
        for (String name : items.keySet())
          if (name.toLowerCase().startsWith(input.toLowerCase()))
            return name;
      return null;
    }

    public void replaceSelection(String s)
    {
      AutoCompletionDoc doc = (AutoCompletionDoc) editor.getDocument();
      try
      {
        Caret caret = editor.getCaret();
        int i = min(caret.getDot(), caret.getMark());
        int j = max(caret.getDot(), caret.getMark());
        doc.replace(i, j - i, s, null);
      } catch (BadLocationException ex)
      {
      }
    }
  }

  private class FontCellRenderer implements ListCellRenderer
  {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
      // extract the component from the item's value
      FontItem item = (FontItem) value;
      boolean s = (isSelected && !item.isSeparator);
      item.setBackground(s ? list.getSelectionBackground() : list.getBackground());
      item.setForeground(s ? list.getSelectionForeground() : list.getForeground());
      return item;
    }
  }

  public static class FontItem extends JPanel
  {
    public String fontname;
    public Font3 font;

    private boolean isSeparator;

    public FontItem(String fontname, Object path)
    {
      this.fontname = fontname;
      if (fontname != null)
      {
        if (path != null)
        {
          if (path instanceof Class)
          {
            String filename = Font3.normalize(fontname, "");
            this.font = Font3.Load((Class) path, filename);
            fontname = filename;
          }
        }

        if (this.font == null)
          this.font = new Font3(fontname, Font.PLAIN, 1);
        this.isSeparator = false;
      } else
      {
        this.font = null;
        this.isSeparator = true;
      }
      this.init();
    }

    public FontItem(String fontname, Font3 font)
    {
      this.fontname = fontname;
      this.font = font;
      this.init();
    }

    private void init()
    {
      this.setOpaque(true);
      if (!isSeparator)
      {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        boolean displayFont = true;
        for (int i = 0; i < fontname.length(); i++)
          if (!font.canDisplay(fontname.charAt(i)))
          {
            displayFont = false;
            break;
          }
        JLabel label = new JLabel(fontname);
        if (font != null && displayFont)
          label.setFont(font.derive((float)fontsize));
        this.add(label);
      } else
      {
        this.setLayout(new BorderLayout());
        this.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
      }
    }

    @Override
    public String toString()
    {
      return fontname == null ? "" : fontname;
    }
  }

  public static void main(String... args)
  {
  }
}