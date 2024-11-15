package sugarcube.formats.ocd.objects.font;

import sugarcube.common.system.log.Log;
import sugarcube.common.system.log.Logger;
import sugarcube.common.system.util.Sys;
import sugarcube.common.data.collections.List3;
import sugarcube.common.data.Bool;
import sugarcube.common.system.io.DragAndDrop;
import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.Zipper;
import sugarcube.common.system.process.Progression;
import sugarcube.common.ui.gui.*;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.svg.SVG;

import javax.swing.*;
import java.awt.*;

public class FontSVGizer extends Frame3 implements DragAndDrop.Listener
{
  static
  {
    Sys.LAF();
  }
  protected Progression progression = new Progression();
  protected Bool stopProcess = new Bool();
  private Logger log = new Logger("TTF2SVGFrame Log", null);
  private LogPane logArea;
  private File3[] files = new File3[0];

  public FontSVGizer()
  {
    super("FontSVGizer");
    this.setResizable(true);
    this.sugarcubize();
    //this.setPackDimension();    
    initComponents();
    DragAndDrop fileDrop = new DragAndDrop(this);
    this.setTransferHandler(fileDrop);
    this.logArea = (LogPane) this.logScrollpane;
    this.logArea.setTransferHandler(fileDrop);
    Logger.AddBigBrother(log);
    this.log.addListener(logArea);
    this.inputField.setEditable(false);
    this.progressBar.setStringPainted(true);
    this.setIdleAndProcess(true, false);
    progression.setBoolCancel(stopProcess);
    progression.setProgressBar(progressBar);
    //this.percentLabel.setText(qualitySlider.getValue()+"%");    
    this.center();
    log.info(this, " - Welcome to FontSVGizer");
  }

  public final void setIdleAndProcess(boolean isIdle, boolean doReplicate)
  {

    this.inputButton.setEnabled(isIdle);
    this.inputField.setEnabled(isIdle);
    this.epubButton.setEnabled(doReplicate);
    this.stopButton.setEnabled(!isIdle);
    this.closeButton.setEnabled(isIdle);
    this.setDisposeOnClose(isIdle);
  }

  @Override
  public void close()
  {
    this.stopProcess.setTrue();
    super.close();
  }

  @Override
  public void dropped(DragAndDrop dnd)
  {
    this.updateFiles(dnd.files());
  }

  public void updateFiles(File3... files)
  {
    StringBuilder sb = new StringBuilder();
    List3<File3> validFiles = new List3<File3>();
    for (File3 file : files)
      if (!file.isExtension(".ttf", ".otf"))
        this.progression.log().warn(this, ".updateFile - unsupported file extension: " + file.extension());
      else
      {
        validFiles.add(file);
        sb.append(file.getName()).append("; ");
      }
    this.inputField.setText(sb.toString());
    this.files = validFiles.toArray(new File3[0]);
    this.setIdleAndProcess(true, true);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {

    wrapperPanel = new javax.swing.JPanel();
    mainPanel = new javax.swing.JPanel();
    inputField = new javax.swing.JTextField();
    inputButton = new javax.swing.JButton();
    logScrollpane = new LogPane();
    progressBar = new javax.swing.JProgressBar();
    buttonPanel = new javax.swing.JPanel();
    stopButton = new javax.swing.JButton();
    closeButton = new javax.swing.JButton();
    epubButton = new javax.swing.JButton();
    zipCheck = new javax.swing.JCheckBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    inputField.setText(" Choose TTF or OTF files...");
    inputField.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        inputFieldActionPerformed(evt);
      }
    });

    inputButton.setText("Choose File");
    inputButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        inputButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
    mainPanel.setLayout(mainPanelLayout);
    mainPanelLayout.setHorizontalGroup(
      mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(logScrollpane)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
            .addComponent(inputField, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(inputButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    mainPanelLayout.setVerticalGroup(
      mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(mainPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(inputField)
          .addComponent(inputButton, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(logScrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    stopButton.setText("Stop");
    stopButton.setMaximumSize(new java.awt.Dimension(55, 30));
    stopButton.setMinimumSize(new java.awt.Dimension(55, 30));
    stopButton.setPreferredSize(new java.awt.Dimension(55, 30));
    stopButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        stopButtonActionPerformed(evt);
      }
    });

    closeButton.setText("Close");
    closeButton.setMaximumSize(new java.awt.Dimension(59, 30));
    closeButton.setMinimumSize(new java.awt.Dimension(59, 30));
    closeButton.setPreferredSize(new java.awt.Dimension(59, 30));
    closeButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        closeButtonActionPerformed(evt);
      }
    });

    epubButton.setText("Export to SVG");
    epubButton.setMaximumSize(new java.awt.Dimension(117, 30));
    epubButton.setMinimumSize(new java.awt.Dimension(117, 30));
    epubButton.setPreferredSize(new java.awt.Dimension(117, 30));
    epubButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        epubButtonActionPerformed(evt);
      }
    });

    zipCheck.setText("Zip Fonts");
    zipCheck.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        zipCheckActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
    buttonPanel.setLayout(buttonPanelLayout);
    buttonPanelLayout.setHorizontalGroup(
      buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(buttonPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(epubButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(zipCheck)
        .addGap(65, 65, 65)
        .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );
    buttonPanelLayout.setVerticalGroup(
      buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(buttonPanelLayout.createSequentialGroup()
        .addGap(0, 0, 0)
        .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(epubButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(zipCheck))
          .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    javax.swing.GroupLayout wrapperPanelLayout = new javax.swing.GroupLayout(wrapperPanel);
    wrapperPanel.setLayout(wrapperPanelLayout);
    wrapperPanelLayout.setHorizontalGroup(
      wrapperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wrapperPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );
    wrapperPanelLayout.setVerticalGroup(
      wrapperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(wrapperPanelLayout.createSequentialGroup()
        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(9, 9, 9))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(wrapperPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(wrapperPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void epubButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_epubButtonActionPerformed
  {//GEN-HEADEREND:event_epubButtonActionPerformed
    Log.info(this, " - converting to SVG Font");
    this.stopProcess.setFalse();
    this.progression.reset();

    this.setIdleAndProcess(false, false);
    final boolean doZip = this.zipCheck.isSelected();
    Worker3 worker = new Worker3(new Worker3.Processable()
    {
      @Override
      public void process(Worker3 worker, Object... empty)
      {
        for (int i = 0; i < files.length; i++)
        {
          File3 file = files[i];
          progression.setDescription("Writing file " + file.getName());
          SVGFont font = Font3.Load(Font.TRUETYPE_FONT, file).toSVG();
          font.setFontname(File3.Extense(file.getName(), SVG.EXT));
          if (doZip)
          {
            Zipper zip = null;
            try
            {
              zip = new Zipper(file.extense(SVG.EXTZ));
              zip.putNextEntry(font.filename());
              Xml.write(font.svgRoot(), zip, 2, true);
              zip.disposeEntry();
              zip.dispose();
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
              zip.dispose();
            }
          }
          else
            Xml.write(font.svgRoot(), new File3(file.directory(), font.entryFilename()));
          progression.setProgress(i / (float) (files.length - 1));
        }
      }

      @Override
      public void done(Worker3 worker, Object... empty)
      {
        progression.complete();
        setIdleAndProcess(true, true);
      }
    });
  }//GEN-LAST:event_epubButtonActionPerformed

  private void closeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeButtonActionPerformed
  {//GEN-HEADEREND:event_closeButtonActionPerformed
    close();
  }//GEN-LAST:event_closeButtonActionPerformed

  private void stopButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopButtonActionPerformed
  {//GEN-HEADEREND:event_stopButtonActionPerformed
    this.stopProcess.setTrue();
  }//GEN-LAST:event_stopButtonActionPerformed

  private void inputButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inputButtonActionPerformed
  {//GEN-HEADEREND:event_inputButtonActionPerformed
    FileChooser3 chooser = new FileChooser3(this);
    chooser.setFileSelectionMode(FileChooser3.FILES_AND_DIRECTORIES);
    chooser.setMultiSelectionEnabled(false);
    if (chooser.acceptOpenDialog())
      updateFiles(chooser.files());
  }//GEN-LAST:event_inputButtonActionPerformed

  private void inputFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_inputFieldActionPerformed
  {//GEN-HEADEREND:event_inputFieldActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_inputFieldActionPerformed

  private void zipCheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zipCheckActionPerformed
  {//GEN-HEADEREND:event_zipCheckActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_zipCheckActionPerformed

  public static void main(String args[])
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        new FontSVGizer().setVisible(true);
      }
    });
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel buttonPanel;
  private javax.swing.JButton closeButton;
  private javax.swing.JButton epubButton;
  private javax.swing.JButton inputButton;
  private javax.swing.JTextField inputField;
  private javax.swing.JScrollPane logScrollpane;
  private javax.swing.JPanel mainPanel;
  private javax.swing.JProgressBar progressBar;
  private javax.swing.JButton stopButton;
  private javax.swing.JPanel wrapperPanel;
  private javax.swing.JCheckBox zipCheck;
  // End of variables declaration//GEN-END:variables
}
