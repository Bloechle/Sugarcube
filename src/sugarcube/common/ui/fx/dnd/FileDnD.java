package sugarcube.common.ui.fx.dnd;

import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import sugarcube.common.data.collections.Files3;
import sugarcube.common.data.collections.List3;
import sugarcube.common.system.io.File3;

import java.io.File;

public class FileDnD extends DnD<File3>
{

    public FileDnD(DragEvent event, File3... data)
    {
        super(event, data);
    }

    public boolean hasExt(String... ext)
    {
        return size() > 0 && value().isExt(ext);
    }

    public File3 file()
    {
        return this.value();
    }

    public File3[] files(String... exts)
    {
        return exts == null || exts.length == 0 ? data() : new Files3(data()).ext(exts).array();
    }

    public String[] filePaths()
    {
        File3[] files = data();
        String[] paths = new String[files.length];
        for (int i = 0; i < files.length; i++)
            paths[i] = files[i] == null ? "" : files[i].path();
        return paths;
    }

    public static void Handle(Scene scene, FileDroppable droppable)
    {
        scene.setOnDragOver(e ->
        {
            Dragboard db = e.getDragboard();
            if (db.hasFiles())
                e.acceptTransferModes(TransferMode.COPY);
        });

        // Dropping over surface
        scene.setOnDragDropped(e ->
        {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasFiles())
            {
                success = true;
                List3<File3> files = new List3<>();
                for (File file : db.getFiles())
                    files.add(new File3(file));
                if (files.isPopulated())
                    droppable.fileDropped(new FileDnD(e, files.toArray(new File3[0])));
            }
            e.setDropCompleted(success);
            e.consume();
        });
    }

}
