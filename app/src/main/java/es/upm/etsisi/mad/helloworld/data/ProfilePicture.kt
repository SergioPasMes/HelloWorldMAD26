package es.upm.etsisi.mad.helloworld.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import java.io.IOException

/**
 * Class representing a profile picture.
 */
data class ProfilePicture(val name: String, val image: ByteArray) {
    /**
     * Returns the contents of this image as a Bitmap.
     */
    fun toBitmap(): Bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
}

/**
 * Allows for easy access to buil-in profile pictures.
 */
object ProfilePictureManager {

    // Subdirectory inside the `assets` directory.
    private const val PFP_PATH = "pfp"

    /**
     * Returns a list of paths to all built-in profile pictures.
     */
    fun getAllImages(context: Context): List<ProfilePicture> {
        val images = context.assets.list(PFP_PATH)
            ?.map { Pair(it, "$PFP_PATH/$it") }
            ?.map { (name, path) -> Pair(name, context.assets.open(path))}
            ?.map { (name, asset) -> ProfilePicture(name, asset.readBytes()) }

        return images ?: listOf()
    }

    /**
     * Loads a single image, specified by `name`.
     */
    fun getImage(context: Context, name: String): ProfilePicture? {
        try {
            val image = context.assets.open("$PFP_PATH/$name").readBytes()
            return ProfilePicture(name, image)
        } catch(exception: IOException) {
            return null
        }
    }
}

/**
 * Helper class for showing a profile picture picker.
 *
 * @param imageList list of images to show.
 * @param onImageSelect callback called when the user selects an image. Not called if the dialog
 * is canceled.
 */
class ProfilePicturePickerManager(
    private val context: Context,
    private val imageList: List<ProfilePicture>,
    private val onImageSelect: ((ProfilePicture) -> Unit)? = null
) {

    /**
     * Shows the alert dialog.
     *
     * If the user selects a picture, `onImageSelect` is called.
     */
    fun show() {
        val dialogBuilder = AlertDialog.Builder(context)
        val layout = LayoutInflater.from(context)
            .inflate(_root_ide_package_.es.upm.etsisi.mad.helloworld.R.layout.alert_pfp_picker_layout, null)

        val dialog = dialogBuilder
            .setCancelable(true)
            .setView(layout)
            .create()

        val gridView = layout.findViewById<GridView>(_root_ide_package_.es.upm.etsisi.mad.helloworld.R.id.pfp_gridview)
        // Close dialog when an image is selected.
        gridView.adapter = GridViewImageAdapter(context, imageList) { image ->
            dialog.dismiss()
            onImageSelect?.invoke(image)
        }

        dialog.show()
    }

    private class GridViewImageAdapter(
        context: Context,
        private val imageList: List<ProfilePicture>,
        private val onImageSelect: ((ProfilePicture) -> Unit)?
    ): android.widget.ArrayAdapter<ProfilePicture>(context, _root_ide_package_.es.upm.etsisi.mad.helloworld.R.layout.alert_pfp_picker_image_layout, imageList) {

        private val inflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val image = imageList[position]

            val layout = convertView ?: inflater.inflate(_root_ide_package_.es.upm.etsisi.mad.helloworld.R.layout.alert_pfp_picker_image_layout, null)
            layout.setOnClickListener { onImageSelect?.invoke(image) }

            val imageView = layout.findViewById<ImageView>(_root_ide_package_.es.upm.etsisi.mad.helloworld.R.id.pfp_imageview)
            imageView.setImageBitmap(image.toBitmap())

            return layout
        }
    }
}