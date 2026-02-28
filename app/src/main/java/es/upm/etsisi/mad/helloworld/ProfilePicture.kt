package es.upm.etsisi.mad.helloworld

import android.content.Context
import java.io.IOException

/**
 * Class representing a profile picture.
 */
data class ProfilePicture(val name: String, val image: ByteArray)

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