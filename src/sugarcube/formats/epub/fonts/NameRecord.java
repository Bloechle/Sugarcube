package sugarcube.formats.epub.fonts;

public class NameRecord {
    public int platformID;
    public int encodingID;
    public int languageID;
    public int nameID;
    public byte[] string;
    public int length;

    public NameRecord(int platformID, int encodingID, int languageID, int nameID, byte[] string) {
        this.platformID = platformID;
        this.encodingID = encodingID;
        this.languageID = languageID;
        this.nameID = nameID;
        this.string = string;
        this.length = string.length;
    }
}
