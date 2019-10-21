package com.jagerdev.foxhoundpricetracker.products.selector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jagerdev.foxhoundpricetracker.R;

import java.util.ArrayList;
import java.util.List;

import tracker.PriceTrackerManager;


interface TagSelectedListener {
    void tagSelected(Tag selectedTag);
    void tagRemoved(Tag removedTag);
}

public class TagManager implements TextWatcher, TagSelectedListener {

    public TagManager(Activity context, PriceTrackerManager priceTrackerManager, TagChangeEvents tagChangeHandler, String title, List<Tag> savedSelectedTags)
    {
        this.context = context;
        this.tags = new ArrayList<>();
        this.selectedTags = savedSelectedTags;
        this.priceTrackerManager = priceTrackerManager;
        this.tagChangeHandler = tagChangeHandler;
        this.title = title;
        this.selectDialog = new Dialog(context);
        this.selectDialog.setContentView(R.layout.tag_select_popup);
        loadTags();
        this.tagSelectorAdapter = new TagSelectorAdapter(context, tags, this);

    }

    public TagManager(Activity context, PriceTrackerManager priceTrackerManager, TagChangeEvents tagChangeHandler, String title)
    {
        this(context, priceTrackerManager, tagChangeHandler, title, new ArrayList<Tag>());
    }

    private String title;
    private TagSelectorAdapter tagSelectorAdapter;
    private List<Tag> tags;
    private List<Tag> selectedTags;
    private PriceTrackerManager priceTrackerManager;
    private TagChangeEvents tagChangeHandler;
    private Context context;
    private TextView tagPanelTitle;
    private EditText searchTags;
    private ImageButton btnAddNewTag;
    private ChipGroup selectedTagsGroup;
    private RecyclerView existingTags;
    private Dialog selectDialog;

    private View.OnClickListener onTagCloseHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            removeSelectedTag(v);
        }
    };

    private View.OnClickListener addNewTag = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tag tag = new Tag(searchTags.getText().toString());
            if (!tags.contains(tag))
            {
                registerNewTag(tag);
            }
        }
    };

    public List<Tag> getSelectedTags()
    {
        return selectedTags;
    }

    public List<String> getSelectedTagNames()
    {
        List<String> tagNames = new ArrayList<>();
        for (Tag tag : selectedTags) tagNames.add(tag.getTagName());
        return tagNames;
    }

    public List<Tag> getAllTags()
    {
        return tags;
    }

    private void loadTags()
    {
        tags.clear();
        List<String> tagNames = priceTrackerManager.getAllTags();
        for(String tagName : tagNames) tags.add(new Tag(tagName));
    }

    private void removeSelectedTag(View v) {
        String tagName = ((Chip)v).getText().toString();
        selectedTagsGroup.removeView(v);
        Tag tag = new Tag(tagName);
        selectedTags.remove(tag);
        tagChangeHandler.chosenTagRemoved(tag);
    }

    private void filterSearchedTags(List<Tag> allTags, String searchString) {
        List<Tag> matchingTags = new ArrayList<>();
        for (Tag tag : allTags)
            if (tag.getTagName().toLowerCase().contains(searchString.toLowerCase()))
                matchingTags.add(tag);

        if (matchingTags.size() == 0) matchingTags.add(new Tag(searchString));
        tagSelectorAdapter.updateAdapterItems(matchingTags);
    }

    public Chip getSelectedTagChip(Tag tag, ChipGroup tagsGroup)
    {
        Chip tagChip;
        for (int i = 0; i < tagsGroup.getChildCount(); i++)
        {
            tagChip = (Chip)tagsGroup.getChildAt(i);
            if (new Tag(tagChip.getText().toString()).equals(tag)) return tagChip;
        }
        return null;
    }

    private void removeTag(Tag removedTag)
    {
        tags.remove(removedTag);
        priceTrackerManager.removeTag(removedTag.getTagName());
        Chip tagChip = getSelectedTagChip(removedTag, selectedTagsGroup);
        if (tagChip != null) removeSelectedTag(tagChip);

        filterSearchedTags(tags, searchTags.getText().toString());
        tagChangeHandler.tagDeleted(removedTag);
        Toast.makeText(context, String.format("Tag removed: %s", removedTag.getTagName()), Toast.LENGTH_SHORT).show();
    }


    private boolean isTagAlreadySelected(Tag tag)
    {
        Chip tagView;
        for (int i = 0; i < selectedTagsGroup.getChildCount(); i++) {
            tagView = (Chip) selectedTagsGroup.getChildAt(i);
            if (tagView.getText().toString().toLowerCase().equals(tag.getTagName().toLowerCase())) return true;
        }
        return false;
    }


    private void removeTagDialog(final Tag removedTag)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        removeTag(removedTag);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(String.format("Do you want to delete '%s' tag?", removedTag.getTagName())).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void showSelectDialog()
    {
        showSelectDialog(new ArrayList<Tag>());
    }

    public void showSelectDialog(List<Tag> alreadySelectedTags)
    {
        selectedTags = alreadySelectedTags;

        selectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectedTagsGroup = selectDialog.findViewById(R.id.chip_group);
        searchTags = selectDialog.findViewById(R.id.edit_search_tags);
        btnAddNewTag = selectDialog.findViewById(R.id.btn_add_new_tag);
        tagPanelTitle = selectDialog.findViewById(R.id.txt_tag_panel_title);
        tagPanelTitle.setText(title);

        selectedTagsGroup.removeAllViews();
        for (Tag t : selectedTags) addChipToGroup(t);   // Load saved tags
        btnAddNewTag.setOnClickListener(addNewTag);
        searchTags.addTextChangedListener(this);
        existingTags = selectDialog.findViewById(R.id.existing_tags);
        existingTags.setLayoutManager(new LinearLayoutManager(context));
        existingTags.setHasFixedSize(true);
        existingTags.setAdapter(tagSelectorAdapter);
        tagSelectorAdapter.notifyDataSetChanged();
        selectDialog.show();
    }

    @Override
    public void tagRemoved(Tag removedTag) {
        tagChangeHandler.chosenTagRemoved(removedTag);
        if (tags.contains(removedTag))
        {
            removeTagDialog(removedTag);
        }
        else Toast.makeText(context, String.format("Tag is not registered yet: %s", removedTag.getTagName()), Toast.LENGTH_SHORT).show();
    }

    private void addChipToGroup(Tag tagToAdd)
    {
        Chip tag = new Chip(context);
        tag.setText(tagToAdd.getTagName());
        tag.setCloseIconVisible(true);
        tag.setCheckable(false);
        tag.setClickable(false);
        tag.setOnCloseIconClickListener(onTagCloseHandler);
        selectedTagsGroup.addView(tag);
    }

    @Override
    public void tagSelected(Tag selectedTag) {
        if (!tags.contains(selectedTag))
        {
            registerNewTag(selectedTag);
        }

        if (!isTagAlreadySelected(selectedTag)) {
            addChipToGroup(selectedTag);
            selectedTags.add(selectedTag);
            tagChangeHandler.tagChosen(selectedTag, false);
        }
        else tagChangeHandler.tagChosen(selectedTag, true);
    }

    private void registerNewTag(Tag newTag)
    {
        if (newTag.getTagName().equals("")) return;
        priceTrackerManager.insertNewTag(newTag.getTagName());
        tags.add(newTag);
        tagSelectorAdapter.updateAdapterItems(tags);
        filterSearchedTags(tags, searchTags.getText().toString());
        Toast.makeText(context, String.format("New tag registered: %s", newTag.getTagName()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterSearchedTags(tags, s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
