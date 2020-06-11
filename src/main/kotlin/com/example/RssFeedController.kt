package com.example

import com.rometools.rome.feed.rss.Channel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class RssFeedController {

    @Value("\${youtube.channel.id}")
    private var youtubeChannelIdDefault: String? = null
    @Value("\${youtube.feed.url}")
    private var youtubeFeedUrl: String? = null

    @Autowired
    private val view: RssFeedView = rssFeedView()

    @RequestMapping("/rss/get")
    internal fun getRss(model: MutableMap<String, Any>, @RequestParam(name = "ytId", required = false) youtubeChannelId: String): RssFeedView? {
        var channelId = youtubeChannelIdDefault
        if (youtubeChannelId.isNotBlank()) {
            channelId = youtubeChannelId
        }

        val reader = RSSReader(youtubeFeedUrl + channelId)
        reader.pollFeed()

        view.updateFeed(reader.buildFeed())

        return view

    }

    @Bean
    fun rssFeedView(): RssFeedView {
        return RssFeedView(Channel())
    }
}