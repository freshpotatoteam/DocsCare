package com.ddd.docscare.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T, VH : RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {

    private var list: ArrayList<T> = arrayListOf()

    abstract fun onBindView(holder: VH, item: T, position: Int)

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindView(holder, list[position], position)
    }

    override fun getItemCount(): Int = list.size

    fun getItem(): List<T> = list

    fun addAll(list: List<T>) {
        this.list.addAll(list)
    }

    fun add(data: T) {
        this.list.add(data)
    }

    fun removeAll() {
        this.list.clear()
    }

    fun remove(data: T) {
        this.list.remove(data)
    }
}