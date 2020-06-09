package com.example

import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.events.Characters
import javax.xml.stream.events.XMLEvent
import kotlin.collections.HashMap

/**
 * Reads and consumes an RSS [feed] from a given URL.
 */
class RSSReader(feedUrl: String) {

    private var url: URL? = null
    private var feed: Feed? = null

    init {
        try {
            this.url = URL(feedUrl)
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        }
        this.feed = buildFeed()
    }

    /**
     * Get all items
     */
    fun getAllFeeds(): MutableList<FeedItem> {
        return feed!!.items
    }

    /**
     * Gets the new state of the RSS feed and checks it against the current [Feed]. If the new state of the RSS feed
     * contains more episodes, swap the existing feed into the new feed. Return [true] if the swap was successful.
     */
    fun pollFeed(): Boolean {
        val currFeed = buildFeed()
        if (this.feed!!.items.size < currFeed!!.items.size) {
            this.feed = currFeed
            return true
        }
        return false
    }

    /**
     * Reads from the rss xml and builds a [Feed].
     */
    private fun buildFeed(): Feed? {
        var feed: Feed? = null
        try {
            var isFeedHeader = true

            // Initialize empty feed values
            var description = ""
            var title = ""
            var link = ""
            var language = ""
            var copyright = ""
            var author = ""
            var publishDate = ""
            var guid = ""

            // Create an XMLInputFactory
            val inputFactory = XMLInputFactory.newInstance()
            // Setup a new eventReader
            val input = read()
            val eventReader = inputFactory.createXMLEventReader(input)
            // Parse the RSS XML
            while (eventReader.hasNext()) {
                var event = eventReader.nextEvent()
                if (event.isStartElement) {
                    val localPart = event.asStartElement().name.localPart
                    when (localPart) {
                        "feed" -> {
                            if (isFeedHeader) {
                                isFeedHeader = false
                                feed = Feed(title, link, description, language,
                                        copyright, publishDate)
                            }
                            event = eventReader.nextEvent()
                        }
                        "title" -> title = getCharacterData(event, eventReader)
                        "description" -> description = getCharacterData(event, eventReader)
                        "link" -> link = getCharacterData(event, eventReader)
                        "guid" -> guid = getCharacterData(event, eventReader)
                        "language" -> language = getCharacterData(event, eventReader)
                        "author" -> author = getCharacterData(event, eventReader)
                        "published" -> publishDate = getCharacterData(event, eventReader)
                        "copyright" -> copyright = getCharacterData(event, eventReader)
                    }
                } else if (event.isEndElement) {
                    if (event.asEndElement().name.localPart === "entry") {
                        val newItem = FeedItem()
                        newItem.author = author
                        newItem.description = description
                        newItem.guid = guid
                        newItem.link = link
                        newItem.title = title
                        newItem.pubDate = publishDate
                        feed!!.items.add(newItem)
                        event = eventReader.nextEvent()
                        continue
                    }
                }
            }
            eventReader.close()
        } catch (e: XMLStreamException) {
            throw RuntimeException(e)
        }
        return feed
    }

    /**
     * Helper function to handle a single [XMLEvent].
     */
    @Throws(XMLStreamException::class)
    private fun getCharacterData(event: XMLEvent, eventReader: XMLEventReader): String {
        var event = event
        var result = ""
        event = eventReader.nextEvent()
        if (event is Characters) {
            result = event.asCharacters().data
        }
        return result
    }

    /**
     * Helper function to open input stream from RSS url.
     */
    private fun read(): InputStream {
        try {
            return url!!.openStream()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

}