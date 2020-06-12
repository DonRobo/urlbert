package at.robbert.redirector.data

fun <T> List<T>.minusIndex(index: Int): List<T> {
    return this.filterIndexed { i, _ -> i != index }
}

fun <T> List<T>.set(index: Int, value: T): List<T> {
    require(index in this.indices)
    return this.mapIndexed { i, t -> if (i == index) value else t }
}

fun <T> List<T>.containsDuplicates(): Boolean = this.groupBy { it }.any { it.value.size != 1 }
