package sugarcube.common.ui.gui;

import sugarcube.common.system.log.Logger;
import sugarcube.common.system.log.Logger.Level;
import sugarcube.common.system.log.Logger.LogRecord;
import sugarcube.common.interfaces.Loggable;
import sugarcube.common.system.io.hardware.Mouse;
import sugarcube.common.data.Clipboard;
import sugarcube.common.system.time.Date3;
import sugarcube.common.data.xml.css.CSSBuilder;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.event.ActionEvent;

public class LogPane extends ScrollPane3 implements Loggable
{
  protected JEditorPane editor = new JEditorPane();
  protected HTMLEditorKit kit = new HTMLEditorKit();
  protected StyleSheet kitCss = kit.getStyleSheet();
  protected Document kitDoc = kit.createDefaultDocument();
  protected CSSBuilder css = new CSSBuilder();
  protected Logger.Recorder recorder = new Logger.Recorder();
  protected Logger.Level level = Logger.Level.INFO;
  protected int logID = 1;
  protected long timestamp = 0;
  protected int showClass = -1;

  public LogPane()
  {
    super();
    this.editor.setEditorKit(kit);
    this.setViewportView(editor);
    this.initialize();
  }

  public LogPane(Logger.Level level)
  {
    this();
    this.level = level;
  }

  public LogPane(Logger.Recorder recorder, Logger.Level level)
  {
    this();
    this.recorder = recorder;
    this.level = level;
  }

  public void setLevel(Level level)
  {
    this.level = level;
  }

  @Override
  public void setTransferHandler(TransferHandler newHandler)
  {
    super.setTransferHandler(newHandler);
    this.editor.setTransferHandler(newHandler);
  }

  private void initialize()
  {
    this.addLoggingRule();
    final Popup3 pp = new Popup3(editor, new Popup3.Listener()
    {
      @Override
      public boolean popupRequest(Popup3 popup, Mouse e)
      {
        popup.clear();
        popup.add(new Action3("Logging level: Debug")
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            level = Logger.Level.DEBUG;
            refresh();
          }
        }.enabled(!level.equals(Logger.Level.DEBUG)));
        popup.add(new Action3("Logging level: Info")
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            level = Logger.Level.INFO;
            refresh();
          }
        }.enabled(!level.equals(Logger.Level.INFO)));
        popup.addSeparator();
        popup.add(new Action3("Copy HTML Log")
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            Clipboard.clip(htmlLog());
          }
        });
        popup.addSeparator();
        popup.add(new Action3("Clear Log")
        {
          @Override
          public void actionPerformed(ActionEvent e)
          {
            recorder.clear();
            log(LogPane.this, " - logger restarted at " + (new Date3()).toString(), Level.INFO);            
            refresh();
          }
        });
        return true;
      }
    });
    this.log(this, " - logger started at " + (new Date3()).toString(), Level.INFO);
  }

  public String htmlLog()
  {    
    return recorder.htmlLog("Log", Level.DEBUG, -1, css.toString(), false);
  }

  public void printDemoMessages()
  {
    log(this, ".writeDemoMessage - debug message...", Logger.Level.DEBUG);
    log(this, ".writeDemoMessage - info message...", Logger.Level.INFO);
    log(this, ".writeDemoMessage - info message bis...", Logger.Level.INFO);
    log(this, ".writeDemoMessage - warning message...", Logger.Level.WARNING);
    log(this, ".writeDemoMessage - error message...", Logger.Level.ERROR);
  }

  public void addRule(String element, String... properties)
  {
    css.write(element, properties);
    kitCss.addRule(new CSSBuilder(element, properties).toString());

  }

  public void addLoggingRule()
  {
    this.addRule("p", "font-family: monospace", "margin: 0px 0px 0px 0px", "padding: 0px 0px 0px 0py", "border-width: 0px 0px 1px 0px", "border-style: dotted", "border-color: #DDDDDD");
    //level text
    this.addRule(".l-trace", "color:#000000", "font-weight:bold");
    this.addRule(".l-debug", "color:#887700", "font-weight:bold");
    this.addRule(".l-config", "color:#0000FF", "font-weight:bold");
    this.addRule(".l-info", "color:#008000", "font-weight:bold");
    this.addRule(".l-warning", "color:#DD8800", "font-weight:bold");
    this.addRule(".l-error", "color:#FF0000", "font-weight:bold");
    //paragraph background
    this.addRule(".p-trace", "background-color:#AAAAAA");
    this.addRule(".p-debug", "background-color:#FFFFAA");
    this.addRule(".p-config", "background-color:#BBBBFF");
    this.addRule(".p-info", "background-color:#BBDDBB");
    this.addRule(".p-warning", "background-color:#FFDBAA");
    this.addRule(".p-error", "background-color:#FFAAAA");
  }

  @Override
  public Loggable log(Object source, Object message, Logger.Level level)
  {
    try
    {
      recorder.add(logID++, source, message, level);
      this.refresh();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return this;
  }

  public void refresh()
  {
    long time = System.currentTimeMillis();
    timestamp = time;
    StringBuilder html = new StringBuilder(LogRecord.AVG_SIZE * recorder.size());
    for (LogRecord record : recorder)
      if (record.level.isHigherOrEqual(level))
        html.append(record.toHTML(record.id));
    this.editor.setText(html.toString());
    this.invokeVerticalMax();
//    this.repaint();
  }

  public void flush()
  {
    this.timestamp = 0;
    this.refresh();
  }

  public void setHTML(String html)
  {
    editor.setText(html);
  }

  public Document document()
  {
    return this.kitDoc;
  }

  public HTMLEditorKit kit()
  {
    return kit;
  }

  public StyleSheet css()
  {
    return kitCss;
  }
}
