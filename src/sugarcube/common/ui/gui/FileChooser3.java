package sugarcube.common.ui.gui;

import sugarcube.common.system.io.File3;
import sugarcube.common.system.io.FileFilter3;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileChooser3 extends JFileChooser
{  
  public static String LAST_DIR = File3.USER_HOME;
  private Component parent; 

  public FileChooser3()
  {
    super(LAST_DIR);
  }

  public FileChooser3(File file)
  {
    super(file);
    if (!file.isDirectory())
      this.setSelectedFile(file);
  }

  public FileChooser3(Component parent, File file)
  {
    this(file);
    this.parent = parent;
  }

  public FileChooser3(Component parent)
  {
    this();
    this.parent = parent;
  }

  public FileChooser3(String filePath)
  {
    this(new File3(filePath).directory());
    if (!new File3(filePath).isDirectory())
      this.setSelectedFile(new File(filePath));
  }

  public FileChooser3(Component parent, String filePath)
  {
    this(filePath);
    this.parent = parent;
  }

  public FileChooser3 enableMultiSelection()
  {
    this.setMultiSelectionEnabled(true);
    return this;
  }

  public FileChooser3 enableFileSelectionMode()
  {
    this.setFileSelectionMode(FILES_ONLY);
    return this;
  }

  public FileChooser3 enableDirectorySelectionMode()
  {
    this.setFileSelectionMode(DIRECTORIES_ONLY);
    return this;
  }

  public FileChooser3 enableFileAndDirectorySelectionMode()
  {
    this.setFileSelectionMode(FILES_AND_DIRECTORIES);
    return this;
  }

  public FileChooser3 filter(boolean acceptDirectory, String... extensions)
  {
    this.setFileFilter(new FileFilter3(acceptDirectory, extensions));
    return this;
  }

  public FileChooser3 filter(String... extensions)
  {
    this.setFileFilter(new FileFilter3(extensions));
    return this;
  }

  public File3 file()
  {
    return this.getSelectedFile();
  }

  public File3[] files()
  {
    return this.getSelectedFiles();
  }

  @Override
  public File3 getSelectedFile()
  {
    File file = super.getSelectedFile();
    if (file == null)
      return null;
    else
    {
      File3 file3 = new File3(file);
      LAST_DIR = file3.directory().path();
      return file3;
    }
  }

  @Override
  public File3[] getSelectedFiles()
  {
    File[] files = super.getSelectedFiles();
    File3[] files3 = new File3[files.length];
    for (int i = 0; i < files.length; i++)
      files3[i] = new File3(files[i]);
    if (files3.length > 0)
      LAST_DIR = files3[files3.length - 1].directory().path();
    return files3;
  }

  public boolean acceptOpenDialog()
  {
    return (showOpenDialog(parent) == FileChooser3.APPROVE_OPTION);
  }

  public boolean acceptSaveDialog()
  {
    return (showSaveDialog(parent) == FileChooser3.APPROVE_OPTION);
  }

  public boolean acceptDialog(String approveButtonText)
  {
    return (this.showDialog(parent, approveButtonText) == FileChooser3.APPROVE_OPTION);
  }
}
