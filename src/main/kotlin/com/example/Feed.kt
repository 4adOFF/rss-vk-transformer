package com.example

import org.springframework.stereotype.Component
import java.util.ArrayList

/**
 * Data class for an RSS feed.
 */
data class Feed(val title: String,
                val link: String,
                val description: String,
                val language: String,
                val copyright: String,
                val pubDate: String) {

    var items: MutableList<FeedItem> = ArrayList()
}