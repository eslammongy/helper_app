package com.eslammongy.helper.helperfun

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Priority
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.eslammongy.helper.R


@GlideModule
class CustomGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        val memoryCacheSizeBytes = 60 * 1024 * 1024 // 20mb
        builder.setDefaultRequestOptions(requestOptions())
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, memoryCacheSizeBytes.toLong()))
       // builder.setDefaultTransitionOptions(DrawableTransitionOptions.withCrossFade())

    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        //val replace = glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }

    private fun requestOptions(): RequestOptions{
        return RequestOptions()
            .override(250, 250)
            .signature(ObjectKey(System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
            .centerInside()
            .priority(Priority.HIGH)
            .skipMemoryCache(true)
            .encodeFormat(Bitmap.CompressFormat.PNG)
            .encodeQuality(100)
            .error(R.drawable.work_on_time_image)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .format(DecodeFormat.PREFER_ARGB_8888)
    }

}
