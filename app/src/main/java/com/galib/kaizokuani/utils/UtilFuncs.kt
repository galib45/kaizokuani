package com.galib.kaizokuani.utils

import android.app.Activity
import android.content.Context
import com.galib.kaizokuani.data.EpisodeLink

fun decodeSourceUrl(sourceUrl: String) : String {
    val map = mapOf(
        "01" to "9", "08" to "0", "05" to "=", "0a" to "2", "0b" to "3", "0c" to "4", "07" to "?",
        "00" to "8", "5c" to "d", "0f" to "7", "5e" to "f", "17" to "/", "54" to "l", "09" to "1",
        "48" to "p", "4f" to "w", "0e" to "6", "5b" to "c", "5d" to "e", "0d" to "5", "53" to "k",
        "1e" to "&", "5a" to "b", "59" to "a", "4a" to "r", "4c" to "t", "4e" to "v", "57" to "o",
        "51" to "i"
    )
    val decoded = StringBuilder()
    for (i in 0 until sourceUrl.length step 2) {
        val chunk = sourceUrl.substring(i, i+2)
        decoded.append(map[chunk] ?: "")
    }

    return decoded.toString().replace("/clock", "/clock.json")
}

fun extractWixmpLinks(link: String) : List<EpisodeLink> {
    // https://repackager.wixmp.com/video.wixstatic.com/video/b42737_be840c03c1904e0c9d71a2a8eeadd088/,1080p,480p,720p,/mp4/file.mp4.urlset/master.m3u8
    // -> https://video.wixstatic.com/video/b42737_be840c03c1904e0c9d71a2a8eeadd088/720p/mp4/file.mp4
    var result = mutableListOf<EpisodeLink>()
    val extracted = link.replace("repackager.wixmp.com/", "")
    val splits = extracted.split(",")
    val start = splits.first()
    val end = splits.last().split(".urlset").first()
    val resolutionList = splits.drop(1).dropLast(1)
    resolutionList.forEach {
        result.add(EpisodeLink("$start$it$end", it))
    }
    return result
}

fun downloadFile(context: Context, url: String, filename: String) {
    Util1DM.downloadFile(
        (context as? Activity)!!,
        url,
        referer = null,
        fileName = filename,
        userAgent = null,
        cookies = null,
        secureUri = true,
        askUserToInstall1DMIfNotInstalled = true
    )
}

