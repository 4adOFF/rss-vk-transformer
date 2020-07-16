package org.chadoff.rss_to_vk_transformer


import com.rometools.rome.feed.atom.Entry
import org.jdom2.Element
import org.jdom2.Namespace

class VkRssEntry() : Entry() {

    fun addImage(imageUrl: String) {
        val image = Element("image", Namespace.getNamespace("image", "http://web.resource.org/rss/1.0/modules/image/"))
        image.addContent(imageUrl)
        this.foreignMarkup.add(image)
    }
}