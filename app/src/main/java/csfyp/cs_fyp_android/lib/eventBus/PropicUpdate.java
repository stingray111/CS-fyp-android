package csfyp.cs_fyp_android.lib.eventBus;

import java.io.File;

public class PropicUpdate {
    private String urlStr;
    private File file;

    public PropicUpdate(String urlStr) {
        this.urlStr = urlStr;
    }

    public PropicUpdate(File file) {
        this.file = file;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public File getFile() {
        return file;
    }
}
