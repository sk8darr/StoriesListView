package com.unixsoft.storieslistview

import android.content.Context
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.unixsoft.storieslistview.utils.ItemOffsetDecoration

/**
 * @author sk8 on 14/02/19.
 */

class StoryListView : LinearLayout{

    interface OnMaxReached{
        fun onMaxItemsReached()
    }

    constructor(context: Context): super(context, null)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        initializeViews(context, attrs, R.attr.StoryListViewStyle)
    }
    constructor(context: Context, @Nullable attrs: AttributeSet, defStyleAttr: Int):super(context, attrs, defStyleAttr){
        initializeViews(context, attrs, defStyleAttr)
    }

    private var backgroundStoriesColor: Int = 0
    private var storyElevation: Float = 0F
    private var storyCornerRadius: Float = 0F
    private var maxItemCount: Int = 0
    private var addIconAsset: Int = 0
    private var addImageIconAsset: Int = 0
    private var removeIconAsset: Int = 0
    private var showButton1 = true
    private var showButton2 = true
    private var showRemoveButton = true
    private var autoMoveToLastImage = true
    private var items = ArrayList<String>()
    private var mAdapter: StoryAdapter? = null
    private var rv: RecyclerView? = null

    //Set listeners from class
    var onStoryClickListener: StoryAdapter.StoryClickListener? = null
        set(value) {
            field = value
            mAdapter?.storyClickListener = field
        }
    var onButtonsClickListener: StoryAdapter.ButtonsClickListener? = null
        set(value) {
            field = value
            mAdapter?.buttonsClickListener = field
        }
    var onMaxReached: OnMaxReached? = null

    private fun initializeViews(context: Context, attrs: AttributeSet, defStyleAttr: Int){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoryListView, defStyleAttr, 0)

        val defBgColor = ContextCompat.getColor(context, R.color.white)
        val defAddPhotoIcon = R.drawable.ic_add_a_photo_black_24dp
        val defAddImageIcon = R.drawable.ic_photo_library_black_24dp
        val defRemoveIcon= R.drawable.ic_close_black_24dp

        try {

            backgroundStoriesColor = typedArray.getColor(R.styleable.StoryListView_backgroundStoriesColor, defBgColor)
            storyElevation = typedArray.getDimension(R.styleable.StoryListView_storyElevation, 0F)
            storyCornerRadius = typedArray.getDimension(R.styleable.StoryListView_storyCornerRadius, 0F)
            maxItemCount = typedArray.getInt(R.styleable.StoryListView_maxItemCount, maxItemCount)
            addIconAsset = typedArray.getResourceId(R.styleable.StoryListView_addPhotoIconAsset, defAddPhotoIcon)
            addImageIconAsset = typedArray.getResourceId(R.styleable.StoryListView_addImageIconAsset, defAddImageIcon)
            removeIconAsset = typedArray.getResourceId(R.styleable.StoryListView_removeIconAsset, defRemoveIcon)
            showButton1 = typedArray.getBoolean(R.styleable.StoryListView_showButton1, showButton1)
            showButton2 = typedArray.getBoolean(R.styleable.StoryListView_showButton2, showButton2)
            showRemoveButton = typedArray.getBoolean(R.styleable.StoryListView_showRemoveButton, showRemoveButton)
            autoMoveToLastImage = typedArray.getBoolean(R.styleable.StoryListView_autoMoveToLastImage, autoMoveToLastImage)

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.story_layout, this, true)
            rv = view?.findViewById(R.id.rv)
            rv?.apply{
                mAdapter = StoryAdapter(context, items, setPreferencesToItem())
                adapter = mAdapter
                setHasFixedSize(true)
                addItemDecoration(
                    ItemOffsetDecoration(
                        context,
                        R.dimen.story_divisor
                    )
                )
//                itemAnimator = SlideInLeftAnimator()
                setBackgroundColor(backgroundStoriesColor)
            }
        } finally {
            typedArray.recycle()
        }
    }

    private fun setPreferencesToItem(): Bundle{
       return Bundle().apply{
           putFloat(KEY_ELEVATION, storyElevation)
           putFloat(KEY_RADIUS, storyCornerRadius)
           putInt(KEY_MAX_ITEMS, maxItemCount)
           putInt(KEY_ADD_PHOTO_ICON_ASSET, addIconAsset)
           putInt(KEY_ADD_IMAGE_ICON_ASSET, addImageIconAsset)
           putInt(KEY_REMOVE_ICON_ASSET, removeIconAsset)
           putBoolean(KEY_SHOW_BUTTON1, showButton1)
           putBoolean(KEY_SHOW_BUTTON2, showButton2)
           putBoolean(KEY_SHOW_REMOVE_BTN, showRemoveButton)
       }
    }

    fun addImage(path: String){
        if(maxItemCount == 0 || items.size < maxItemCount) {
            items.add(path)
            mAdapter?.notifyDataSetChanged()
            if (autoMoveToLastImage) {
                rv?.smoothScrollToPosition(items.size)
            }
            if(maxItemCount == items.size){
                onMaxReached?.onMaxItemsReached()
            }
        } else {
            onMaxReached?.onMaxItemsReached()
        }
    }

    fun removeImage(position: Int) {
        if (items.size > position) {
            items.removeAt(position)
            mAdapter?.notifyItemRemoved(position)
        }
    }
}