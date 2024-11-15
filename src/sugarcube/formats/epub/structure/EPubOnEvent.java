package sugarcube.formats.epub.structure;

import sugarcube.common.data.collections.Str;
import sugarcube.common.data.xml.Nb;
import sugarcube.formats.epub.structure.js.JS;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class EPubOnEvent
{
  public OCDPaintable source;
  public OCDPaintable target;
  public String sourceID;
  public String onloadAnim;
  public String onclickAnim;
  public int onloadDelay;
  public String targetPage = null;
  public String targetID = null;

  public EPubOnEvent(OCDPaintable source)
  {    
    this.sourceID = source.id();
    this.source = source;
    this.onloadAnim = prop("onload");
    this.onclickAnim = prop("onclick");
    this.onloadDelay = MillisFromSec(prop("onload-delay"));

    // page_0001.xml#sub1
    String anchor = prop("onclick-target");
    if (Str.Has(anchor))
    {
      if (anchor.startsWith("#"))
        anchor = anchor.substring(1);
      if (anchor.endsWith("#"))
        anchor = anchor.substring(0, anchor.length() - 1);

      int i = anchor.indexOf("#");
      if (i > 0)
      {
        targetPage = anchor.substring(0, i);
        targetID = anchor.substring(i + 1, anchor.length());
      } else if (anchor.endsWith(".xml"))
      {
        targetPage = anchor;
        targetID = null;
      } else
      {
        targetPage = null;
        targetID = anchor;
      }
    }
  }

  public boolean hasTargetPage()
  {
    return Str.Has(targetPage);
  }

  public void setTarget(OCDPaintable target)
  {
    this.target = target;
  }

  public String targetID()
  {
    return Str.Has(targetID) ? targetID : sourceID;
  }

  public String imageFilename()
  {
    return source.isImage() ? source.asImage().filename() : null;
  }

  public boolean isVideo()
  {
    return source.isImage() && source.asImage().isMP4();
  }

  public boolean isModal()
  {
    return IsModal(onclickAnim);
  }

  public static boolean IsModal(String anim)
  {
    return anim != null && anim.equals("modal");
  }

  public String onloadJS(String id)
  {
    return javascript(id, onloadAnim, onloadDelay);
  }

  public String onclickJS(String id)
  {
    return javascript(id, onclickAnim, 0);
  }

  public String javascript(String id, String anim, int delay)
  {
    if (anim.equals("modal"))
      return JS.AddClassTimeout("modal-" + id, "-sc-modal-off sc-modal-on", 0);
    else
      return JS.AddClassTimeout(id, anim + " -sc-opacity-0", delay);
  }

  // public static String Anims(String anim, boolean forward)
  // {
  // return forward ? anim + " -" + AnimBack(anim) + " -sc-opacity-0" : "-" +
  // anim + " " + AnimBack(anim) + " sc-opacity-0";
  // }

  // public String anims(boolean forward)
  // {
  // return forward ? anim + " -" + animBack() + " sc-opacity-1 -sc-opacity-0" :
  // "-" + anim + " " + animBack() + " sc-opacity-0 -sc-opacity-1";
  // }

  // public static String AnimBack(String anim)
  // {
  // if (anim.contains("In"))
  // return anim.replace("In", "Out");
  // else if (anim.contains("Out"))
  // return anim.replace("Out", "In");
  // else
  // return anim;
  // }

  public boolean hasOnload()
  {
    return Str.Has(onloadAnim);
  }

  public boolean hasOnclick()
  {
    return Str.Has(onclickAnim);
  }

  public boolean hasOnclickTarget()
  {
    return hasOnclick() && (Str.Has(targetID) || Str.Has(targetPage));
  }

  public boolean hasOnclickTargetOnSamePage()
  {
    return hasOnclickTarget() && targetPage == null;
  }

  public String prop(String key)
  {
    return prop(key, "");
  }

  public String prop(String key, String def)
  {
    return source.get(key, def).trim();
  }

  public static int MillisFromSec(String sec)
  {
    return (int) (Math.round(1000 * Nb.Double(sec.trim().replace("s", ""), 0.0)));
  }
}
