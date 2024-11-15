package sugarcube.insight.ribbon.actions.annot;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import sugarcube.common.data.collections.List3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.dialogs.FxWindow;
import sugarcube.insight.core.IS;
import sugarcube.insight.ribbon.insert.InsertRibbon;
import sugarcube.formats.ocd.objects.OCDAnnot;
import sugarcube.formats.pdf.resources.icons.Icon;

public class AnnotDialog extends FxWindow
{
  public static List3<AnnotItem> ITEMS = new List3<>();

  static
  {
    Add(OCDAnnot.TYPE_LINK, null);
    Add(OCDAnnot.TYPE_QUIZ, null);
    Add(OCDAnnot.TYPE_LAYOUT, OCDAnnot.CLASS_HEADER);
    Add(OCDAnnot.TYPE_LAYOUT, OCDAnnot.CLASS_FOOTER);
    Add(OCDAnnot.TYPE_LAYOUT, OCDAnnot.CLASS_FIXED);
    Add(OCDAnnot.TYPE_LAYOUT, OCDAnnot.CLASS_IGNORE);
  }

  private @FXML BorderPane leftPane;
  private @FXML BorderPane rightPane;
  private @FXML ListView<AnnotItem> annotList;

  private InsertRibbon tab;

  public AnnotDialog(InsertRibbon tab)
  {
    super("Insert Annotation", true, tab.window());
    this.tab = tab;
    IS.InsightCSS(windowPane);
    
    this.icon(Icon.Image(Icon.EDIT, 48, IS.GREEN_LIGHT));
    this.minSize(400, 300);
    this.noModality();
//    IT.DarkRoot(root);

    this.annotList.getItems().addAll(ITEMS);
    Fx.Fluent(annotList).multiple(false).cell(list -> new AnnotListCell(this)).listen((obs, old, val) -> itemSelected((AnnotItem) val));
    // this.onClose(e -> {
    // tab.update();
    // });

    this.dnd();
    this.show();
  }

  private static void Add(String type, String className)
  {
    ITEMS.add(new AnnotItem(type, className));
  }

  public void itemSelected(AnnotItem item)
  {
    if (item == null)
      return;

    OCDAnnot annot = tab.insertAnnot(null, item.type);
    if (item.hasClassname())
      annot.setClassname(item.className);
//    tab.refreshObjectPane();
    tab.refresh();
  }

  public static void Show(InsertRibbon tab)
  {
    AnnotDialog dialog = new AnnotDialog(tab);
    dialog.setOnClose(() -> {
      tab.refresh();
    });
  }
}
