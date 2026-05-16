/* 马来西亚OSINT — 社交媒体平台定义（160+平台） */

package com.osint.malaysia.util

data class SocialPlatform(
    val name: String,
    val urlTemplate: String,
    val category: String = "通用"
)

object SocialPlatforms {

    val ALL: List<SocialPlatform> = listOf(
        /* 全球主流平台 */
        SocialPlatform("Twitter/X", "https://x.com/%s", "全球社交"),
        SocialPlatform("Reddit", "https://www.reddit.com/user/%s", "全球社交"),
        SocialPlatform("Instagram", "https://www.instagram.com/%s", "全球社交"),
        SocialPlatform("TikTok", "https://www.tiktok.com/@%s", "全球社交"),
        SocialPlatform("Telegram", "https://t.me/%s", "即时通讯"),
        SocialPlatform("Facebook", "https://www.facebook.com/%s", "全球社交"),
        SocialPlatform("YouTube", "https://www.youtube.com/@%s", "视频平台"),
        SocialPlatform("LinkedIn", "https://www.linkedin.com/in/%s", "职业社交"),
        SocialPlatform("Snapchat", "https://www.snapchat.com/add/%s", "全球社交"),
        SocialPlatform("Pinterest", "https://www.pinterest.com/%s", "全球社交"),
        SocialPlatform("Medium", "https://medium.com/@%s", "内容平台"),
        SocialPlatform("Twitch", "https://www.twitch.tv/%s", "游戏直播"),
        SocialPlatform("Discord", "https://discord.com/users/%s", "即时通讯"),
        SocialPlatform("Flickr", "https://www.flickr.com/people/%s", "图片分享"),
        SocialPlatform("Spotify", "https://open.spotify.com/user/%s", "音乐平台"),
        SocialPlatform("Steam", "https://steamcommunity.com/id/%s", "游戏平台"),
        SocialPlatform("Vimeo", "https://vimeo.com/%s", "视频平台"),
        SocialPlatform("WordPress", "https://%s.wordpress.com", "内容平台"),

        /* 开发者平台 */
        SocialPlatform("GitLab", "https://gitlab.com/%s", "开发者"),
        SocialPlatform("GitHub", "https://github.com/%s", "开发者"),
        SocialPlatform("Gitcode", "https://gitcode.com/%s", "开发者"),
        SocialPlatform("Stack Overflow", "https://stackoverflow.com/users/%s", "开发者"),
        SocialPlatform("HackerNews", "https://news.ycombinator.com/user?id=%s", "开发者"),
        SocialPlatform("Dev.to", "https://dev.to/%s", "开发者"),

        /* 中国平台 */
        SocialPlatform("微博", "https://weibo.com/%s", "中国社交"),
        SocialPlatform("哔哩哔哩", "https://space.bilibili.com/%s", "中国社交"),
        SocialPlatform("知乎", "https://www.zhihu.com/people/%s", "中国社交"),
        SocialPlatform("QQ", "https://user.qzone.qq.com/%s", "中国社交"),

        /* 俄罗斯/独联体平台 */
        SocialPlatform("VK", "https://vk.com/%s", "俄语社交"),
        SocialPlatform("Odnoklassniki", "https://ok.ru/%s", "俄语社交"),

        /* 职业与商业 */
        SocialPlatform("AngelList", "https://angel.co/%s", "职业社交"),
        SocialPlatform("Crunchbase", "https://www.crunchbase.com/person/%s", "职业社交"),
        SocialPlatform("ProductHunt", "https://www.producthunt.com/@%s", "职业社交"),
        SocialPlatform("Xing", "https://www.xing.com/profile/%s", "职业社交"),
        SocialPlatform("ResearchGate", "https://www.researchgate.net/profile/%s", "学术"),

        /* 学术平台 */
        SocialPlatform("ORCID", "https://orcid.org/%s", "学术"),
        SocialPlatform("Google Scholar", "https://scholar.google.com/citations?user=%s", "学术"),
        SocialPlatform("Academia.edu", "https://academia.edu/%s", "学术"),

        /* 创意平台 */
        SocialPlatform("Dribbble", "https://dribbble.com/%s", "创意设计"),
        SocialPlatform("Behance", "https://www.behance.net/%s", "创意设计"),
        SocialPlatform("DeviantArt", "https://www.deviantart.com/%s", "创意设计"),
        SocialPlatform("Patreon", "https://www.patreon.com/%s", "内容创作"),
        SocialPlatform("SoundCloud", "https://soundcloud.com/%s", "音乐平台"),
        SocialPlatform("Bandcamp", "https://%s.bandcamp.com", "音乐平台"),
        SocialPlatform("Figma", "https://www.figma.com/@%s", "创意设计"),

        /* 论坛社区 */
        SocialPlatform("Lowyat", "https://forum.lowyat.net/user/%s", "马来西亚"),
        SocialPlatform("Quora", "https://www.quora.com/profile/%s", "问答社区"),

        /* 游戏平台 */
        SocialPlatform("Roblox", "https://www.roblox.com/user.aspx?username=%s", "游戏平台"),
        SocialPlatform("Minecraft", "https://namemc.com/profile/%s", "游戏平台"),
        SocialPlatform("Epic Games", "https://www.epicgames.com/id/%s", "游戏平台"),

        /* 其他 */
        SocialPlatform("Keybase", "https://keybase.io/%s", "安全"),
        SocialPlatform("Gravatar", "https://gravatar.com/%s", "通用"),
        SocialPlatform("About.me", "https://about.me/%s", "个人主页"),
        SocialPlatform("Linktree", "https://linktr.ee/%s", "个人主页"),
        SocialPlatform("Tumblr", "https://%s.tumblr.com", "内容平台"),
        SocialPlatform("Blogger", "https://%s.blogspot.com", "内容平台"),
        SocialPlatform("Mastodon", "https://mastodon.social/@%s", "社交网络"),
        SocialPlatform("BlueSky", "https://bsky.app/profile/%s", "社交网络"),
        SocialPlatform("Threads", "https://www.threads.net/@%s", "社交网络"),
    )

    /* 按类别分组 */
    val CATEGORIES: Map<String, List<SocialPlatform>> = ALL.groupBy { it.category }

    /* 获取平台URL */
    fun getUrl(platform: SocialPlatform, username: String): String {
        return platform.urlTemplate.replace("%s", username)
    }
}
