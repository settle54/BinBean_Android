package com.binbean.admin.model

import android.net.Uri

sealed class PhotoItem {
    data class Local(val uri: Uri) : PhotoItem()
    data class Remote(val url: String) : PhotoItem()
}
