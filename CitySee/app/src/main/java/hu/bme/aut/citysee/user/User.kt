package hu.bme.aut.citysee.user

import hu.bme.aut.citysee.domain.model.Badge

class User {
    var points: Int = 0
    var level: Int = 1
    private val badges: MutableList<Badge> = mutableListOf()

    fun addPoints(points: Int){
        this.points += points
        checkLevelUp()
        checkForNewBadge()
    }

    private fun checkLevelUp(){
        if(this.points >= this.level * 100){
            levelUp()
        }
    }

    private fun levelUp(){
        this.level++
    }

    private fun checkForNewBadge() {
        val requiredBadges = this.points / 100
        while (badges.size < requiredBadges) {
            addBadge(Badge("Trophy ${badges.size + 1}", "Earned for reaching ${badges.size * 100} points"))
        }
    }

    fun addBadge(badge: Badge){
        badge.unlock()
        badges.add(badge)
    }

    fun getBadges(): List<Badge>{
        return badges
    }
}