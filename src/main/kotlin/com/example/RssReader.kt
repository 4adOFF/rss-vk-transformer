package com.example

import com.rometools.rome.feed.atom.Content
import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Feed
import com.rometools.rome.feed.atom.Link
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
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.Characters
import javax.xml.stream.events.XMLEvent


/**
 * Reads and consumes an RSS [feed] from a given URL.
 */
class RSSReader(feedUrl: String) {

    private var url: URL? = null
    var feed: Feed = Feed()

    init {
        try {
            this.url = URL(feedUrl)
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        }
        this.feed = Feed()
    }

    fun getAllFeeds(): MutableList<Entry> {
        return feed.entries
    }

    fun pollFeed(): Boolean {
        val currFeed = buildFeed()
        if (this.feed.entries.size < currFeed.entries.size) {
            this.feed = currFeed
            return true
        }
        return false
    }

    fun buildFeed(): Feed {
        try {
            var isFeedHeader = true

            // Initialize empty feed values
            var description = ""
            var title = ""
            var link = ""
            var imageUrl = ""
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
                                    feed.updated = pubDate
                                }
                                event = eventReader.nextEvent()
                            }
                            "title" -> title = getCharacterData(event, eventReader)
                            "published" -> pubDate = parseDateString(getCharacterData(event, eventReader))
                            "description" -> description = getCharacterData(event, eventReader)
                            "thumbnail" -> imageUrl = getAttrValueByName(event, "url")
                            "link" -> link = getAttrValueByName(event, "href")
                        }
                    } else if (event.isEndElement) {
                        if (event.asEndElement().name.localPart === "entry") {
                            val newEntry = Entry()
                            newEntry.title = title
                            newEntry.updated = pubDate
                            val otherlinks: MutableList<Link> = ArrayList()
                            newEntry.otherLinks = otherlinks
                            val videolink = Link()
                            videolink.rel = "video"
                            videolink.href = link
                            val imglink = Link()
                            imglink.rel = "image"
                            imglink.href = imageUrl
                            otherlinks.add(imglink)
                            otherlinks.add(videolink)
                            feed.entries.add(newEntry)
                            event = eventReader.nextEvent()
                            continue
                        }
                    }
                }
            eventReader.close()
            feed.entries.add(generateTestEntry(link, imageUrl))
        } catch (e: XMLStreamException) {
            throw RuntimeException(e)
        }
        return feed
    }

    //todo delete after testing
    private fun generateTestEntry(link: String, imageUrl: String): Entry {

        val testEntry = Entry()
        testEntry.title = "test title" + Random().nextInt(1000)
        testEntry.updated = Date()
        val otherlinks: MutableList<Link> = ArrayList()
        testEntry.otherLinks = otherlinks
        val videolink = Link()
        videolink.rel = "video"
        videolink.href = link
        val imglink = Link()
        imglink.rel = "image"
        imglink.href = imageUrl
        otherlinks.add(imglink)
        otherlinks.add(videolink)

        val c = Content()
        c.type = "image"
        c.src = imageUrl
        val contents: MutableList<Content> = ArrayList()
        contents.add(c)
        testEntry.contents = contents

        return testEntry
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

    private fun getAttrByName(event: XMLEvent, attrName: String): Attribute {
        return (event as StartElementEvent).getAttributeByName(QName.valueOf(attrName))
    }

    private fun getAttrValueByName(event: XMLEvent, attrName: String): String {
        return getAttrByName(event, attrName).value
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