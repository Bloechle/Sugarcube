package sugarcube.formats.pdf.reader.pdf.node;

import sugarcube.common.data.collections.List3;
import sugarcube.common.graphics.Graphics3;
import sugarcube.common.data.xml.Treezable;
import sugarcube.formats.pdf.reader.Dexter;
import sugarcube.formats.pdf.reader.PDFDisplayProps;
import sugarcube.formats.pdf.reader.pdf.node.annotation.PDFLinkAnnot;
import sugarcube.formats.pdf.reader.pdf.node.annotation.PDFWidgetAnnot;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;
import sugarcube.formats.pdf.reader.pdf.object.Reference;
import sugarcube.formats.pdf.reader.pdf.object.StreamLocator;

import java.util.*;

/**
 * It's basically a tree node that has a given type, a parent and children
 */
public abstract class PDFNode<T extends PDFNode> implements Treezable, Iterable<T>
{
    protected PDFDocument document;
    protected final List3<T> children = new List3<>();
    protected PDFNode parent;
    protected final String type;
    protected Reference reference;
    protected int id;

    /**
     * Caution: parent==null for PDFMatrix, PDFOperator and PDFEnvironment
     */
    protected PDFNode(String type, PDFNode parent)
    {
        this.type = type;
        this.parent = parent;
        if (parent != null)
            this.document = parent.document();
    }

//    public Object addToPool(Reference pointerRef, Object node)
//    {
//        if (document == null)
//            Log.debug(this, ".addToPool - document not found: " + this);
//        else
//            document.addToPool(pointerRef, node);
//        return node;
//    }

    public StreamLocator streamLocator()
    {
        return null;
    }

    public Reference reference()
    {
        return reference;
    }

    public boolean isReference(int id)
    {
        return this.reference != null && this.reference.id() == id;
    }

    public boolean isReference(int id, int gen)
    {
        return this.reference != null && this.reference.id() == id && this.reference.generation() == gen;
    }

    @Override
    public abstract String sticker();

    @Override
    public Iterator<T> iterator()
    {
        return children().iterator();
    }

    @Override
    public String toString()
    {
        return type;
    }

    public PDFPage page()
    {
        if (this.isDocument())
            return this.toDocument().firstPage();
        PDFNode node = this;
        while (node != null && !node.isPage() && !node.isDocument())
            node = node.parent();
        if (node != null)
            return node.isPage() ? node.toPage() : node.isDocument() ? node.toDocument().firstPage() : null;
        return null;
    }

    public PDFDocument document()
    {
        return document == null && this.isDocument() ? document = this.toDocument() : document;
    }

    public void add(T node)
    {
        if (node != null)
        {
            node.id = children.size();
            node.parent = this;
            this.children.add(node);
        }
    }

    @Override
    public LinkedList<T> children()
    {
        if (Dexter.DEBUG_TYPE_AVOID == null || Dexter.DEBUG_TYPE_AVOID.isEmpty())
            return children;
        List3<T> list = new List3<T>();
        for (T t : children)
            if (Dexter.DEBUG_TYPE_AVOID.hasnt(t.type))
                list.add(t);
        return list;
    }

    public LinkedList<T> list()
    {
        return children();
    }

    public List<PDFNode> list(String... types)
    {
        Set<String> filters = new HashSet<String>();
        filters.addAll(Arrays.asList(types));
        List<PDFNode> filtered = new LinkedList<PDFNode>();
        for (PDFNode vo : this)
            if (types.length == 0 || filters.contains(vo.type))
                filtered.add(vo);
        return filtered;
    }

    public PDFNode instance(PDFContent content, PDFInstr instr, PDFContext context)
    {
        return this;
    }

    @Override
    public PDFNode parent()
    {
        return parent;
    }

    public void setParent(PDFNode parentNode)
    {
        this.parent = parentNode;
    }

    public int nbOfChildren()
    {
        return list().size();
    }

    public int id()
    {
        return this.id;
    }

    public String getType()
    {
        return type;
    }

    public boolean isValid()
    {
        return true;
    }

    public boolean isWidget()
    {
        return this.type.equals(Dexter.WIDGET);
    }

    public boolean isWidgetForm()
    {
        return false;
    }

    public boolean isLink()
    {
        return this.type.equals(Dexter.LINK);
    }

    public boolean isRichMedia()
    {
        return this.type.equals(Dexter.RICH_MEDIA);
    }

    public boolean isText()
    {
        return this.type.equals(Dexter.TEXT);
    }

    public boolean isPath()
    {
        return this.type.equals(Dexter.PATH);
    }

    public boolean isImage()
    {
        return this.type.equals(Dexter.IMAGE);
    }

    public boolean isClip()
    {
        return this.type.equals(Dexter.CLIP);
    }

    public boolean isPage()
    {
        return this.type.equals(Dexter.PAGE);
    }

    public boolean isContent()
    {
        return this.type.equals(Dexter.CONTENT);
    }

    public boolean isResources()
    {
        return this.type.equals(Dexter.RESOURCES);
    }

    public boolean isResourceChild()
    {
        return this.isResources() ? true : this.parent == null ? false : parent.isResourceChild();
    }

    public boolean isDocument()
    {
        return this.type.equals(Dexter.DOCUMENT);
    }

    public boolean isBadOp()
    {
        return this.type.equals(Dexter.BADOP);
    }

    public PDFLinkAnnot toLink()
    {
        return isLink() || isWidget() ? (PDFLinkAnnot) this : null;
    }

    public PDFWidgetAnnot toWidget()
    {
        return isWidget() ? (PDFWidgetAnnot) this : null;
    }

    public PDFText toText()
    {
        return isText() ? (PDFText) this : null;
    }

    public PDFPath toPath()
    {
        return isPath() ? (PDFPath) this : null;
    }

    public PDFClip toClip()
    {
        return isClip() ? (PDFClip) this : null;
    }

    public PDFImage toImage()
    {
        return isImage() ? (PDFImage) this : null;
    }

    public PDFPage toPage()
    {
        return isPage() ? (PDFPage) this : null;
    }

    public PDFContent toContent()
    {
        return isContent() ? (PDFContent) this : null;
    }

    public PDFResources toResources()
    {
        return isResources() ? (PDFResources) this : null;
    }

    public PDFDocument toDocument()
    {
        return isDocument() ? (PDFDocument) this : null;
    }

    public PDFBugOp toBadOp()
    {
        return isBadOp() ? (PDFBugOp) this : null;
    }

    public void paint(Graphics3 g, PDFDisplayProps properties)
    {
    }
}
