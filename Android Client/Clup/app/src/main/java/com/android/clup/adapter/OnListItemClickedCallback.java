package com.android.clup.adapter;

/**
 * A simple interface allowing an entity to be notified when a list item is clicked.
 */
public interface OnListItemClickedCallback {
    /**
     * Notifies the callee that an item in the list has been clicked. This item
     * is located in the list at the {@code position} position.
     *
     * @param position the position inside the list, where the clicked element is located.
     */
    void onListItemClicked(final int position);
}
