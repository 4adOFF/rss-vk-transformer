package com.example

import com.rometools.rome.feed.rss.Channel
import com.rometools.rome.feed.rss.Item
import org.springframework.web.servlet.view.feed.AbstractRssFeedView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RssFeedView(var feedDescription: Channel) : AbstractRssFeedView() {

    override fun buildFeedMetadata(model: Map<String?, Any?>?,
                                   feed: Channel, request: HttpServletRequest?) {
        feed.title = feedDescription.title
        feed.description = feedDescription.description
        feed.link = feedDescription.link
        feed.feedType = "rss_2.0"
    }

    override fun buildFeedItems(model: Map<String?, Any?>?,
                                request: HttpServletRequest?, response: HttpServletResponse?): List<Item?>? {
        return feedDescription.items
    }

    fun updateFeed(newFeed: Channel) {
        feedDescription = newFeed
    }
}