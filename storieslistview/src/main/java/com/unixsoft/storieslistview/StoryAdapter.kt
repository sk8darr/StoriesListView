package com.unixsoft.storieslistview

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_add.view.*
import kotlinx.android.synthetic.main.item_story_card.view.*

/**
 * @author sk8 on 13/02/19.
 */

class StoryAdapter (private val context: Context, private val items: List<String>, prefs: Bundle):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val VIEW_TYPE_ADD = 0
    private val VIEW_TYPE_IMAGE = 1
    private var elevation = 0f
    private var cornerRadius = 0f
    private var maxItems = 0
    private var addPhotoIcon = 0
    private var addImageIcon = 0
    private var removeIcon = 0
    private var showBtn1 = true
    private var showBtn2 = true
    private var showRemoveBtn = true
    private var readyForRemove = true
    var storyClickListener: StoryClickListener? = null
    var buttonsClickListener: ButtonsClickListener? = null


    interface StoryClickListener{
        fun onImageClick(url: String, position: Int)
        fun onImageRemoved(item: String, position: Int)
    }

    interface ButtonsClickListener{
        fun onButton1Click()
        fun onButton2Click()
    }

    init {
        elevation = prefs.getFloat(KEY_ELEVATION)
        cornerRadius = prefs.getFloat(KEY_RADIUS)
        maxItems = prefs.getInt(KEY_MAX_ITEMS)
        addPhotoIcon = prefs.getInt(KEY_ADD_PHOTO_ICON_ASSET)
        addImageIcon = prefs.getInt(KEY_ADD_IMAGE_ICON_ASSET)
        removeIcon = prefs.getInt(KEY_REMOVE_ICON_ASSET)
        showBtn1 = prefs.getBoolean(KEY_SHOW_BUTTON1)
        showBtn2 = prefs.getBoolean(KEY_SHOW_BUTTON2)
        showRemoveBtn = prefs.getBoolean(KEY_SHOW_REMOVE_BTN)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){
            VIEW_TYPE_ADD -> AddViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_add, parent, false))
            else -> StoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_story_card, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if(maxItems == 0 || items.size < maxItems){
            items.size + 1
        } else {
            items.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == items.size) VIEW_TYPE_ADD  else  VIEW_TYPE_IMAGE
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {

        if(VIEW_TYPE_IMAGE == h.itemViewType){
            val holder = h as StoryViewHolder
            holder.cardView.cardElevation = elevation
            holder.cardView.radius = cornerRadius
            holder.imageV.setOnClickListener{
                if(items.isNotEmpty() && items.size > position)
                    storyClickListener?.onImageClick(items[position], holder.adapterPosition)
            }
            holder.btnDelete?.visibility = if(showRemoveBtn) View.VISIBLE else View.GONE
            holder.btnDelete?.setImageDrawable(ContextCompat.getDrawable(context, removeIcon))
            holder.btnDelete?.setOnClickListener {
                storyClickListener?.onImageRemoved(removeItem(holder.adapterPosition), holder.adapterPosition)
            }
            GlideApp.with(context)
                .load(items[position])
                .centerCrop()
                .into(holder.imageV)
        } else if(VIEW_TYPE_ADD == h.itemViewType){
            val holder = h as AddViewHolder
            holder.itemView.ib_btn1.visibility = if(showBtn1) View.VISIBLE else View.GONE
            holder.itemView.ib_btn2.visibility = if(showBtn2) View.VISIBLE else View.GONE
            holder.itemView.ib_btn1.setImageDrawable(ContextCompat.getDrawable(context, addPhotoIcon))
            holder.itemView.ib_btn2.setImageDrawable(ContextCompat.getDrawable(context, addImageIcon))
            holder.itemView.ib_btn1.setOnClickListener { buttonsClickListener?.onButton1Click() }
            holder.itemView.ib_btn2.setOnClickListener { buttonsClickListener?.onButton2Click() }
        }
    }

    private fun removeItem(position: Int): String{
        return if(readyForRemove && position < items.size && position >= 0) {
            readyForRemove = false
            val item = items[position]
            (items as ArrayList<String>).removeAt(position)
            notifyItemRemoved(position)
            readyForRemove = true
            item
        } else {
            ""
        }
    }

    class StoryViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageV: AppCompatImageView = view.findViewById(R.id.iv_bg)
        val btnDelete: AppCompatImageButton? = view.ib_delete
        val cardView: CardView = view.card_view
    }

    class AddViewHolder(view:View): RecyclerView.ViewHolder(view)
}