# rss-vk-transformer
I have a channel on youtube. <br/>I'm too lazy to manually post every video from my channel to vk public page.
<br/>
We have rss feed from youtube https://www.youtube.com/feeds/videos.xml?channel_id=[channelId] 
```xml
<feed xmlns:yt="http://www.youtube.com/xml/schemas/2015" xmlns:media="http://search.yahoo.com/mrss/" xmlns="http://www.w3.org/2005/Atom">
<link rel="self" href="http://www.youtube.com/feeds/videos.xml?channel_id="/>
<id>yt:channel:</id>
<yt:channelId></yt:channelId>
<title></title>
<link rel="alternate" href="https://www.youtube.com/channel/"/>
    <author>
        <name></name>
        <uri></uri>
    </author>
    <published></published>
    <entry>
        <id>yt:video:</id>
        <yt:videoId></yt:videoId>
        <yt:channelId></yt:channelId>
        <title></title>
        <link rel="alternate" href="https://www.youtube.com/watch?v="/>
        <author>
            <name></name>
            <uri></uri>
        </author>
        <published></published>
        <updated></updated>
        <media:group>
            <media:title></media:title>
            <media:content url="https://www.youtube.com/v/XXX?version=3" type="application/x-shockwave-flash" width="640" height="390"/>
            <media:thumbnail url="https://i3.ytimg.com/vi/XXX/hqdefault.jpg" width="480" height="360"/>
            <media:description></media:description>
            <media:community>
                <media:starRating count="1" average="5.00" min="1" max="5"/>
                <media:statistics views="1"/>
            </media:community>
        </media:group>
    </entry>
    <!-- and many entry -->
</feed>
```
<br/>We need...  Hmmm... I don't know... <br/>
I can't found documentation about import rss feed in vk public page, but I have some answer from support vk. <br/>
Now I know sample format rss entry (for my goal) for import vk public page. 
```xml
<entry>
    <title>title for post</title>
    <link rel="video" href="https://www.youtube.com/watch?v=XXX"/> 
    <updated>2020-07-27T05:00:03Z</updated> 
    <image xmlns="http://web.resource.org/rss/1.0/modules/image/">https://i3.ytimg.com/vi/XXX/hqdefault.jpg</image>
    <description xmlns="">description many many symbols</description>
    <guid xmlns="">https://www.youtube.com/watch?v=XXX</guid>
</entry>
```

<br/>

_**title**_ - **used only article** mode for title article <br/>
_**link**_ - link to video or any web-resource, for post or article <br/>
_**image**_ - **used only article mode** preview for article <br/>
_**description**_ - text for post or article, CDATA don't work with post <br/>
_**guid**_ - preview for link to video

#HOW TO START 
You can start this app on [heroku](https://devcenter.heroku.com/articles/getting-started-with-gradle-on-heroku) <br/>
You required set var - YOUTUBE_CHANNEL_ID <br/>
<br/>
:black_joker:enjoy!:game_die:
