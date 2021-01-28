package com.android.clup.adapter;

/**
 * A simple interface allowing an entity to be notified when a {@code RecyclerView} item is clicked.
 */
public interface OnRecyclerViewItemClickedCallback {
    /**
     * Notifies the callee that an item in the {@code RecyclerView} has been clicked. This item
     * is located in the {@code RecyclerView} in {@code checkedItemPosition} position.
     *
     * @param position the position inside the {@code RecyclerView}, where the clicked
     *                 element is located.
     */
    void onRecyclerViewItemClicked(final int position);
}
