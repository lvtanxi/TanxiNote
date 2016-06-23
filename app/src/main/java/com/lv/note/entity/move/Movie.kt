package com.lv.note.entity.move

/**
 * User: 吕勇
 * Date: 2016-06-22
 * Time: 09:30
 * Description:
 */
class Movie {
    var rating: Rating? = null
    var title = ""
    var collect_count: Int = 0
    var original_title = ""
    var subtype = ""
    var year = ""
    var images: Avatars? = null
    var alt = ""
    var id = ""
    var genres: List<String>? = null
    var casts: List<Directors>? = null
    var directors: List<Directors>? = null

}
