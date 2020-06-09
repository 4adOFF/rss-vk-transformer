package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

@Controller
class Controller {

    @Value("\${spring.datasource.url}")
    private var dbUrl: String? = null
    @Value("\${youtube.channel.id}")
    private var youtubeChanelId: String? = null
    @Value("\${youtube.feed.url}")
    private var youtubeFeedUrl: String? = null

    @Autowired
    lateinit private var dataSource: DataSource

    @RequestMapping("/")
    internal fun index(): String {
        return "index"
    }

    @RequestMapping("/db")
    internal fun db(model: MutableMap<String, Any>): String {
        val connection = dataSource.getConnection()
        try {
            val stmt = connection.createStatement()
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
            stmt.executeUpdate("INSERT INTO ticks VALUES (now())")
            val rs = stmt.executeQuery("SELECT tick FROM ticks")

            val output = ArrayList<String>()
            while (rs.next()) {
                output.add("Read from DB: " + rs.getTimestamp("tick"))
            }

            model.put("records", output)
            return "db"
        } catch (e: Exception) {
            connection.close()
            model.put("message", e.message ?: "Unknown error")
            return "error"
        }

    }

    @RequestMapping("/rss/get")
    internal fun getRss(model: MutableMap<String, Any>): String {
        val output = ArrayList<String>()

        RSSReader(youtubeFeedUrl + youtubeChanelId).getNewestItem()

        model.put("records", output)
        return "getRss"

    }

    @Bean
    @Throws(SQLException::class)
    fun dataSource(): DataSource {
        if (dbUrl?.isEmpty() ?: true) {
            return HikariDataSource()
        } else {
            val config = HikariConfig()
            config.jdbcUrl = dbUrl
            return HikariDataSource(config)
        }
    }
}