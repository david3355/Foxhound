package com.jagerdev.foxhoundpricetracker.products.selector;

import androidx.annotation.Nullable;

public class Tag {

    private String tagName;

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (obj instanceof Tag)
        {
            Tag matcher = (Tag)obj;
            return this.tagName.toLowerCase().equals(matcher.tagName.toLowerCase());
        }
        return false;
    }
}
