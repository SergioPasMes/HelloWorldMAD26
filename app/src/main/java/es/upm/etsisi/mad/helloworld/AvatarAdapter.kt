package es.upm.etsisi.mad.helloworld

class AvatarAdapter(private val context: android.content.Context, private val imageIds: List<Int>) : android.widget.BaseAdapter() {
    override fun getCount() = imageIds.size
    override fun getItem(p: Int) = imageIds[p]
    override fun getItemId(p: Int) = p.toLong()

    override fun getView(p: Int, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View {
        val img = (convertView as? android.widget.ImageView) ?: android.widget.ImageView(context).apply {
            layoutParams = android.widget.AbsListView.LayoutParams(250, 250) // Size in pixels
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
        }
        img.setImageResource(imageIds[p])
        return img
    }
}