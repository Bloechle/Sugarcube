package sugarcube.insight.core;

import sugarcube.common.system.Prefs;
import sugarcube.formats.ocd.objects.OCDPaintable;

public class FxDisplayProps
{
    public boolean fonts = true;
    public boolean clips = true;
    public boolean paths = true;
    public boolean images = true;
    public boolean texts = true;
    public boolean highlightSpaces = false;
    public boolean highlightTexts = false;
    public boolean highlightPaths = false;
    public boolean highlightClips = false;
    public int nbOfElements = 0;

    public transient int counter = 0;

    public FxDisplayProps update(Prefs prefs)
    {
        fonts = prefs.bool(IS.FONTS_ON, fonts);
        clips = prefs.bool(IS.CLIPS_ON, clips);
        images = prefs.bool(IS.IMAGES_ON, images);
        paths = prefs.bool(IS.PATHS_ON, paths);
        texts = prefs.bool(IS.TEXTS_ON, texts);
        highlightSpaces = prefs.bool(IS.SPACES_HIGH, highlightSpaces);
        highlightTexts = prefs.bool(IS.TEXTS_HIGH, highlightTexts);
        highlightPaths = prefs.bool(IS.PATHS_HIGH, highlightPaths);
        highlightClips = prefs.bool(IS.CLIPS_HIGH, highlightClips);
        nbOfElements = prefs.integer(IS.ELEMENTS_NB, nbOfElements);
        //Log.debug(this,  ".update - nbOfElements: "+nbOfElements);
        return this;
    }

    public boolean doDisplay(OCDPaintable node)
    {
        if (!images && node.isImage() || !paths && node.isPath() || !texts && (node.isText() || node.isTextBlock()))
            return false;

        return true;
    }

    public boolean isCounterOK()
    {
        if (nbOfElements <= 0)
            return true;

        if (counter < nbOfElements)
        {
            counter++;
            return true;
        }
        return false;
    }
}
