package pureplus;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ImageFileFilter extends FileFilter
{
	String[]  exts = {".png",".jpg",".jpeg",".jfif"};

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
    		return true;
    	}

		String  name = file.getName();
		for (int i=0; i<exts.length; i++) {
			if (name.endsWith(exts[i])) return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Just Image";
	}
}
