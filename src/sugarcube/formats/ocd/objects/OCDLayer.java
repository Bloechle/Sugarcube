package sugarcube.formats.ocd.objects;


public class OCDLayer extends OCDContent
{
  public static final String NAME_BACKGROUND = "background";

  public OCDLayer(OCDNode parent, String name, OCDPaintable... nodes)
  {
    super(OCDGroup.LAYER, parent);
    this.setName(name == null ? "" : name);
    if (nodes.length > 0)
    {
      this.addAll(nodes);
      this.extent = this.bounds().extent();
    }
  }

//  @Override
//  public Collection<? extends OCDNode> writeAttributes(Xml xml)
//  {
//    this.writeGroupAttributes(xml);
//    if (layerProps != null)
//    {
//      if (layerProps.alpha < 1)
//        xml.write("transparency", layerProps.alpha);
//      if (layerProps.offsetX != 0)
//        xml.write("offset-x", layerProps.offsetX);
//      if (layerProps.offsetY != 0)
//        xml.write("offset-y", layerProps.offsetY);
//    }
//    props.writeAttributes(xml);
//    return this.children();
//  }
//
//  @Override
//  public void readAttributes(DomNode dom)
//  {
//    LayerProps lp = new LayerProps();    
//    lp.alpha = dom.real("transparency", 1);
//    lp.offsetX = dom.real("offset-x", 0);
//    lp.offsetY = dom.real("offset-y", 0);
//    if(lp.alpha ==1 && lp.offsetX==0 && lp.offsetY==0)
//      lp = null;
//    else
//      this.layerProps = lp;
//        
//    super.readAttributes(dom);
//  }
}
