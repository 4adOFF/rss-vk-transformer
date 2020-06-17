package com.example

import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Feed
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RssFeedView(var feedDescription: Feed) : AbstractAtomFeedView() {

    override fun buildFeedEntries(model: MutableMap<String, Any>?, request: HttpServletRequest?, response: HttpServletResponse?): MutableList<Entry> {
        return feedDescription.entries
    }

    override fun buildFeedMetadata(model: Map<String?, Any?>?,
                                   feed: Feed, request: HttpServletRequest?) {
        feed.title = feedDescription.title
        this.contentType = "text/xml; charset=UTF-8"
    }

    fun updateFeed(newFeed: Feed) {
        feedDescription = newFeed
    }
}