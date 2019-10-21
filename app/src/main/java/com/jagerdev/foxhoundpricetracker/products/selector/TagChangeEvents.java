package com.jagerdev.foxhoundpricetracker.products.selector;

public interface TagChangeEvents {
    void tagChosen(Tag chosenTag, boolean alreadySelected);
    void chosenTagRemoved(Tag removedTag);
    void tagDeleted(Tag deletedTag);
}
