package com.example.data

import com.example.model.Component3DModel

/**
 * Community how-to videos from the repair-links pack.
 * Not factory workshop content. Vehicle year/config is not verified per clip.
 */
data class CommunityRepairVideo(
    val title: String,
    val url: String,
    val keywords: List<String>
)

object CommunityRepairVideos {
    const val DISCLAIMER =
        "Community video only. Not the Ford workshop manual. Confirm it matches this 2004 Sport Trac 4WD 4.0L VIN K before following it."

    val catalog: List<CommunityRepairVideo> = listOf(
        CommunityRepairVideo(
            "Upper control arm / ball joint",
            "https://www.youtube.com/watch?v=uSwpGu9JsQA",
            listOf("control arm", "ball joint", "uca", "upper arm")
        ),
        CommunityRepairVideo(
            "Front sway bar end link",
            "https://www.youtube.com/watch?v=xcfcMBpbuLc",
            listOf("sway", "end link", "stabilizer", "swaybar")
        ),
        CommunityRepairVideo(
            "Front sway bar end link (alt)",
            "https://www.youtube.com/watch?v=nQejtlnhlAA",
            listOf("sway", "end link", "stabilizer")
        ),
        CommunityRepairVideo(
            "Rear sway bar links and bushings",
            "https://www.youtube.com/watch?v=vy7T-OQ9oXo",
            listOf("rear sway", "sway", "bushing", "stabilizer")
        ),
        CommunityRepairVideo(
            "Front hub and bearing",
            "https://www.youtube.com/watch?v=kW0bAWGwRVg",
            listOf("hub", "bearing", "wheel hub", "knuckle")
        ),
        CommunityRepairVideo(
            "Shock absorber install",
            "https://www.youtube.com/watch?v=p4vI_S_Z3YI",
            listOf("shock", "strut", "damper")
        ),
        CommunityRepairVideo(
            "Front ABS wheel speed sensor",
            "https://www.youtube.com/watch?v=8N7YKWDZoDg",
            listOf("abs", "wheel speed", "speed sensor")
        ),
        CommunityRepairVideo(
            "Front brake pads and rotors",
            "https://www.youtube.com/watch?v=rC7tbywZgRE",
            listOf("brake", "pad", "rotor", "caliper", "front brake")
        ),
        CommunityRepairVideo(
            "Rear brakes",
            "https://www.youtube.com/watch?v=nZ8C68E_UqI",
            listOf("rear brake", "brake", "caliper", "pad", "rotor")
        ),
        CommunityRepairVideo(
            "Serpentine belt and tensioner",
            "https://www.youtube.com/watch?v=EMqwYGUpnqE",
            listOf("belt", "serpentine", "tensioner", "idler")
        ),
        CommunityRepairVideo(
            "Spark plugs and wires",
            "https://www.youtube.com/watch?v=H72W9m_Yp1I",
            listOf("spark", "plug", "ignition", "wire", "coil")
        ),
        CommunityRepairVideo(
            "Alternator swap",
            "https://www.youtube.com/watch?v=68Cl_0S_Z_g",
            listOf("alternator", "charging", "generator")
        ),
        CommunityRepairVideo(
            "Air filter and MAF cleaning",
            "https://www.youtube.com/watch?v=VwO3F2wP63I",
            listOf("air filter", "maf", "mass air")
        ),
        CommunityRepairVideo(
            "Window regulator / motor",
            "https://www.youtube.com/watch?v=ture5ryks-4",
            listOf("window", "regulator", "door glass")
        ),
        CommunityRepairVideo(
            "Headlight housing",
            "https://www.youtube.com/watch?v=NzMVcCsZ860",
            listOf("headlight", "headlamp", "head light")
        ),
        CommunityRepairVideo(
            "Tail light",
            "https://www.youtube.com/watch?v=l_vPhm8F9_Y",
            listOf("tail light", "taillight", "tail lamp")
        ),
        CommunityRepairVideo(
            "Tailgate cable",
            "https://www.youtube.com/watch?v=R90vI-0v_3g",
            listOf("tailgate", "gate cable")
        )
    )

    fun matching(query: String, component: Component3DModel? = null): List<CommunityRepairVideo> {
        val haystack = buildString {
            append(query.lowercase())
            if (component != null) {
                append(' ')
                append(component.name.lowercase())
                append(' ')
                append(component.id.lowercase())
                append(' ')
                append(component.system.displayName.lowercase())
            }
        }
        return catalog.filter { video ->
            video.keywords.any { keyword -> haystack.contains(keyword) }
        }.distinctBy { it.url }
    }

    fun format(videos: List<CommunityRepairVideo>, limit: Int = 3): String {
        if (videos.isEmpty()) {
            return "No packed community video matches that request. $DISCLAIMER"
        }
        return videos.take(limit).joinToString(" ") { video ->
            "${video.title}: ${video.url}."
        } + " $DISCLAIMER"
    }
}
