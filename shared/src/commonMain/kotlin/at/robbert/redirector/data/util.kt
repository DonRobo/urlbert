package at.robbert.redirector.data

fun <T> List<T>.minusIndex(index: Int): List<T> {
    return this.filterIndexed { i, _ -> i != index }
}
