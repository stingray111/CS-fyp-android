package csfyp.cs_fyp_android.lib;

import com.cunoraz.tagview.Tag;


public class CustomTag {
    private Tag tag;

    public CustomTag(int id, String text, int layoutColor) {
        tag = new Tag(text);
        tag.id = id;
        tag.layoutColor = layoutColor;
    }

    public Tag getTag() {
        return tag;
    }
}
