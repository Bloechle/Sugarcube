package sugarcube.common.ui.gui;

import sugarcube.common.data.collections.List3;
import sugarcube.common.ui.gui.icons.ImageIcon3;
import sugarcube.common.data.xml.Treezable;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.util.*;

public class Tree3 extends JTree
{
  public static class Renderer extends DefaultTreeCellRenderer
  {
    public Renderer()
    {
      try
      {
        this.setLeafIcon(new ImageIcon3("tree-leaf.png"));
      }
      catch (Exception e)
      {
      }
    }
  }

  public static class Model implements TreeModel
  {
    private List<TreeModelListener> listeners = new ArrayList<>();
    private Treezable root;

    public Model(Treezable root)
    {
      this.root = root;
    }

    public Model()
    {
      this.root = new Treezable()
      {
        @Override
        public List<Treezable> children()
        {
          return new LinkedList<>();
        }

        @Override
        public Treezable parent()
        {
          return null;
        }

        @Override
        public String sticker()
        {
          return "Empty Tree";
        }
      };
    }

    @Override
    public Treezable getRoot()
    {
      return root;
    }

    private Collection<? extends Treezable> children(Object parent)
    {
      Collection<? extends Treezable> children = parent == null ? new List3<Treezable>() : ((Treezable) parent).children();
      return children == null ? new List3<Treezable>() : children;
    }

    public int getChildCount()
    {
      return children(root).size();
    }

    @Override
    public int getChildCount(Object parent)
    {
      return children(parent).size();
    }

    @Override
    public boolean isLeaf(Object node)
    {
      return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
    }

    @Override
    public Treezable getChild(Object parent, int index)
    {
      for (Treezable child : children(parent))
        if (index-- == 0)
          return child;
      return null;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
      int index = 0;
      for (Treezable treezable : children(parent))
      {
        if (treezable == child)
          return index;
        index++;
      }
      return 0;
    }

    private List<Treezable> addChildren(List<Treezable> nodes, Treezable parent)
    {
      nodes.add(parent);
      for (Treezable child : parent.children())
        addChildren(nodes, child);
      return nodes;
    }

    public List<Treezable> getNodes()
    {
      return addChildren(new LinkedList<Treezable>(), root);
    }

    public TreePath getTreePath(Treezable node)
    {
      List<Treezable> path = new LinkedList<>();
      path.add(node);
      while ((node = node.parent()) != null)
        path.add(node);
      Collections.reverse(path);
      return new TreePath(path.toArray(new Treezable[0]));
    }

    @Override
    public void addTreeModelListener(TreeModelListener listener)
    {
      if (listener != null && !listeners.contains(listener))
        listeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener)
    {
      if (listener != null)
        listeners.remove(listener);
    }

    public void treeNodesChanged(TreeModelEvent e)
    {
      for (TreeModelListener listener : listeners)
        listener.treeNodesChanged(e);
    }

    public void treeNodesInserted(TreeModelEvent e)
    {
      for (TreeModelListener listener : listeners)
        listener.treeNodesInserted(e);
    }

    public void treeNodesRemoved(TreeModelEvent e)
    {
      for (TreeModelListener listener : listeners)
        listener.treeNodesRemoved(e);
    }

    public void treeStructureChanged(TreeModelEvent e)
    {
      for (TreeModelListener listener : listeners)
        listener.treeStructureChanged(e);
    }
  }

  public Tree3(Model treeModel, TreeCellRenderer cellRenderer)
  {
    super(treeModel);
    this.setLargeModel(false);
    this.setRootVisible(true);
    this.setShowsRootHandles(true);
    //this.setFont(Font3.WIDGET_FONT);
    this.setCellRenderer(cellRenderer);
    //this.setEditable(true);
  }

  public Tree3(Model treeModel)
  {
    this(treeModel, new Renderer());
  }

  public Tree3(Treezable root)
  {
    this(new Model(root));
  }

  public Tree3(Treezable root, TreeCellRenderer cellRenderer)
  {
    this(new Model(root), cellRenderer);
  }

  public Tree3()
  {
    this(new Model());
  }

  public void expandAll()
  {
    expandSubTree(getPathForRow(0));
  }

  private void expandSubTree(TreePath path)
  {
    expandPath(path);
    Object node = path.getLastPathComponent();
    int childrenNumber = getModel().getChildCount(node);
    TreePath[] childrenPath = new TreePath[childrenNumber];
    for (int childIndex = 0; childIndex < childrenNumber; childIndex++)
    {
      childrenPath[childIndex] = path.pathByAddingChild(getModel().getChild(node, childIndex));
      expandSubTree(childrenPath[childIndex]);
    }
  }

  public Treezable[] path(Treezable node)
  {
    List<Treezable> path = new LinkedList<>();
    path.add(node);
    while (node.parent() != null)
      path.add(node = node.parent());
    Collections.reverse(path);
    return path.toArray(new Treezable[0]);
  }

  public void refreshAttributes(Treezable node)
  {
    this.getModel().treeNodesChanged(new TreeModelEvent(this, path(node)));
  }

  public void refreshStructure(Treezable node)
  {
    this.getModel().treeStructureChanged(new TreeModelEvent(this, path(node)));
  }

  public void setSelected(Treezable node)
  {
    this.setSelectionPath(this.getModel().getTreePath(node));
  }

  public void setSingleTreeSelection()
  {
    this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
  }

  public void setMultipleTreeSelection()
  {
    this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
  }

  public void setContiguousTreeSelection()
  {
    this.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
  }

  @Override
  public Treezable getLastSelectedPathComponent()
  {
    return (Treezable) super.getLastSelectedPathComponent();
  }

  public Treezable[] getSelectedPathComponents()
  {
    TreePath[] paths = super.getSelectionPaths();
    if (paths == null)
      return new Treezable[0];
    else
    {
      Treezable[] treezables = new Treezable[paths.length];
      for (int i = 0; i < treezables.length; i++)
        treezables[i] = (Treezable) paths[i].getLastPathComponent();
      return treezables;
    }
  }

  @Override
  public String convertValueToText(Object value, boolean selected,
    boolean expanded, boolean leaf, int row,
    boolean hasFocus)
  {
    return ((Treezable) value).sticker();
  }

  public void setModel(Model model)
  {
    super.setModel(model);
  }

  public Treezable getRoot()
  {
    return getModel().getRoot();
  }

  @Override
  public Model getModel()
  {
    return (Model) super.getModel();
  }
}
