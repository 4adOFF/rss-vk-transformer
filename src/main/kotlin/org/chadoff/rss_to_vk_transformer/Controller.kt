package org.chadoff.rss_to_vk_transformer

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class Controller {

    @RequestMapping("/")
    internal fun index(): String {
        return "index"
    }
}