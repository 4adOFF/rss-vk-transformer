package org.chadoff.rss_to_vk_transformer


import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Link
import org.jdom2.Element
import org.jdom2.Namespace

class VkRssEntry() : Entry() {

    fun addDescription(description: String, imageUrl: String) {
        addImage(imageUrl)
        val desc = Element("description")
        val d = description.replace("\n","<br/>")
        val templateCDATAImg = "<![CDATA[$d]]>"
        desc.addContent(templateCDATAImg)
        this.foreignMarkup.add(desc)
    }

    fun addVideoLinkAndGuid(link: String) {
        val otherLinks: MutableList<Link> = ArrayList()
        val videoLink = Link()
        videoLink.rel = "video"
        videoLink.href = link
        otherLinks.add(videoLink)
        this.otherLinks = otherLinks
        val desc = Element("guid")
        desc.addContent(link)
        this.foreignMarkup.add(desc)
    }

    private fun addImage(imageUrl: String) {
        val image = Element("image", Namespace.getNamespace("http://web.resource.org/rss/1.0/modules/image/"))
        image.addContent(imageUrl)
        this.foreignMarkup.add(image)
    }
}