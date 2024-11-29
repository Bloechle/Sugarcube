package sugarcube.formats.epub.fonts;

public class CmapSegment {
    public int startCode;
    public int endCode;
    public int idDelta;
    public int idRangeOffset;

    public CmapSegment(int startCode, int endCode, int idDelta, int idRangeOffset) {
        this.startCode = startCode;
        this.endCode = endCode;
        this.idDelta = idDelta;
        this.idRangeOffset = idRangeOffset;
    }
}
