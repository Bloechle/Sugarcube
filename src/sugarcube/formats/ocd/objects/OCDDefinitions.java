package sugarcube.formats.ocd.objects;

import sugarcube.common.data.collections.List3;
import sugarcube.common.data.collections.Map3;
import sugarcube.common.data.collections.StringSet;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.data.xml.DomNode;
import sugarcube.common.data.xml.Xml;
import sugarcube.common.data.xml.XmlINode;
import sugarcube.formats.ocd.OCD;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OCDDefinitions extends OCDNode implements Iterable<OCDPaintable>
{
  public static final String TAG = "definitions";
  private Map3<String, OCDPaintable> defs = new Map3<String, OCDPaintable>();

  public OCDDefinitions(OCDPage page)
  {
    super(TAG, page);
    this.addDefinition(new OCDClip(this, page().bounds(), OCDClip.ID_PAGE));
  }

  @Override
  public OCDDefinitions clear()
  {
    for (OCDPaintable def : defs)
      def.setParent(null);
    this.defs.clear();
    return this;
  }
  
  public StringSet clipIDs()
  {
    StringSet ids = new StringSet();
    for (OCDPaintable node : this)
      if (node.isClip())
        ids.add(node.id());
    return ids;      
  }
  
  public String newClipID()
  {
    StringSet clipIDs = clipIDs();
    int i = clipIDs.size();
    String clipID = "c"+i;
    while(clipIDs.has(clipID))
      clipID = "c"+(++i);
    return clipID;
  }
  
  public OCDClip newClip(Shape path)
  {
    OCDClip clip = new OCDClip(this, path, newClipID());
    this.addDefinition(clip);
    return clip;   
  }

  public List3<OCDClip> clips()
  {
    List3<OCDClip> clips = new List3<OCDClip>();
    for (OCDPaintable node : this)
      if (node.isClip())
        clips.add((OCDClip)node);
    return clips;
  }

  public OCDClip clip(String id)
  {
    OCDPaintable clip = this.defs.get(id);
    return clip != null && clip.isClip() ? (OCDClip)clip : null;
  }

  @Override
  public Collection<? extends OCDNode> writeAttributes(Xml xml)
  {
    return this.children();
  }

  @Override
  public void readAttributes(DomNode e)
  {
  }

  @Override
  public XmlINode newChild(DomNode child)
  {
    if (OCD.isClip(child))
      return new OCDClip(this);
    return null;
  }

  @Override
  public void endChild(XmlINode child)
  {
    if (child == null)
      return;
    if (OCD.isClip(child))
      addDefinition((OCDClip) child);
  }

  public void addDefinition(OCDPaintable def)
  {
    def.setParent(this);    
    this.defs.put(def.needID(), def);
  }

  public void removeDefinition(OCDPaintable def)
  {
    this.defs.remove(def.id());
  }

  @Override
  public List<? extends OCDNode> children()
  {
    return defs.list();
  }

  @Override
  public String sticker()
  {
    return  "Definitions["+this.nbOfChildren()+"]";
  }

  @Override
  public void paint(Graphics3 g, OCD.ViewProps props)
  {    
    for (OCDNode node : children())
      node.paint(g, props);
  }

  @Override
  public Iterator<OCDPaintable> iterator()
  {
    return this.defs.values().iterator();
  }
}