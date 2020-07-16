package com.example

import com.rometools.rome.feed.atom.Content
import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Feed
import com.rometools.rome.feed.atom.Link
import com.rometools.rome.feed.rss.Image
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
                            val newEntry = VkRssEntry()
                            newEntry.title = title
                            newEntry.updated = pubDate
                            val c = Content()
                            c.value = description
                            val contents: MutableList<Content> = ArrayList()
                            contents.add(c)
                            newEntry.contents = contents
                            val otherlinks: MutableList<Link> = ArrayList()
                            newEntry.otherLinks = otherlinks
                            val videolink = Link()
                            videolink.rel = "video"
                            videolink.href = link
                            newEntry.addImage(imageUrl)
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
    private fun generateTestEntry(link: String, imageUrl: String): VkRssEntry {
        val testEntry = VkRssEntry()
        testEntry.title = "test title" + Random().nextInt(10000)
        testEntry.issued = Date()
        val otherlinks: MutableList<Link> = ArrayList()
        testEntry.otherLinks = otherlinks
        val videolink = Link()
        videolink.rel = "video"
        videolink.href = link
        otherlinks.add(videolink)
        val img = Image()
        img.url = imageUrl
        img.title = "test image title"
        testEntry.image = img

        return testEntry
    }

    private fun parseDateString(textDate: String): Date {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(textDate)
    }

    @Throws(XMLStreamException::class)
    private fun getCharacterData(event: XMLEvent, eventReader: XMLEventReader): String {
        var result = ""
        var tEvent = event
        tEvent = eventReader.nextEvent()
        while (!tEvent.isEndElement) {
            if (tEvent is Characters) {
                result += tEvent.asCharacters().data
                tEvent = eventReader.nextEvent()
            }
        }
        return result
    }

    private fun getAttrByName(event: XMLEvent, attrName: String): Attribute {
        return (event as StartElementEvent).getAttributeByName(QName.valueOf(attrName))
    }

    private fun getAttrValueByName(event: XMLEvent, attrName: String): String {
        return getAttrByName(event, attrName).value
    }

    private fun read(): InputStream {
        try {
            return url!!.openStream()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

}