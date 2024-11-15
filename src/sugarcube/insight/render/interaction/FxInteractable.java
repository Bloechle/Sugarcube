package sugarcube.insight.render.interaction;

import sugarcube.common.graphics.geom.Line3;
import sugarcube.common.graphics.geom.Transform3;
import sugarcube.insight.render.FxOCDNode;

public interface FxInteractable
{
  Transform3 transform();
  
  Line3 extent();
  
  boolean isInteractable();
  
  default boolean isMovable()
  {
    return false;
  }
  
  default boolean isResizable()
  {
    return false;
  }
  
  void interacted(FxInteractor interactor);
  
  void dispose();
  
  default boolean isNode()
  {
    return false;
  }
  
  default FxOCDNode node()
  {
    return isNode() ? (FxOCDNode)this : null;
  }
}
