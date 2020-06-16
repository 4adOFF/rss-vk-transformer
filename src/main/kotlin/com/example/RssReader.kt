package com.example

import com.rometools.rome.feed.rss.Channel
import com.rometools.rome.feed.rss.Description
import com.rometools.rome.feed.rss.Item
import com.sun.xml.internal.stream.events.StartElementEvent
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.namespace.QName

import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.events.Characters
import javax.xml.stream.events.XMLEvent

/**
 * Reads and consumes an RSS [feed] from a given URL.
 */
class RSSReader(feedUrl: String) {

    private var url: URL? = null
    var feed: Channel = Channel()

    init {
        try {
            this.url = URL(feedUrl)
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        }
        this.feed = Channel()
    }

    /**
     * Get all items
     */
    fun getAllFeeds(): MutableList<Item> {
        return feed.items
    }

    /**
     * Gets the new state of the RSS feed and checks it against the current [Feed]. If the new state of the RSS feed
     * contains more episodes, swap the existing feed into the new feed. Return [true] if the swap was successful.
     */
    fun pollFeed(): Boolean {
        val currFeed = buildFeed()
        if (this.feed.items.size < currFeed.items.size) {
            this.feed = currFeed
            return true
        }
        return false
    }

    /**
     * Reads from the rss xml and builds a [Feed].
     */
    fun buildFeed(): Channel {
        try {
            var isFeedHeader = true

            // Initialize empty feed values
            var description = ""
            var title = ""
            var link = ""
            var pubDate = Date(0)

            val inputFactory = XMLInputFactory.newInstance()
            val input = read()
            val eventReader = inputFactory.createXMLEventReader(input)
            while (eventReader.hasNext()) {
                var event = eventReader.nextEvent()
                if (event.isStartElement) {
                    val localPart = event.asStartElement().name.localPart
                    when (localPart) {
                        "feed" -> {
                            if (isFeedHeader) {
                                isFeedHeader = false
                                feed.title = title
                                feed.link = link
                                feed.description = description
                                feed.pubDate = pubDate
                            }
                            event = eventReader.nextEvent()
                        }
                        "title" -> title = getCharacterData(event, eventReader)
                        "published" -> pubDate = parseDateString(getCharacterData(event, eventReader))
//                        "description" -> description = getCharacterData(event, eventReader)
                        "link" -> link = getLink(event)
                    }
                } else if (event.isEndElement) {
                    if (event.asEndElement().name.localPart === "entry") {
                        val newItem = Item()
                        val d = Description()
                        d.value = description
                        newItem.description = d
                        newItem.link = link
                        newItem.title = title
                        newItem.pubDate = pubDate
                        feed.items.add(newItem)
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

    private fun parseDateString(textDate: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(textDate)
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
     * Helper function to handle a single [XMLEvent].
     */
    @Throws(XMLStreamException::class)
    private fun getLink(event: XMLEvent): String {
        return (event as StartElementEvent).getAttributeByName(QName.valueOf("href")).value
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